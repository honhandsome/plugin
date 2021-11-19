package com.tyjh.gradle

import org.gradle.BuildAdapter
import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.Plugin

class ApplicationPlugin implements Plugin<Project> {

    void apply(Project project) {

        // Register a task
        project.tasks.register("greeting") {
            doLast {
                println("gradle-plugin 'com.tyjh.plugin.ApplicationPlugin'")
            }
        }

        //==========================================================
        project.extensions.create('YppTracker', AutoSettingParams)
        GlobalConfig.setProject(project)

        //使用Transform实行遍历
        def android = project.extensions.getByType(AppExtension)
        project.gradle.addBuildListener(new BuildAdapter() {
            void buildFinished(BuildResult result) {
                super.buildFinished(result)
                AutoTransformModule.clearModuleMap()
            }
        })
        registerTransform(android)
        project.afterEvaluate {
            Logger.setDebug(project.YppTracker.isDebug)
        }
    }

    static AutoTransform registerTransform(BaseExtension android) {
        AutoTransform transform = new AutoTransform()
        android.registerTransform(transform)
        return transform
    }
}