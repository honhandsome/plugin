package com.tyjh.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.PluginAware

class ModuleImporterPlugin implements Plugin<PluginAware> {

    @Override
    void apply(PluginAware target) {
        def localModuleFile = new File(target.rootDir, "module.local")
        if (!localModuleFile.exists()) {
            return
        }
        if (target instanceof Settings) {
            println "配置本地项目"
            localModuleFile.each {
                if ("" == it || it.startsWith("//") || it.startsWith("#")) {
                    // skip
                } else {
                    String[] names = it.split("=>").collect { it.trim() }
                    if (names.length >= 2) {
                        String moduleName = names[0]
                        String path = names[1]
                        println "找到模块:$moduleName,路径:$path"
                        if (!new File(path).exists()) {
                            println "路径:$path 未找到"
                        }
                        String projectName = path.find("[^/]*\$")
                        println "ProjectName: $projectName"
                        target.include ":${projectName}"
                        target.project(":${projectName}").projectDir = new File(path)
                    }
                }
            }
        } else if (target instanceof Project) {
            println "开始替换"
            def externalDeps = [:]
            def externalDepModule = []
            localModuleFile.each {
                if ("" == it || it.startsWith("//") || it.startsWith("#")) {
                    // skip
                } else {
                    String[] names = it.split("=>").collect { it.trim() }
                    if (names.length >= 2) {
                        String moduleName = names[0]
                        String path = names[1]
                        String projectName = path.find("[^/]*\$")
                        externalDeps.put(moduleName, projectName)
                        externalDepModule.add(moduleName)
                    }
                }
            }
            target.allprojects {
                configurations.all {
                    resolutionStrategy {
                        dependencySubstitution {
                            externalDeps.each {
                                println "${it.key} 替换为本地项目 ${it.value}"
                                substitute module(it.key) with project(":${it.value}")
                            }
                        }
                    }
                }
            }
        }
    }
}