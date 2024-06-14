package com.atlassian.performance.tools.license

import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory

class PublishTest {

    private val projectName = "publish-test"

    @Before
    fun cleanUp() {
        findLocalPublication().toFile()
            .takeIf { it.exists() }
            ?.deleteRecursively()
    }

    @Test
    fun shouldPublishModule() {
        val version = "0.3.0"
        val project = Fixtures().configureProject(projectName, version)
        val realisticLookingRemote = URIish("git@github.com:atlassian/jira-actions.git") // not actually pushed to
        project.git.addOrigin(realisticLookingRemote)

        val publishResult = GradleRunner.create()
            .withProjectDir(project.root)
            .withArguments("publishToMavenLocal", "--stacktrace")
            .withPluginClasspath()
            .forwardOutput()
            .withDebug(true)
            .build()

        val publishTask = publishResult.task(":publishToMavenLocal")
        assertThat(publishTask?.outcome)
            .isEqualTo(TaskOutcome.SUCCESS)
        val pomXml = findPublishedPom("$version-SNAPSHOT")
        assertThat(pomXml).exists()
        val scm = extractScm(pomXml)
        assertThat(scm.getChildValue("url"))
            .`as`("SCM url in $pomXml")
            .isEqualTo("https://github.com/atlassian/publish-test")
        assertThat(scm.getChildValue("connection"))
            .`as`("SCM connection in $pomXml")
            .isEqualTo("scm:git:git@github.com:atlassian/publish-test.git")
        assertThat(scm.getChildValue("developerConnection"))
            .`as`("SCM dev connection in $pomXml")
            .isEqualTo("scm:git:git@github.com:atlassian/publish-test.git")
    }

    private fun extractScm(
        pomXml: File
    ): Node {
        val pom: Document = pomXml
            .inputStream()
            .buffered()
            .use { DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(it) }
        return pom
            .getElementsByTagName("scm")
            .toIterable()
            .single()
    }

    private fun findPublishedPom(
        publishedVersion: String
    ): File = findLocalPublication()
        .resolve(publishedVersion)
        .resolve("$projectName-$publishedVersion.pom")
        .toFile()

    private fun findLocalPublication(): Path = Paths.get(System.getProperty("user.home"))
        .resolve(".m2")
        .resolve("repository")
        .resolve("com")
        .resolve("atlassian")
        .resolve("performance")
        .resolve("tools")
        .resolve(projectName)

    private fun NodeList.toIterable(): Iterable<Node> = object : Iterable<Node> {
        override fun iterator(): Iterator<Node> {
            return object : Iterator<Node> {
                private var index = 0
                override fun hasNext(): Boolean = index < this@toIterable.length
                override fun next(): Node = this@toIterable.item(index++)
            }
        }
    }

    private fun Node.getChildValue(
        childName: String
    ): String? = childNodes
        .toIterable()
        .singleOrNull { it.nodeName == childName }
        ?.textContent
}
