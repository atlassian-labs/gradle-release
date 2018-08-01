package com.atlassian.performance.tools.release.repositories

import net.linguica.gradle.maven.settings.LocalMavenSettingsLoader
import net.linguica.gradle.maven.settings.MavenSettingsPluginExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.kotlin.dsl.repositories
import java.net.URI

class RepositoryConfigurator(
    private val project: Project
) {
    fun configureAtlassianRepositories(): PublishingRepositories {
        var main: MavenArtifactRepository? = null
        var snapshot: MavenArtifactRepository? = null

        project.repositories {
            mavenLocal()
            mavenCentral()

            maven {
                name = "atlassian-public"
                url = URI("https://packages.atlassian.com/maven-external/")
            }
            main = atlassianPrivateRepository {
                name = "atlassian-private"
                url = URI("https://packages.atlassian.com/maven-private/")
            }
            snapshot = atlassianPrivateRepository {
                name = "atlassian-private-snapshot"
                url = URI("https://packages.atlassian.com/maven-private-snapshot")
            }
        }

        return PublishingRepositories(main = main!!, snapshot = snapshot!!)
    }

    private fun RepositoryHandler.atlassianPrivateRepository(
        configuration: MavenArtifactRepository.() -> Unit
    ): MavenArtifactRepository {
        return maven {
            configuration()
            credentials(findCredentials())
        }
    }

    private fun MavenArtifactRepository.findCredentials(): Action<in PasswordCredentials> {
        return atlassianPrivateFromEnv()
            ?: mavenCredentials()
            ?: throw Exception("Maven settings for '$name' are missing")
    }

    private fun atlassianPrivateFromEnv(): Action<in PasswordCredentials>? {
        val envUsername = System.getenv("atlassian_private_username")
        val envPassword = System.getenv("atlassian_private_password")
        if (envUsername == null || envPassword == null) {
            return null
        }
        return Action {
            username = envUsername
            password = envPassword
        }
    }

    private fun MavenArtifactRepository.mavenCredentials(): Action<in PasswordCredentials>? {
        val settings = LocalMavenSettingsLoader(MavenSettingsPluginExtension(project)).loadSettings()
        val server = settings.getServer(name) ?: return null

        return Action {
            username = server.username
            password = server.password
        }
    }
}