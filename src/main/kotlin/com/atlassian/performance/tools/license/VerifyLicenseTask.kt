package com.atlassian.performance.tools.license

import nl.javadude.gradle.plugins.license.LicenseResolver
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class VerifyLicenseTask : DefaultTask() {
    private val allowedLicenses = setOf("Apache 2.0")

    @TaskAction
    fun verifyLicenses() {
        val licenseResolver = LicenseResolver()
        licenseResolver.setProperty("project", project)
        licenseResolver.setProperty("includeProjectDependencies", true)
        licenseResolver.setProperty("ignoreFatalParseErrors", false)
        val dependencies = licenseResolver.provideLicenseMap4Dependencies()

        var checkFailed = false
        dependencies.forEach { dependency ->
            if (dependency.hasLicense()) {
                dependency.licenseMetadataList.forEach { license ->
                    if(!allowedLicenses.contains(license.licenseName)){
                        logger.error("Illegal license found ${license.licenseName} in ${dependency.dependency}")
                        checkFailed = true
                    }
                }
            } else {
                logger.error("Dependency without a license found ${dependency.dependency}")
                checkFailed = true
            }
        }

        if(checkFailed){
            throw Exception("Licenses verify task failed")
        }
    }
}