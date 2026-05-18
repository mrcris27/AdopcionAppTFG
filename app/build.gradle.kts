plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    id("com.google.gms.google-services")

    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.adopciontfg"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.adopciontfg"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation.core.lint)
    implementation(libs.material.icons.extended)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.material.icons.extended)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.osmdroid.android)
    implementation(libs.androidx.compose.ui.test)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.hilt.android)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    ksp(libs.hilt.compiler)

    // Import the Firebase BoM (Bill of Materials) to manage your Firebase library versions
    // Always use the latest version available.
    implementation(platform("com.google.firebase:firebase-bom:32.0.0")) //

    // Firebase Authentication for user and shelter logins
    implementation("com.google.firebase:firebase-auth-ktx")

    // Cloud Firestore for storing animal and shelter data
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Cloud Storage for animal photos
    implementation("com.google.firebase:firebase-storage-ktx")
}
