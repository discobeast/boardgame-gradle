plugins {
    kotlin("jvm") version "2.0.0"
    application
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "MainKt"
}
dependencies {
    implementation("org.jline:jline:3.25.0")
    implementation("org.fusesource.jansi:jansi:2.4.1")
}
tasks.jar {
    archiveVersion = ""
    manifest {
        attributes["Main-Class"] = "MainKt"
        attributes["Add-Opens"] = "java.base/java.lang=ALL-UNNAMED"
    }
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
