plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Existing plugins
    alias(libs.plugins.compose.compiler)

    // Make sure that you have the Google services Gradle plugin
    id("com.google.gms.google-services")

    // Add the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics")

    id("kotlin-kapt")
}

android {
    namespace = "uk.co.explose.schminder.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "uk.co.explose.schminder.android"
        minSdk = 27
        targetSdk = 36
        versionCode = 17
        versionName = "1.0.1.17"

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
            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10" // or latest
    }
}

dependencies {
    implementation(libs.androidbrowserhelper)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material) // Can keep for some design compat
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.material.icons.extended)

// build.gradle (app)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi) // or Gson
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


// Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))

    // Add the dependencies for the Crashlytics and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-installations-ktx:17.1.4")

    implementation("androidx.room:room-runtime:2.7.1")
    kapt("androidx.room:room-compiler:2.7.1")


    // 🧱 Jetpack Compose core libraries
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    //implementation(libs.androidx.material3) // Optional: material2 is also okay
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.material3.v132)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.material3.adaptive.navigation.suite)
    // implementation(libs.androidx.material3.adaptive.navigation)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.1")

    implementation("androidx.compose.material3:material3:1.3.2")

    implementation("com.google.accompanist:accompanist-swiperefresh:0.33.2-alpha")

    implementation("com.google.android.gms:play-services-wearable:19.0.0")

    // 🔁 Navigation for Compose
    implementation(libs.androidx.navigation.compose)

    // OCR ML Kit
    implementation ("androidx.camera:camera-core:1.3.1")
    implementation ("androidx.camera:camera-camera2:1.3.1")
    implementation ("androidx.camera:camera-lifecycle:1.3.1")
    implementation ("androidx.camera:camera-view:1.3.1")

    implementation ("com.google.mlkit:text-recognition:16.0.0")

    implementation ("com.google.accompanist:accompanist-permissions:0.35.0-alpha")

    implementation ("androidx.datastore:datastore-preferences:1.1.0")


    // 🧪 Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}