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
        val versionPropsFile = file("version.properties")
        if (!versionPropsFile.exists()) {
            throw GradleException("version.properties file not found at ${versionPropsFile.absolutePath}")
        }
        val version = loadProperties(versionPropsFile.path)
        versionCode = version.getProperty("version.code").toInt()
        versionName = version.getProperty("version.name")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // signingConfigs 块已被移除

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 强制 release 构建使用 debug 签名
            signingConfig = signingConfigs.getByName("debug")
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
} // <-- android {} 块结束

// --- 最终的、正确的修改 ---
// 使用 androidComponents API 和 archivesBaseName 来设置输出文件的基础名称
androidComponents {
    onVariants { variant ->
        // 设置该变体的基础名称，Gradle 会自动添加 ABI 后缀和 .apk 扩展名
        // variant.name 会是类似 "release" 或 "debug" 的名字
        variant.archivesBaseName.set("${rootProject.name}-${variant.name}")
    }
}
// --- 修改结束 ---

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
