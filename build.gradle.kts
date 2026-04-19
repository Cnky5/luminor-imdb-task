plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.testng:testng:7.12.0")
    testImplementation("com.codeborne:selenide:7.16.0")
    testImplementation("io.qameta.allure:allure-testng:2.34.0")
}

tasks.test {
    useTestNG()
    systemProperty("allure.results.directory", layout.buildDirectory.dir("allure-results").get().asFile.absolutePath)
}
