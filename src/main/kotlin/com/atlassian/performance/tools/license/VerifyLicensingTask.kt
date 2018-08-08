package com.atlassian.performance.tools.license

import nl.javadude.gradle.plugins.license.DependencyMetadata
import nl.javadude.gradle.plugins.license.LicenseResolver
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class VerifyLicensingTask : DefaultTask() {
    private val allowedLicenses = setOf(
        "The Apache Software License, Version 2.0",
        "Apache License, Version 2.0",
        "Apache 2",
        "Apache 2.0",
        "The Apache License, Version 2.0",
        "MIT license",
        "MIT License",
        "The MIT License",
        "Revised BSD",
        "Eclipse Distribution License (New BSD License)",
        "New BSD License",
        "GNU Lesser General Public License"
    )

    @TaskAction
    @Suppress("unused")
    fun verifyLicenses() {
        val dependencies = resolveDependencies()
        getEmptyLicenseMessages(dependencies).forEach { logger.warn(it) }
        val illegalLicenseMessages = getIllegalLicenseMessages(dependencies)
        if (illegalLicenseMessages.isNotEmpty()) {
            illegalLicenseMessages.forEach { logger.error(it) }
            throw Exception("Illegal license found")
        }
    }

    private fun getIllegalLicenseMessages(dependencies: Set<DependencyMetadata>): List<String> {
        return dependencies
            .filter(DependencyMetadata::hasLicense)
            .flatMap { dependency ->
                dependency
                    .licenseMetadataList
                    .filter { !allowedLicenses.contains(it.licenseName) }
                    .map { license ->
                        "Illegal license found '${license.licenseName}' in '${dependency.dependency}'"
                    }
            }
    }

    private fun getEmptyLicenseMessages(dependencies: Set<DependencyMetadata>): List<String> {
        return dependencies
            .filterNot(DependencyMetadata::hasLicense)
            .map { dependency: DependencyMetadata ->
                "Dependency without a license found '${dependency.dependency}'"
            }
            .toList()
    }

    private fun resolveDependencies(): Set<DependencyMetadata> {
        val licenseResolver = LicenseResolver()
        licenseResolver.setProperty("project", project)
        licenseResolver.setProperty("includeProjectDependencies", true)
        licenseResolver.setProperty("ignoreFatalParseErrors", false)
        licenseResolver.setProperty("dependenciesToIgnore", listOf<String>())
        licenseResolver.setProperty("dependencyConfiguration", "compile")
        return licenseResolver.provideLicenseMap4Dependencies()
    }
}