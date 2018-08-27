package com.atlassian.performance.tools.release

import com.atlassian.performance.tools.release.javadoc.Javadoc
import com.atlassian.performance.tools.release.repositories.PublishingRepositories
import com.atlassian.performance.tools.release.source.Source
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
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

        publishing.publications.create("mavenJava", MavenPublication::class.java) {
            pom {
                scm {
                    url.set("https://bitbucket.org/atlassian/${project.name}")
                    connection.set("scm:git:git@bitbucket.org:atlassian/${project.name}.git")
                    developerConnection.set("scm:git:git@bitbucket.org:atlassian/${project.name}.git")
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

        includePom()

        if (scmVersion.version.endsWith("SNAPSHOT").not()) {
            publishing.repositories.add(publishingRepositories.main)
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
