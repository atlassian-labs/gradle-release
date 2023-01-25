package com.atlassian.performance.tools.release

import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import pl.allegro.tech.build.axion.release.ReleasePlugin
import pl.allegro.tech.build.axion.release.domain.VersionConfig

class ReleaseConfigurator(
    private val project: Project
) {
    fun configureRelease(): VersionConfig {
        project.plugins.apply("pl.allegro.tech.build.axion-release")
        val scmVersion = project.extensions[ReleasePlugin.VERSION_EXTENSION] as VersionConfig

        scmVersion.tag.prefix.set("release-")
        project.version = scmVersion.version
        project.group = "com.atlassian.performance.tools"

        project.tasks.getByName("release").doFirst {
            if (scmVersion.scmPosition.branch != "master") {
                throw Exception("Releasing allowed only on master branch")
            }
        }

        return scmVersion
    }
}