//plugins {
//    id("com.android.application")
//    kotlin("android")
//    id("org.jetbrains.compose")
//    id("org.jetbrains.kotlin.plugin.compose")
//}
//
//android {
//    namespace = "com.composeflow.camp.android"
//    compileSdk = 35
//
//    defaultConfig {
//        applicationId = "com.composeflow.camp.android"
//        minSdk = 24
//        targetSdk = 35
//        versionCode = 1
//        versionName = "1.0"
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_17
//        targetCompatibility = JavaVersion.VERSION_17
//    }
//
//    kotlinOptions {
//        jvmTarget = "17"
//    }
//}
//
//dependencies {
//    implementation(project(":shared"))
//    implementation(compose.runtime)
//    implementation(compose.foundation)
//    implementation(compose.material3)
//    implementation("androidx.activity:activity-compose:1.9.3")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
//}


plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    // 👈 不要在 androidApp 再加 kotlinx-serialization 插件
}

android {
    namespace = "com.composeflow.camp.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.composeflow.camp.android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation("androidx.activity:activity-compose:1.9.3")
    // 👇 直接在 androidApp 里加上序列化依赖，解决找不到引用的问题
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}