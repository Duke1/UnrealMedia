/**
 * 插件版本
 */
object PluginVersions {

    const val kotlinVersion = "1.7.21"
    const val kotlinCoroutines = "1.6.4"


    const val lifecycleVersion = "2.4.1"

    const val coreKtx = "1.7.0"
    const val fragmentKtx = "1.4.1"

    const val annotation = "1.3.0"
    const val gson = "2.10"

    const val okhttp = "4.10.0"

    const val roomVersion = "2.4.3"


    const val frescoVersion = "2.5.0"
    const val navVersion = "2.5.3"
}

object PluginDeps {
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${PluginVersions.kotlinVersion}"
    const val kotlinxCoroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${PluginVersions.kotlinCoroutines}"


    const val coreKtx = "androidx.core:core-ktx:1.9.0"

    const val transitionKtx = "androidx.transition:transition-ktx:1.4.1"
    const val media = "androidx.media:media:1.6.0"
    const val material = "com.google.android.material:material:1.7.0"
    const val paletteKtx = "androidx.palette:palette-ktx:1.0.0"
    const val cardview = "androidx.cardview:cardview:1.0.0"
    const val recyclerview = "androidx.recyclerview:recyclerview:1.2.1"
    const val appcompat = "androidx.appcompat:appcompat:1.5.1"

    const val okhttp = "com.squareup.okhttp3:okhttp:${PluginVersions.okhttp}"
    const val gson = "com.google.code.gson:gson:${PluginVersions.gson}"
    const val fresco = "com.facebook.fresco:fresco:${PluginVersions.frescoVersion}"


    const val room = "androidx.room:room-runtime:${PluginVersions.roomVersion}"
    const val roomCompiler = "androidx.room:room-compiler:${PluginVersions.roomVersion}"
}