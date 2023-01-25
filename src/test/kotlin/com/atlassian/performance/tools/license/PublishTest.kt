package com.atlassian.performance.tools.license

import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory

class PublishTest {


    @Test
    fun shouldPublishModule() {
        val projectName = "release-test"
        val version = "0.3.0"
        val project = configureProject(projectName, version)

        val publishResult = GradleRunner.create()
            .withProjectDir(project)
            .withArguments("publishToMavenLocal", "--stacktrace")
            .withPluginClasspath()
            .forwardOutput()
            .withDebug(true)
            .build()

        val publishTask = publishResult.task(":publishToMavenLocal")
        assertThat(publishTask?.outcome)
            .isEqualTo(TaskOutcome.SUCCESS)
        val pomXml = findPublishedPom(projectName, "$version-SNAPSHOT")
        assertThat(pomXml).exists()
        val scm = extractScm(pomXml)
        assertThat(scm.getChildValue("url"))
            .`as`("SCM url in $pomXml")
            .isEqualTo("https://github.com/atlassian/release-test")
        assertThat(scm.getChildValue("connection"))
            .`as`("SCM connection in $pomXml")
            .isEqualTo("scm:git:git@github.com:atlassian/release-test.git")
        assertThat(scm.getChildValue("developerConnection"))
            .`as`("SCM dev connection in $pomXml")
            .isEqualTo("scm:git:git@github.com:atlassian/release-test.git")
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

    private fun configureProject(
        projectName: String,
        version: String
    ): File {
        val buildFolder = TemporaryFolder()
        buildFolder.create()
        val git = Git.init().setDirectory(buildFolder.root).call()
        addInitialCommit(projectName, buildFolder, git)
        git.tag().setName("release-$version-alpha").call()
        addCode(buildFolder, git)
        addRemote(git)
        return buildFolder.root
    }

    private fun addInitialCommit(
        projectName: String,
        buildFolder: TemporaryFolder,
        git: Git
    ) {
        buildFolder.newFile("build.gradle").writeText(
            """
            plugins {
                id 'com.atlassian.performance.tools.gradle-release'
                id "org.jetbrains.kotlin.jvm" version "1.3.20"
            }
            dependencies {
                compile group: 'org.jetbrains.kotlin', name: 'kotlin-stdlib-jdk8', version: '1.3.20'
            }
            """.trimIndent()
        )
        buildFolder.newFile("settings.gradle").writeText(
            """
            rootProject.name = "$projectName"
            """.trimIndent()
        )
        buildFolder.newFile(".gitignore").writeText(
            """
            .gradle
            build
            """.trimIndent()
        )
        git
            .add()
            .addFilepattern(".gitignore")
            .addFilepattern("settings.gradle")
            .addFilepattern("build.gradle")
            .call()
        git
            .commit()
            .setMessage("Initial commit")
            .call()
    }

    private fun addCode(
        buildFolder: TemporaryFolder,
        git: Git
    ) {
        val javaFolder = buildFolder.newFolder("src", "main", "kotlin")
        File(javaFolder, "Hello.kt").writeText(
            """
            class Hello {
            }
            """.trimIndent()
        )
        git
            .add()
            .addFilepattern("src")
            .call()
        git
            .commit()
            .setMessage("Hello world")
            .call()
    }

    private fun addRemote(
        git: Git
    ) {
        git
            .remoteAdd()
            .also { it.setName("origin") }
            .also { it.setUri(URIish("git@github.com:atlassian/jira-actions.git")) }
            .call()
    }

    private fun findPublishedPom(
        projectName: String,
        publishedVersion: String
    ): File = Paths.get(System.getProperty("user.home"))
        .resolve(".m2")
        .resolve("repository")
        .resolve("com")
        .resolve("atlassian")
        .resolve("performance")
        .resolve("tools")
        .resolve(projectName)
        .resolve(publishedVersion)
        .resolve("$projectName-$publishedVersion.pom")
        .toFile()

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
