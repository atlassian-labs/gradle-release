package com.atlassian.performance.tools

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.extra
import java.lang.System.getenv

open class SigningKeyInformationTask : DefaultTask() {
    @Input
    val signingKeyId = "signing.keyId"

    @Input
    val signingSecretKeyRingFile = "signing.secretKeyRingFile"

    @Input
    val signingPassword = "signing.password"

    @TaskAction
    @Suppress("unused")
    fun gatherKeyInformation() {
        configureProperty(signingKeyId)
        configureProperty(signingSecretKeyRingFile)
        configureProperty(signingPassword)
    }

    private fun configureProperty(property: String) {
        val propertyValue = getenv(property)
        if (!project.extra.has(property) && propertyValue != null) {
            project.extra.set(property, propertyValue)
        }
    }
}