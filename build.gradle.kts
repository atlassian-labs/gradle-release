import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish").version("0.21.0")
    id("pl.allegro.tech.build.axion-release").version("1.18.16")
    id("signing")
}

dependencyLocking {
    lockAllConfigurations()
}

scmVersion {
    tag.prefix.set("release-")
}

project.version = scmVersion.version
project.group = "com.atlassian.performance.tools"

gradlePlugin {
    plugins {
        create("${project.group}.${project.name}") {
            id = "${project.group}.${project.name}"
            implementationClass = "${project.group}.GradleRelease"
        }
    }
}

pluginBundle {
    website = "https://bitbucket.org/atlassian/gradle-release"
    vcsUrl = "https://bitbucket.org/atlassian/gradle-release"
    (plugins) {
        "${project.group}.${project.name}" {
            displayName = "Performance Tools release plugin"
            description = "Performance Tools release plugin"
            version = scmVersion.version
            tags = listOf("individual")
        }
    }
}

dependencies {
    implementation(gradlePlugin("pl.allegro.tech.build.axion-release", "1.18.16"))
    implementation(gradlePlugin("org.jetbrains.dokka", "1.9.20"))
    implementation(gradlePlugin("com.github.hierynomus.license", "0.15.0"))
    implementation("io.github.rmanibus.gradle:maven-settings-plugin:0.7")
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.10.0.202406032230-r")
    testImplementation("junit:junit:4.12")
    testImplementation("org.assertj:assertj-core:3.11.0")
}

fun gradlePlugin(
    id: String,
    version: String
): String {
    return "$id:$id.gradle.plugin:$version"
}

repositories {
    gradlePluginPortal()
}

tasks.wrapper {
    gradleVersion = "8.11"
    distributionType = Wrapper.DistributionType.ALL
}
