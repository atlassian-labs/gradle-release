package com.atlassian.performance.tools.license

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class VerifyLicensingTaskTest {

    @JvmField
    @Rule
    val temp = TemporaryFolder()

    @Test
    fun shouldFailAMissingLicense() {
        val illegalLicenses = """
            plugins {
                id("com.atlassian.performance.tools.gradle-release")
                id("java-library")
            }

            dependencies {
                api("net.jcip:jcip-annotations:1.0")
            }
            """.trimIndent()
        val result = check(illegalLicenses).buildAndFail()

        assertThat(result.task(":verifyLicensing")?.outcome).isEqualTo(TaskOutcome.FAILED)
        assertThat(result.output).contains("'No license found' in 'net.jcip:jcip-annotations:1.0'")
    }

    private fun check(build: String): GradleRunner {
        temp.newFile("build.gradle.kts").writeText(build)
        return GradleRunner.create()
            .withProjectDir(temp.root)
            .withArguments(":check")
            .withPluginClasspath()
            .withGradleVersion("6.7")
            .withDebug(true)
    }

    @Test
    fun shouldPassWhenLegal() {
        val legalLicenses = """
            plugins {
                id("com.atlassian.performance.tools.gradle-release")
                id("java-library")
            }

            dependencies {
                api("com.github.stephenc.jcip:jcip-annotations:1.0-1")
                compile("com.amazonaws:aws-java-sdk-iam:1.11.298")
                compile("org.codehaus.mojo:animal-sniffer-annotations:1.14")
                compile("org.jsoup:jsoup:1.10.2")
                compile("org.eclipse.jgit:org.eclipse.jgit:4.11.0.201803080745-r")
                compile("org.hamcrest:hamcrest-core:1.3")
                compile("joda-time:joda-time:2.8.1")
                compile("com.squareup.okio:okio:1.13.0")
                compile("io.github.bonigarcia:webdrivermanager:1.7.1")
                implementation("com.atlassian.data:random-data:1.4.3")
                implementation("org.apache.logging.log4j:log4j-api:2.23.1")
            }
            """.trimIndent()

        val result = check(legalLicenses).build()

        assertThat(result.task(":verifyLicensing")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }
}
