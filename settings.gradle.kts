pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "snappicker"

include(":snappicker")
include(":snappicker-compose")

include(":samples:android")
include(":samples:android-compose")
