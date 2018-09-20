package com.atlassian.performance.tools.release.repositories

import net.linguica.gradle.maven.settings.LocalMavenSettingsLoader
import net.linguica.gradle.maven.settings.MavenSettingsPluginExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.internal.artifacts.repositories.DefaultPasswordCredentials
import org.gradle.kotlin.dsl.repositories
import java.net.URI

class RepositoryConfigurator(
    private val project: Project
) {
    fun configureAtlassianRepositories(): PublishingRepositories {
        var main: MavenArtifactRepository? = null

        project.repositories {
            mavenLocal()
            mavenCentral()

            maven {
                name = "atlassian-external"
                url = URI("https://packages.atlassian.com/maven-external/")
            }

            main = atlassianRepository {
                name = "atlassian-public"
                url = URI("https://maven.atlassian.com/public/")
            }
        }

        return PublishingRepositories(main = main!!)
    }

    private fun RepositoryHandler.atlassianRepository(
        configuration: MavenArtifactRepository.() -> Unit
    ): MavenArtifactRepository {
        return maven {
            configuration()
            findCredentials(repo = this)?.let { creds ->
                credentials {
                    username = creds.username
                    password = creds.password
                }
            }
        }
    }

    private fun findCredentials(
        repo: MavenArtifactRepository
    ): PasswordCredentials? = atlassianCredentialsFromEnv() ?: mavenCredentials(repo)

    private fun atlassianCredentialsFromEnv(): PasswordCredentials? {
        val envUsername: String? = System.getenv("atlassian_private_username")
        val envPassword: String? = System.getenv("atlassian_private_password")
        return if (envUsername == null || envPassword == null) {
            null
        } else {
            DefaultPasswordCredentials(
                envUsername,
                envPassword
            )
        }
    }

    private fun mavenCredentials(
        repo: MavenArtifactRepository
    ): PasswordCredentials? {
        val settings = LocalMavenSettingsLoader(MavenSettingsPluginExtension(project)).loadSettings()
        val server = settings.getServer(repo.name) ?: return null
        return DefaultPasswordCredentials(
            server.username,
            server.password
        )
    }
}