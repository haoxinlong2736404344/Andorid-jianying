//plugins {
//    kotlin("multiplatform")
//    kotlin("plugin.serialization")
//    id("com.android.library")
//    id("org.jetbrains.compose")
//    id("org.jetbrains.kotlin.plugin.compose")
//}
//
//kotlin {
//    androidTarget()
//    jvm()
//
//    sourceSets {
//        commonMain.dependencies {
//            implementation(compose.runtime)
//            implementation(compose.foundation)
//            implementation(compose.material3)
//            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
//        }
//
//        commonTest.dependencies {
//            implementation(kotlin("test"))
//        }
//    }
//}



//android {
//    namespace = "com.composeflow.camp.dynamic.shared"
//    compileSdk = 35
//
//    defaultConfig {
//        minSdk = 24
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_17
//        targetCompatibility = JavaVersion.VERSION_17
//    }
//
//}




plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlinx-serialization")
}

kotlin {
    // 👇 关键：声明 Android 目标
    androidTarget()

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            }
        }
        androidMain {
            dependencies {
                implementation("io.coil-kt:coil-compose:2.7.0")
            }
        }
    }
}

android {
    namespace = "com.composeflow.camp.shared"
    compileSdk = 35

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}