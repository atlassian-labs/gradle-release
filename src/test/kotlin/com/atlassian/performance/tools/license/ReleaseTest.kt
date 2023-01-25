package com.atlassian.performance.tools.license

import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
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

        val releaseResult = GradleRunner.create()
            .withProjectDir(project.root)
            .withArguments("release", "--stacktrace")
            .withPluginClasspath()
            .forwardOutput()
            .withDebug(true)
            .build()

        val releaseTask = releaseResult.task(":release")
        assertThat(releaseTask?.outcome)
            .isEqualTo(TaskOutcome.SUCCESS)
    }
}
