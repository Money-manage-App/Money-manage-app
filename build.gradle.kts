// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        // 🔥 Dòng này rất quan trọng cho plugin Google Services
        classpath("com.google.gms:google-services:4.4.2")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
