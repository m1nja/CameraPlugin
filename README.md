# CameraPlugin
android Camera plugin

android自定义Camera插件，较高的兼容性

How to get a Git project in your build

step1. add the JitPack repository to your build file

add it in your project root build.gradle at the end of repositories

allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}

step2. Add the dependencies

dependencies {
    compile 'com.github.m1nja:CameraPlugin:v1.0.0'
}
