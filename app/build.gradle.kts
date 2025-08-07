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

    // signingConfigs 块已被移除，因为我们不再需要自定义签名

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 强制 release 构建使用 debug 签名，无需任何私有密钥
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
} // <-- 这里是 android {} 块的结束位置

// --- 关键修改：此代码块必须在 android {} 块之外 ---
// 使用 AGP 7+ 推荐的 androidComponents API 来设置输出文件名
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val baseOutputName = "${rootProject.name}-${variant.buildType}"
            val finalOutputName = if (variant.flavorName.isNullOrEmpty()) {
                "${baseOutputName}.apk"
            } else {
                "${baseOutputName}-${variant.flavorName}.apk"
            }
            // 正确的属性是 outputFileName
            output.outputFileName.set(finalOutputName)
        }
    }
}
// --- 关键修改结束 ---

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
