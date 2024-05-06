plugins {
    alias(libs.plugins.jvm)
    `java-library`
}

repositories {
    mavenCentral()
    maven {
        setUrl("https://jitpack.io")
    }
}

dependencies {
    implementation("com.github.letorbi:discord-game-sdk4j:connection-check-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.10")
    implementation(files("/usr/share/processing/lib/pde.jar"))

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

// Include required libraries in the jar
tasks.named<Jar>("jar") {
    archiveFileName.set("DiscordRichPresence.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(
        configurations.runtimeClasspath.get().filter {
            // Exclude Processing libraries
            !it.toString().contains("pde.jar") and it.exists()
        }.map {
            if (it.isDirectory) it else zipTree(it)
        }
    )
}
