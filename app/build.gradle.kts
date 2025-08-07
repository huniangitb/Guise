import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.konan.properties.loadProperties
import com.android.build.api.variant.ApplicationVariant

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
    signingConfigs {
        // 为了在没有 local.properties 时也能编译，我们添加条件判断
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            create("release") {
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
                val properties = loadProperties(localPropertiesFile.path)
                // 确保属性存在，否则这里会抛出 NullPointerException
                storeFile = File(properties.getProperty("sign.store.file") ?: error("sign.store.file not found in local.properties"))
                storePassword = properties.getProperty("sign.store.password") ?: error("sign.store.password not found in local.properties")
                keyAlias = properties.getProperty("sign.key.alias") ?: error("sign.key.alias not found in local.properties")
                keyPassword = properties.getProperty("sign.key.password") ?: error("sign.key.password not found in local.properties")
            }
        } else {
            // 如果 local.properties 不存在，为了编译通过，可以打印警告
            println("Warning: local.properties not found. Release signing config will not be explicitly set and will default to debug signing if not overridden.")
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
                // 这意味着在没有 local.properties 时，release 构建将使用 debug 签名
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

    // --- 解决 NullPointerException 的关键修改 ---
    // 替换掉旧的 buildOutputs.all {}
    // 使用新的 applicationVariants.all API 来处理构建变体的输出
    applicationVariants.all { variant ->
        variant.outputs.all { output ->
            // 对于 AGP 8.x，output.outputFileName 是一个 Property<String>
            // 需要使用 .set() 方法来设置其值

            val baseOutputName = "${rootProject.name}-${variant.buildType.name}"
            val finalOutputName = if (variant.flavorName.isNotEmpty()) {
                "${baseOutputName}-${variant.flavorName}.apk"
            } else {
                "${baseOutputName}.apk"
            }

            // 设置 APK 的输出文件名
            output.outputFileName.set(finalOutputName)
        }
    }
    // --- 关键修改结束 ---
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
