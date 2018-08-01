plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("pl.allegro.tech.build.axion-release").version("1.8.1")
}

scmVersion {
    tag.prefix = "release"
}
project.version = scmVersion.version
project.group = "com.atlassian.performance.tools"

gradlePlugin {
    (plugins) {
        "${project.group}.${project.name}" {
            id = "${project.group}.${project.name}"
            implementationClass = "${project.group}.GradleRelease"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

dependencies {
    compile(gradlePlugin("pl.allegro.tech.build.axion-release", "1.8.1"))
    compile(gradlePlugin("org.jetbrains.dokka", "0.9.17"))
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

val wrapper = tasks["wrapper"] as Wrapper
wrapper.gradleVersion = "4.9"
wrapper.distributionType = Wrapper.DistributionType.ALL