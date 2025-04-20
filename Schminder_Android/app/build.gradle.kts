plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Existing plugins
    alias(libs.plugins.compose.compiler)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "uk.co.explose.schminder.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "uk.co.explose.schminder.android"
        minSdk = 27
        targetSdk = 34
        versionCode = 4
        versionName = "1.0.1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            buildConfigField( "String", "BASE_URL", "\"https://8703-82-34-165-255.ngrok-free.app/schminder_net/\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material) // Can keep for some design compat
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.material.icons.extended)

// build.gradle (app)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi) // or Gson

// Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")


    // 🧱 Jetpack Compose core libraries
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3) // Optional: material2 is also okay
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    // 🔁 Navigation for Compose
    implementation(libs.androidx.navigation.compose)

    // 🧪 Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}