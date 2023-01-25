package com.atlassian.performance.tools.release

import com.atlassian.performance.tools.SigningKeyInformationTask
import com.atlassian.performance.tools.release.javadoc.Javadoc
import com.atlassian.performance.tools.release.repositories.PublishingRepositories
import com.atlassian.performance.tools.release.source.Source
import org.eclipse.jgit.api.Git
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPomScm
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import pl.allegro.tech.build.axion.release.domain.VersionConfig

class PublishingConfigurator(
    private val project: Project,
    private val scmVersion: VersionConfig
) {
    fun configurePublishing(
        publishingRepositories: PublishingRepositories,
        component: SoftwareComponent,
        source: Source,
        javadoc: Javadoc
    ) {
        project.plugins.apply("org.gradle.maven-publish")
        val publishing = project.extensions[PublishingExtension.NAME] as org.gradle.api.publish.PublishingExtension

        publishing.publications.register("mavenJava", MavenPublication::class.java) {
            pom {
                scm {
                    includeScm()
                }

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0")
                        distribution.set("repo")
                    }
                }
            }
            from(component)
            artifact(source.jar)
            artifact(javadoc.jar)
        }
        val signingExtension = SigningExtension(project)
        signingExtension.sign(publishing.publications)
        val gatherKeyInformation = project.tasks.create("gatherKeyInformation", SigningKeyInformationTask::class.java)
        gatherKeyInformation.group = "Release"
        gatherKeyInformation.description = "Gathers key information"
        val signMavenJavaPublication = project.getTasksByName("signMavenJavaPublication", false)
        signMavenJavaPublication.forEach { it.dependsOn(gatherKeyInformation) }
        project.tasks.withType(Sign::class.java) {
            onlyIf { project.extra.has(gatherKeyInformation.signingKeyId) }
        }
        includePom()

        if (scmVersion.version.endsWith("SNAPSHOT").not()) {
            publishing.repositories.add(publishingRepositories.main)
        }
    }

    private fun MavenPomScm.includeScm() {
        val remoteHost: String? = scmVersion
            .repository
            .directory
            .asFile
            .get()
            .resolve(".git")
            .let { if (it.exists()) it else null }
            ?.let { Git.open(it) }
            ?.use { repo ->
                repo
                    .remoteList()
                    .call()
                    .singleOrNull { it.name == "origin" }
                    ?.urIs
                    ?.single()
                    ?.host
            }
        val projectName = project.name
        when (remoteHost) {
            null -> {
                // we're fine with a missing SCM, because this is evaluated eagerly, ie. even if there is no publication
                // a publication without a Git repo will still fail when the Axion plugin will try to push a tag
            }
            "bitbucket.org" -> {
                url.set("https://bitbucket.org/atlassian/$projectName")
                connection.set("scm:git:git@bitbucket.org:atlassian/$projectName.git")
                developerConnection.set("scm:git:git@bitbucket.org:atlassian/$projectName.git")
            }
            "github.com" -> {
                url.set("https://github.com/atlassian/$projectName")
                connection.set("scm:git:git@github.com:atlassian/$projectName.git")
                developerConnection.set("scm:git:git@github.com:atlassian/$projectName.git")
            }
            else -> throw Exception("Cannot infer POM SCM section from $remoteHost")
        }
    }

    private fun includePom() {
        project.tasks.getByName<Jar>("jar") {
            into("META-INF/maven/${project.group}/${project.name}") {
                rename(".*", "pom.xml")
                from(project.tasks.withType(GenerateMavenPom::class.java).single())
            }
            into("META-INF") {
                from(".")
                include("LICENSE.txt")
                include("NOTICE.txt")
            }
        }
    }
}
