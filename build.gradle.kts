plugins {
    kotlin("multiplatform") version "2.0.21" apply false
    kotlin("android") version "2.0.21" apply false
    kotlin("plugin.serialization") version "2.0.21" apply false
    id("com.android.application") version "8.7.3" apply false
    id("com.android.library") version "8.7.3" apply false
    id("org.jetbrains.compose") version "1.7.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}
