package com.tyjh.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin

public class LibraryPlugin implements Plugin<Project> {

    public void apply(Project project) {
        // Register a task
        project.tasks.register("greeting") {
            doLast {
                println("gradle-plugin 'com.tyjh.plugin.LibraryPlugin'")
            }
        }
    }
}