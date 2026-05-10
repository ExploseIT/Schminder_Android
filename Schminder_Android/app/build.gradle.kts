plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Existing plugins
    alias(libs.plugins.compose.compiler)

    // Make sure that you have the Google services Gradle plugin
    id("com.google.gms.google-services")

    // Add the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics")

    //id("kotlin-kapt")
    //id("com.google.devtools.ksp") version "2.3.4" apply false
    id("com.google.devtools.ksp")
}

android {
    namespace = "uk.co.explose.schminder.android"
    compileSdk = 37

    defaultConfig {
        applicationId = "uk.co.explose.schminder.android"
        minSdk = 27
        targetSdk = 37
        versionCode = 20
        versionName = "1.0.1.20"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
             buildConfigField( "String", "BASE_URL",  "\"https://explose.ngrok.app/schminder_net/\"")

            //buildConfigField( "String", "BASE_URL", "\"https://schminder.co.uk/\"")
        }
        release {
            isMinifyEnabled = true // Enables R8 shrinking/obfuscation
            isShrinkResources = true
            buildConfigField( "String", "BASE_URL", "\"https://schminder.co.uk/\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.tracing)
    implementation(libs.androidbrowserhelper)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material) // Can keep for some design compat
    implementation(libs.androidx.material.icons.extended)

// build.gradle (app)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi) // or Gson
    implementation(libs.converter.gson)


// Import the Firebase BoM
    implementation(platform(libs.firebase.bom))

    // Add the dependencies for the Crashlytics and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    // Firebase Authentication
    implementation(libs.firebase.auth)
    implementation(libs.firebase.installations.ktx)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)


    // 🧱 Jetpack Compose core libraries
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    //implementation(libs.androidx.material3) // Optional: material2 is also okay
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.material3.v140)
    implementation(libs.androidx.material3.adaptive.navigation.suite)
    // implementation(libs.androidx.material3.adaptive.navigation)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.accompanist.swiperefresh)

    implementation(libs.play.services.wearable)

    // 🔁 Navigation for Compose
    implementation(libs.androidx.navigation.compose)

    // OCR ML Kit
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)

    implementation (libs.text.recognition)

    implementation (libs.accompanist.permissions)

    implementation (libs.androidx.datastore.preferences)


    // 🧪 Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}