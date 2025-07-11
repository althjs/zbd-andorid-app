plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "kr.zbd.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "kr.zbd.android"
        minSdk = 24
        targetSdk = 35
        versionCode = 9
        versionName = "1.0.9"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // 벡터 드로어블 지원
        vectorDrawables.useSupportLibrary = true
        
        // 리소스 구성 최적화
        resourceConfigurations += setOf("ko", "en")
    }

    signingConfigs {
        create("release") {
            storeFile = file("../keys/release.keystore")
            storePassword = "키스토어암호"
            keyAlias = "release"
            keyPassword = "키스토어암호"
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // 릴리스 빌드 최적화
            isDebuggable = false
            isJniDebuggable = false
            isPseudoLocalesEnabled = false
        }
    }
    
    // 앱 번들 설정
    bundle {
        language {
            enableSplit = false
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation("androidx.browser:browser:1.7.0") // Chrome Custom Tabs
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
