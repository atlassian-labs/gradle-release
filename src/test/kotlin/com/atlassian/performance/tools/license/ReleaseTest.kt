package com.atlassian.performance.tools.license

import com.atlassian.performance.tools.license.Fixtures.ProjectFixture
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ReleaseTest {

    @Test
    fun shouldReleaseModule() {
        val projectName = "release-test"
        val version = "1.2.4"
        val project = Fixtures().configureProject(projectName, version)
        val safelyPushableRemote = TemporaryFolder()
            .also { it.create() }
            .root
            .also { Git.init().setDirectory(it).call() }
            .let { URIish("file://${it.absolutePath}") }
        project.git.addOrigin(safelyPushableRemote)
        project.git.push().call()

        val markNextVersion = project.buildTask(
            "markNextVersion",
            "-Prelease.incrementer=incrementPatch",
            "-Prelease.localOnly",
            "--stacktrace"
        )
        assertThat(markNextVersion?.task(":markNextVersion")?.outcome).isEqualTo(SUCCESS)

        val release = project.buildTask("release", "--stacktrace")
        assertThat(release?.task(":release")?.outcome).isEqualTo(SUCCESS)

        val currentVersion = project.buildTask("currentVersion")
        assertThat(currentVersion?.output).contains("1.2.5")
    }

    private fun ProjectFixture.buildTask(task: String, vararg args: String): BuildResult? {
        return GradleRunner.create()
            .withProjectDir(this.root)
            .withArguments(task, *args)
            .withPluginClasspath()
            .forwardOutput()
            .withDebug(true)
            .build()
    }
}
