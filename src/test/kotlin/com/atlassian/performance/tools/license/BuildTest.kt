package com.atlassian.performance.tools.license

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class BuildTest {

    @Test
    fun shouldExecuteTestTask() {
        val result = GradleRunner.create()
            .withProjectDir(configureBuildGradle())
            .withArguments("build", "--stacktrace")
            .withPluginClasspath()
            .withDebug(true)
            .build()

        assertEquals(SUCCESS, result.task(":test")!!.outcome)
    }

    private fun configureBuildGradle(): File {
        val buildFolder = TemporaryFolder()
        buildFolder.create()
        val buildGradle = buildFolder.newFile("build.gradle")
        buildGradle.writeText(
            """
            plugins {
                id 'com.atlassian.performance.tools.gradle-release'
                id "org.jetbrains.kotlin.jvm" version "1.3.20"
            }

            dependencies {
                compile 'org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.20'
                testCompile 'junit:junit:4.12'
            }
        """.trimIndent()
        )
        val javaFolder = buildFolder
            .newFolder("src", "test", "kotlin")

        val javaClass = File(javaFolder, "Hello.kt")
        javaClass.writeText("""
            class HelloTest {
                @org.junit.Test
                fun test() {
                }
            }
        """.trimIndent())

        return buildFolder.root
    }
}