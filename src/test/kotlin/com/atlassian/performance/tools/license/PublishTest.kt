package com.atlassian.performance.tools.license

import org.eclipse.jgit.api.Git
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class PublishTest {

    @Test
    fun shouldPublishModule() {
        val project = configureProject()
        GradleRunner.create()
            .withProjectDir(project)
            .withArguments("release")
            .withPluginClasspath()
            .forwardOutput()
            .withDebug(true)
            .build()

        val publishResult = GradleRunner.create()
            .withProjectDir(project)
            .withArguments("publishToMavenLocal")
            .withPluginClasspath()
            .forwardOutput()
            .withDebug(true)
            .build()

        val publishTask = publishResult.task(":publishToMavenLocal")!!
        Assert.assertEquals(TaskOutcome.SUCCESS, publishTask.outcome)
    }

    private fun configureProject(): File {
        val buildFolder = TemporaryFolder()
        buildFolder.create()

        val buildGradle = buildFolder.newFile("build.gradle")
        buildGradle.writeText("""
            plugins {
                id 'com.atlassian.performance.tools.gradle-release'
                id "org.jetbrains.kotlin.jvm" version "1.2.61"
            }
            dependencies {
                compile group: 'org.jetbrains.kotlin', name: 'kotlin-stdlib-jdk8', version: '1.2.61'
            }
        """.trimIndent())

        val settings = buildFolder.newFile("settings.gradle")
        settings.writeText("""
            rootProject.name = "release-test"
        """.trimIndent())

        val gitignore = buildFolder.newFile(".gitignore")
        gitignore.writeText("""
            .gradle
            build
        """.trimIndent())

        val javaFolder = buildFolder
            .newFolder("src", "main", "kotlin")

        val javaClass = File(javaFolder, "Hello.kt")
        javaClass.writeText("""
            class Hello {
            }
        """.trimIndent())

        val git = Git
            .init()
            .setDirectory(buildFolder.root)
            .call()

        git
            .add()
            .addFilepattern(".gitignore")
            .addFilepattern("settings.gradle")
            .addFilepattern("build.gradle")
            .addFilepattern("src")
            .call()
        git
            .commit()
            .setMessage("message")
            .call()

        git
            .tag()
            .setName("release-0.3.0-alpha")
            .call()

        return buildFolder.root
    }
}