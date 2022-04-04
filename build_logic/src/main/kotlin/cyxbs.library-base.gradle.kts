import ext.get
import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.kotlin
import versions.AGP

plugins {
    kotlin("android")
    id("com.android.library")
}

android {
    compileSdk = AGP.compileSdk

    publishing {
        singleVariant("debug")
    }

    defaultConfig {
        minSdk = AGP.mineSdk
        targetSdk = AGP.targetSdk

        splits {
            density {

            }
        }

        testInstrumentationRunner = AGP.testInstrumentationRunner
        // 秘钥文件
        manifestPlaceholders += (project.ext["secret"]["manifestPlaceholders"] as Map<String, Any>)
        (project.ext["secret"]["buildConfigField"] as Map<String, String>).forEach { (k, v) ->
            buildConfigField("String", k, v)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}