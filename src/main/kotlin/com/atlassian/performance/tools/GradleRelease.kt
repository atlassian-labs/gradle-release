package com.atlassian.performance.tools

import com.atlassian.performance.tools.release.PublishingConfigurator
import com.atlassian.performance.tools.release.ReleaseConfigurator
import com.atlassian.performance.tools.release.javadoc.JavadocConfigurator
import com.atlassian.performance.tools.release.repositories.RepositoryConfigurator
import com.atlassian.performance.tools.release.source.SourceConfigurator
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

@Suppress("unused")
open class GradleRelease : Plugin<Project> {

    override fun apply(project: Project) {

        val publishingRepositories = RepositoryConfigurator(project).configureAtlassianRepositories()
        val scmVersion = ReleaseConfigurator(project).configureRelease()
        val source = SourceConfigurator(project).configureSources()
        val javadoc = JavadocConfigurator(project).configureJavadoc()

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