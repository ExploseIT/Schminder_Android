// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // Existing plugins
    alias(libs.plugins.compose.compiler) apply false

// Make sure that you have the Google services Gradle plugin 4.4.1+ dependency
    id("com.google.gms.google-services") version "4.4.4" apply false

    // Add the dependency for the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics") version "3.0.7" apply false

    id("com.google.devtools.ksp") version "2.3.7" apply false
}

