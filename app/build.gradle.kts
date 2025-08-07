import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    kotlin("kapt")
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    // alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.houvven.guise"
    compileSdk = 34

    defaultConfig {
        applicationId = namespace
        minSdk = 24
        targetSdk = 35
        // 确保 version.properties 存在，否则这里也会报错
        val version = loadProperties(file("version.properties").path)
        versionCode = version.getProperty("version.code").toInt()
        versionName = version.getProperty("version.name")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        // 为了在没有 local.properties 时也能编译，我们将 release 签名配置暂时注释掉。
        // 如果你需要发布版本，你需要提供这些签名信息。
        // 或者，你可以考虑在 CI/CD 环境中通过环境变量注入这些值。
        /*
        create("release") {
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            // 检查 local.properties 是否存在，如果不存在则跳过签名配置
            if (rootProject.file("local.properties").exists()) {
                val properties = loadProperties(rootProject.file("local.properties").path)
                storeFile = File(properties.getProperty("sign.store.file"))
                storePassword = properties.getProperty("sign.store.password")
                keyAlias = properties.getProperty("sign.key.alias")
                keyPassword = properties.getProperty("sign.key.password")
            } else {
                // 在没有 local.properties 时，为了编译通过，可以考虑使用默认的 debug 签名
                // 警告：这会导致 release 构建使用 debug 签名，不适用于生产环境发布
                println("Warning: local.properties not found. Release build will use debug signing config.")
                storeFile = file("debug.keystore") // 默认的 debug 密钥库
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
        */
        // 临时解决方案：如果 local.properties 不存在，则不创建 release 签名配置
        // 这样 release 构建会回退到 debug 签名
        if (rootProject.file("local.properties").exists()) {
            create("release") {
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
                val properties = loadProperties(rootProject.file("local.properties").path)
                storeFile = File(properties.getProperty("sign.store.file"))
                storePassword = properties.getProperty("sign.store.password")
                keyAlias = properties.getProperty("sign.key.alias")
                keyPassword = properties.getProperty("sign.key.password")
            }
        } else {
            // 如果 local.properties 不存在，确保 release 构建类型不引用不存在的签名配置
            // 或者使用默认的 debug 签名
            println("Warning: local.properties not found. Release signing config will not be applied or will use default debug signing.")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 仅当 release 签名配置存在时才应用
            if (signingConfigs.findByName("release") != null) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                // 如果没有 release 签名配置，则使用 debug 签名配置
                signingConfig = signingConfigs.getByName("debug")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    splits {
        abi {
            isEnable = true
            isUniversalApk = true
            reset()
            include("arm64-v8a", "x86_64")
        }
    }
    buildOutputs.all {
        this as BaseVariantOutputImpl
        outputFileName = "${rootProject.name}-${name}.apk"
    }
}

dependencies {
    implementation(project(":hook"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // compose
    implementation(libs.androidx.material.icons.extended)
    // lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // destination
    implementation(libs.compose.destinations.core)
    implementation(libs.compose.destinations.bottomSheet)
    ksp(libs.compose.destinations.ksp)
    // mmkv
    implementation(libs.mmkv.static)
    implementation(libs.mmkv.ktx)
    // koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.betterandroid.extension.system)
    implementation(libs.lservice)
    implementation(libs.libsu.io)
    implementation(libs.hiddenapibypass)
}
