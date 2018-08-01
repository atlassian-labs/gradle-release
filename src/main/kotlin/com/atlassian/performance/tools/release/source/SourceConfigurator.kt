package com.atlassian.performance.tools.release.source

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getPlugin

class SourceConfigurator(
    private val project: Project
) {
    fun configureSources(): Source {
        project.plugins.apply("java")
        val java = project.convention.getPlugin(JavaPluginConvention::class)

        java.sourceCompatibility = JavaVersion.VERSION_1_8
        java.targetCompatibility = JavaVersion.VERSION_1_8

        return Source(
            project.tasks.create("sourcesJar", Jar::class.java) {
                classifier = "sources"
                from(java.sourceSets["main"].allSource)
            }
        )
    }

}