package com.atlassian.performance.tools.license

import nl.javadude.gradle.plugins.license.DependencyMetadata
import nl.javadude.gradle.plugins.license.LicenseResolver
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class VerifyLicensingTask : DefaultTask() {
    private val allowedLicenses = setOf(
        "The Apache Software License, Version 2.0", // https://choosealicense.com/licenses/apache-2.0/
        "Apache License, Version 2.0",
        "Apache 2",
        "Apache 2.0",
        "Apache-2.0",
        "Apache License 2.0",
        "Apache License v2.0",
        "Apache Public License 2.0",
        "The Apache License, Version 2.0",
        "Bouncy Castle Licence",
        "MIT", // https://choosealicense.com/licenses/mit/
        "MIT license",
        "MIT License",
        "The MIT License",
        "Revised BSD",
        "New BSD License",
        "BSD",
        "BSD License",
        "CC0 1.0 Universal",
        "CDDL 1.1",
        "CDDL+GPL License",
        "COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0",
        "Dual license consisting of the CDDL v1.1 and GPL v2",
        "Eclipse Distribution License (New BSD License)",
        "Eclipse Public License 1.0",
        "GNU General Public License, version 2 (GPL2), with the classpath exception",
        "GPL2 w/ CPE",
        "GNU Lesser General Public License", // https://choosealicense.com/licenses/lgpl-2.1/
        "LGPL, version 2.1"
    )

    @TaskAction
    @Suppress("unused")
    fun verifyLicenses() {
        val dependencies = listOf("compileClasspath", "runtimeClasspath")
            .flatMap { resolveDependencies(it) }
            .toSet()
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

    /**
     * https://choosealicense.com/no-permission/
     */
    private fun getEmptyLicenseMessages(dependencies: Set<DependencyMetadata>): List<String> {
        return dependencies
            .filterNot(DependencyMetadata::hasLicense)
            .map { dependency: DependencyMetadata ->
                "Dependency without a license found '${dependency.dependency}'"
            }
            .toList()
    }

    private fun resolveDependencies(config: String): Set<DependencyMetadata> {
        val licenseResolver = LicenseResolver()
        licenseResolver.setProperty("project", project)
        licenseResolver.setProperty("includeProjectDependencies", true)
        licenseResolver.setProperty("ignoreFatalParseErrors", false)
        licenseResolver.setProperty("dependenciesToIgnore", listOf<String>())
        licenseResolver.setProperty("dependencyConfiguration", config)
        return licenseResolver.provideLicenseMap4Dependencies()
    }
}
