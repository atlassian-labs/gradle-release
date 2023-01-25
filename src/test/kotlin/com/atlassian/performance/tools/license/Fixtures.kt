package com.atlassian.performance.tools.license

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish
import org.junit.rules.TemporaryFolder
import java.io.File

class Fixtures {

    class ProjectFixture(
        val root: File,
        val git: Git
    )

    internal fun configureProject(
        projectName: String,
        version: String
    ): ProjectFixture {
        val buildFolder = TemporaryFolder()
        buildFolder.create()
        val git = Git.init().setDirectory(buildFolder.root).call()
        addInitialCommit(projectName, buildFolder, git)
        git.tag().setName("release-$version-alpha").call()
        addCode(buildFolder, git)
        return ProjectFixture(buildFolder.root, git)
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
}

internal fun Git.addRemote(uri: URIish) {
    remoteAdd()
        .apply { setName("origin") }
        .apply { setUri(uri) }
        .call()
}