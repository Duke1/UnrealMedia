plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version PluginVersions.kotlinVersion
}


android {
    compileSdk = CompileConfig.compileSdkVersion
    buildToolsVersion = CompileConfig.buildToolsVersion

    defaultConfig {
        applicationId = CompileConfig.applicationId
        minSdk = CompileConfig.minSdkVersion
        targetSdk = CompileConfig.targetSdkVersion
        versionCode = CompileConfig.versionCode
        versionName = CompileConfig.versionName

        renderscriptTargetApi = 21
        renderscriptSupportModeEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++11"
                cppFlags += "-frtti"
                cppFlags += "-fexceptions"
                cppFlags += "-DANDROID"
                cppFlags += "-Wno-unused-variable"

                arguments += "-DANDROID_PLATFORM=android-30"
                arguments += "-DANDROID_TOOLCHAIN=clang"
                arguments += "-DANDROID_STL=c++_shared"
                arguments += "-DANDROID_TOOLCHAIN_NAME=arm-linux-androideabi-clang3.6"
            }
        }


        ndk {

            //'x86', 'armeabi', 'armeabi-v7a', 'arm64-v8a'
            // stl 'stlport_static'
            abiFilters += "arm64-v8a"
        }

        //        sourceSets {
        //            main {
        //                jniLibs.srcDir(['libs', '../../ffmpeg_build'])
        //            }
        //
        //        }

        vectorDrawables.useSupportLibrary = true


    }
    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt") , "proguard-rules.pro")
        }
    }
    externalNativeBuild {
        cmake {
            path("CMakeLists.txt")
            version = CompileConfig.cmakeVersion
        }
    }
    ndkVersion = CompileConfig.ndkVersion

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
    implementation(fileTree(mapOf("dir" to "libs" , "include" to listOf("*.jar" , "*.aar"))))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")

    implementation(PluginDeps.transitionKtx)
    implementation(PluginDeps.coreKtx)
    implementation(PluginDeps.paletteKtx)
    implementation(PluginDeps.appcompat)
    implementation(PluginDeps.cardview)
    implementation(PluginDeps.material)
    implementation(PluginDeps.recyclerview)
    implementation(PluginDeps.media)

    //Kotlin
    implementation(PluginDeps.kotlinReflect)
    implementation(PluginDeps.kotlinxCoroutinesAndroid)


    // KTX 扩展程序列表  https://developer.android.google.cn/kotlin/ktx/extensions-list
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${PluginVersions.lifecycleVersion}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${PluginVersions.lifecycleVersion}")

    implementation("androidx.navigation:navigation-fragment-ktx:${PluginVersions.navVersion}")
    implementation("androidx.navigation:navigation-ui-ktx:${PluginVersions.navVersion}")

    implementation(PluginDeps.room)
    annotationProcessor(PluginDeps.roomCompiler)
    kapt(PluginDeps.roomCompiler)

    implementation(PluginDeps.okhttp)

    implementation(PluginDeps.gson)

    implementation(PluginDeps.fresco)

    //权限申请
    implementation("com.github.permissions-dispatcher:permissionsdispatcher:4.9.2")
    implementation("com.github.permissions-dispatcher:ktx:1.1.4")
    kapt("com.github.permissions-dispatcher:permissionsdispatcher-processor:4.9.2")

}


kapt { //指定room.schemaLocation生成的文件路径
    arguments {
        arg("room.schemaLocation" , "$projectDir/schemas".toString())
    }
}
