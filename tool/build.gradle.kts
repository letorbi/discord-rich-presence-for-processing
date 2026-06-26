plugins {
    id("com.gradleup.shadow") version "8.3.1"
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
    implementation("com.github.JnCrMx:discord-game-sdk4j:v1.0.0")
    implementation("com.google.code.gson:gson:2.10")
    compileOnly(files("/usr/share/processing/lib/app/resources/modes/java/mode/app-4.5.5.jar"))

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

tasks.shadowJar {
    minimize()
}
