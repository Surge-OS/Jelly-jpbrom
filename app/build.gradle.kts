/*
 * SPDX-FileCopyrightText: 2017-2023 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

import org.lineageos.generatebp.GenerateBpPlugin
import org.lineageos.generatebp.GenerateBpPluginExtension
import org.lineageos.generatebp.models.Module

plugins {
    id("com.android.application")
    id("kotlin-android")
}

apply {
    plugin<GenerateBpPlugin>()
}

buildscript {
    repositories {
        maven("https://raw.githubusercontent.com/lineage-next/gradle-generatebp/v1.7/.m2")
    }

    dependencies {
        classpath("org.lineageos:gradle-generatebp:+")
    }
}

android {
    compileSdk = 34
    namespace = "org.lineageos.jelly"

    defaultConfig {
        applicationId = "org.lineageos.jelly"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            // Enables code shrinking, obfuscation, and optimization.
            isMinifyEnabled = true

            // Enables resource shrinking.
            isShrinkResources = true

            // Includes the default ProGuard rules files.
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
        getByName("debug") {
            // Append .dev to package name so we won't conflict with AOSP build.
            applicationIdSuffix = ".dev"
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

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.0.20-Beta2"))

    implementation("androidx.core:core-ktx:1.15.0-alpha01")
    implementation("androidx.annotation:annotation:1.9.0-alpha01")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.palette:palette:1.0.0")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0-alpha01")
    implementation("androidx.transition:transition:1.5.0")
    implementation("com.google.android.material:material:1.13.0-alpha04")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
}

configure<GenerateBpPluginExtension> {
    targetSdk.set(android.defaultConfig.targetSdk!!)
    availableInAOSP.set { module: Module ->
        when {
            module.group.startsWith("androidx") -> {
                // We provide our own androidx.appcompat
                !module.group.startsWith("androidx.appcompat")
            }
            module.group.startsWith("org.jetbrains") -> true
            module.group == "com.google.auto.value" -> true
            module.group == "com.google.errorprone" -> true
            module.group == "com.google.guava" -> true
            module.group == "junit" -> true
            else -> false
        }
    }
}
