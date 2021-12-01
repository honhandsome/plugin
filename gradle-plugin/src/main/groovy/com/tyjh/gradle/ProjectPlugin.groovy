package com.tyjh.gradle

import butterknife.plugin.ButterKnifePlugin
import com.alibaba.android.arouter.register.launch.PluginLaunch
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryPlugin
import kotlin.Unit
import kotlin.jvm.functions.Function1
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsSubpluginIndicator
import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.KaptAnnotationProcessorOptions
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

abstract class ProjectPlugin implements Plugin<Project> {

    abstract boolean isApplication()

    @Override
    void apply(Project project) {
        println "Amazing Plugin start:" + project.getName()

        project.extensions.create("config", Extension.class)
        Class<BaseExtension> androidPlugin = isApplication() ? AppPlugin.class : LibraryPlugin.class
        project.getPlugins().apply(androidPlugin)

        BaseExtension extension = project.extensions.getByName("android")
        extension.compileSdkVersion = 30
        extension.buildToolsVersion = "30.0.2"
        extension.defaultConfig.minSdkVersion = 21
        extension.defaultConfig.targetSdkVersion = 28
        extension.buildTypes
                .getByName("release")
                .proguardFiles(extension.getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro')
                .consumerProguardFiles('proguard-rules.pro')
                .setMinifyEnabled(false)
        extension.compileOptions.sourceCompatibility = JavaVersion.VERSION_1_8
        extension.compileOptions.targetCompatibility = JavaVersion.VERSION_1_8
        extension.defaultConfig.javaCompileOptions.annotationProcessorOptions.arguments.put "AROUTER_MODULE_NAME", project.getName()

        project.afterEvaluate {
            doConfig(project, project.extensions.getByType(Extension.class))
        }
    }

    void doConfig(Project project, Extension extension) {
        //Kotlin
        if (extension.getKotlin()) {
            println "Project ${project.getName()} set Kotlin"
            project.getPlugins().apply(KotlinAndroidPluginWrapper.class)
            project.getPlugins().apply(AndroidExtensionsSubpluginIndicator.class)
            project.getPlugins().apply(Kapt3GradleSubplugin.class)
        }
        //ARouter
        if (extension.getARouter()) {
            println "Project ${project.getName()} set ARouter"
            project.getPlugins().apply(PluginLaunch.class)
            String moduleName = project.getName()
            project.dependencies.add('implementation', 'com.alibaba:arouter-api:1.5.1')
            project.dependencies.add('annotationProcessor', 'com.alibaba:arouter-compiler:1.5.1')
            if (extension.getKotlin()) {
                project.extensions.getByType(KaptExtension.class).arguments(new Function1<KaptAnnotationProcessorOptions, Unit>() {
                    @Override
                    Unit invoke(KaptAnnotationProcessorOptions kaptAnnotationProcessorOptions) {
                        kaptAnnotationProcessorOptions.arg("AROUTER_MODULE_NAME", moduleName)
                        return null
                    }
                })
                project.dependencies.add('kapt', 'com.alibaba:arouter-compiler:1.5.1')
            }
        }
        //ButterKnife
        if (extension.getButterKnife()) {
            println "Project ${project.getName()} set ButterKnife"
            project.getPlugins().apply(ButterKnifePlugin.class)
            project.dependencies.add('implementation', 'com.jakewharton:butterknife:10.2.1')
            project.dependencies.add('annotationProcessor', 'com.jakewharton:butterknife-compiler:10.2.1')
            if (extension.getKotlin()) {
                project.dependencies.add('kapt', 'com.jakewharton:butterknife-compiler:10.2.1')
            }
        }
    }
}
