// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlin_version by extra("1.9.0")
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    }
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven {  url = uri("https://maven.google.com") }
    }
}
plugins {
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
        id("com.android.application") version "8.1.1" apply false
        id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10" apply false
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" // this version matches your Kotlin version


}