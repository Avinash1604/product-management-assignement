plugins {
    // Auto-provisions the requested JDK toolchain (JDK 25) from Foojay/Adoptium.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "product-management"
