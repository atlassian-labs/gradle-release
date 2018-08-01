package com.atlassian.performance.tools.release.javadoc

import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.getByName
import org.jetbrains.dokka.gradle.DokkaTask

class JavadocConfigurator(
    private val project: Project
) {
    fun configureJavadoc(): Javadoc {

        project.plugins.apply("org.jetbrains.dokka")

        val dokka = project.tasks.getByName<DokkaTask>("dokka") {
            outputFormat = "html"
            outputDirectory = "${project.buildDir}/javadoc"

            reportUndocumented = false
        }

        val javadocJar = project.tasks.create("javadocJar", Jar::class.java) {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            classifier = "javadoc"
            from(dokka)
        }

        return Javadoc(javadocJar)
    }
}
