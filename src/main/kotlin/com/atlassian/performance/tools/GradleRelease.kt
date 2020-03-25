package com.atlassian.performance.tools

import com.atlassian.performance.tools.license.VerifyLicensingTask
import com.atlassian.performance.tools.release.PublishingConfigurator
import com.atlassian.performance.tools.release.ReleaseConfigurator
import com.atlassian.performance.tools.release.javadoc.JavadocConfigurator
import com.atlassian.performance.tools.release.repositories.RepositoryConfigurator
import com.atlassian.performance.tools.release.source.SourceConfigurator
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

@Suppress("unused")
fun Project.gradleRelease(configure: GradleReleaseConfig.() -> Unit): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("gradleRelease", configure)

@Suppress("unused")
open class GradleRelease : Plugin<Project> {

    override fun apply(project: Project) {
        val gradleReleaseConfig = project.extensions.create<GradleReleaseConfig>("gradleRelease")
        val privateMode = gradleReleaseConfig.atlassianPrivateMode

        val publishingRepositories = RepositoryConfigurator(project).configureAtlassianRepositories(privateMode)
        val scmVersion = ReleaseConfigurator(project).configureRelease()
        val source = SourceConfigurator(project).configureSources()
        val javadoc = JavadocConfigurator(project).configureJavadoc()

        val verifyLicense = project.tasks.create("verifyLicensing", VerifyLicensingTask::class.java)
        verifyLicense.group = "Release"
        verifyLicense.description = "Verifies if our dependencies use only allowed licenses"
        val check = project.getTasksByName("check", false)
        check.forEach { it.dependsOn(verifyLicense) }

        PublishingConfigurator(
            project = project,
            scmVersion = scmVersion
        ).configurePublishing(
            publishingRepositories = publishingRepositories,
            component = project.components["java"],
            source = source,
            javadoc = javadoc
        )
    }
}