buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.3.1")

        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${PluginVersions.kotlinVersion}")

    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")
    }
}

tasks.register("clean" , Delete::class) {
    delete(rootProject.buildDir)
}
