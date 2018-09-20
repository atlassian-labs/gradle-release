import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish").version("0.10.0")
    id("pl.allegro.tech.build.axion-release").version("1.8.1")
    id("signing")
}

scmVersion {
    tag.prefix = "release"
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
    compile(gradlePlugin("pl.allegro.tech.build.axion-release", "1.8.1"))
    compile(gradlePlugin("org.jetbrains.dokka", "0.9.17"))
    compile(gradlePlugin("com.github.hierynomus.license", "0.14.0"))
    compile("net.linguica.gradle:maven-settings-plugin:0.5")
    testCompile("junit:junit:4.12")
    testCompile("org.hamcrest:hamcrest-library:1.3")
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

task<Wrapper>("wrapper") {
    gradleVersion = "4.10.2"
    distributionType = Wrapper.DistributionType.ALL
}
