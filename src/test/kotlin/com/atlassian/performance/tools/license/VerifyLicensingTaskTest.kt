package com.atlassian.performance.tools.license

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class VerifyLicensingTaskTest {

    @Test
    fun shouldFailAMissingLicense() {
        val result = GradleRunner.create()
                .withProjectDir(configureBuildGradle())
                .withArguments(":check")
                .withPluginClasspath()
                .withGradleVersion("6.7")
                .withDebug(true)
                .buildAndFail()

        assertThat(result.task(":verifyLicensing")?.outcome).isEqualTo(TaskOutcome.FAILED)
        assertThat(result.output).contains("'No license found' in 'net.jcip:jcip-annotations:1.0'")
    }

    private fun configureBuildGradle(): File {
        val buildFolder = TemporaryFolder()
        buildFolder.create()
        val buildGradle = buildFolder.newFile("build.gradle")
        buildGradle.writeText("""
            plugins {
                id 'com.atlassian.performance.tools.gradle-release'
                id 'java-library'
            }

            dependencies {
                api 'net.jcip:jcip-annotations:1.0'
                compile 'com.amazonaws:aws-java-sdk-iam:1.11.298'
                compile 'org.codehaus.mojo:animal-sniffer-annotations:1.14'
                compile 'org.jsoup:jsoup:1.10.2'
                compile 'org.eclipse.jgit:org.eclipse.jgit:4.11.0.201803080745-r'
                compile 'org.hamcrest:hamcrest-core:1.3'
                compile 'joda-time:joda-time:2.8.1'
                compile 'com.squareup.okio:okio:1.13.0'
                compile 'io.github.bonigarcia:webdrivermanager:1.7.1'
            }
        """.trimIndent())
        return buildFolder.root
    }
}
