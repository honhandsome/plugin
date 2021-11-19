package com.tyjh.gradle

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class ProjectPlugin implements Plugin<Project> {

    abstract boolean isApplication()

    @Override
    void apply(Project project) {
        println "Amazing Plugin start:" + project.getName()
        Class<BaseExtension> androidPlugin = isApplication() ? AppPlugin.class : LibraryPlugin.class
        project.getPlugins().apply(androidPlugin)

        addOthersPlugin(project)
    }

    void addOthersPlugin(Project project) {
    }
}
