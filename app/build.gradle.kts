plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.redditcloneapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.redditcloneapp"
        minSdk = 35
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)

    // Room local db
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // AndroidX + UI
    implementation(libs.material)
    implementation(libs.recyclerview)

    // AndroidX Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // WorkManager – background tasks
    implementation(libs.work.runtime)

    // Glide
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // Navigation components
    implementation(libs.nav.fragment)
    implementation(libs.nav.ui)
}