package com.atlassian.performance.tools.release.repositories

import net.linguica.gradle.maven.settings.LocalMavenSettingsLoader
import net.linguica.gradle.maven.settings.MavenSettingsPluginExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.repositories
import java.net.URI

class RepositoryConfigurator(
    private val project: Project
) {
    fun configureAtlassianRepositories(private: Boolean): PublishingRepositories {
        return if (private) {
            configurePrivateAtlassianRepositories()
        } else {
            configurePublicAtlassianRepositories()
        }
    }

    private fun configurePrivateAtlassianRepositories(): PublishingRepositories {
        var main: MavenArtifactRepository? = null

        project.repositories {
            mavenLocal()

            main = atlassianRepository {
                name = "atlassian-private"
                url = URI("https://maven.atlassian.com/private/")
            }
        }

        return PublishingRepositories(main = main!!)
    }

    private fun configurePublicAtlassianRepositories(): PublishingRepositories {
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
            findCredentials(repo = this)?.let { staticCredentials ->
                credentials {
                    username = staticCredentials.username
                    password = staticCredentials.password
                }
            }
        }
    }

    private fun findCredentials(
        repo: MavenArtifactRepository
    ): StaticPasswordCredentials? = atlassianCredentialsFromEnv() ?: mavenCredentials(repo)

    private fun atlassianCredentialsFromEnv(): StaticPasswordCredentials? {
        val envUsername: String? = System.getenv("atlassian_private_username")
        val envPassword: String? = System.getenv("atlassian_private_password")
        return if (envUsername == null || envPassword == null) {
            null
        } else {
            StaticPasswordCredentials(
                username = envUsername,
                password = envPassword
            )
        }
    }

    private fun mavenCredentials(
        repo: MavenArtifactRepository
    ): StaticPasswordCredentials? {
        val settings = LocalMavenSettingsLoader(MavenSettingsPluginExtension(project)).loadSettings()
        val server = settings.getServer(repo.name) ?: return null
        return StaticPasswordCredentials(
            username = server.username,
            password = server.password
        )
    }
}