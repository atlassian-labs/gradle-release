package com.atlassian.performance.tools.release.repositories

import org.gradle.api.artifacts.repositories.ArtifactRepository

data class PublishingRepositories(
    val main: ArtifactRepository
)