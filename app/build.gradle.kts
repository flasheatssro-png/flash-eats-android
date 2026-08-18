plugins {
    id("com.android.application")
}

android {
    namespace = "cz.flasheats.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "cz.flasheats.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
