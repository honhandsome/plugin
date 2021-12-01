package com.tyjh.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.tasks.bundling.Jar

class LibraryPlugin extends ProjectPlugin {

    @Override
    boolean isApplication() {
        return false
    }

    @Override
    void doConfig(Project project, Extension extension) {
        super.doConfig(project, extension)
        configUploadArchives(project)
    }

    void configUploadArchives(Project project) {
        def outFile = new File("${project.projectDir}/pom.properties")
        if (!outFile.exists()) {
            return
        }
        println("Found pom.properties")
        project.getPlugins().apply(MavenPlugin.class)
        Properties pomProps = new Properties()
        pomProps.load(new FileInputStream("${project.projectDir}/pom.properties"))
        project.uploadArchives {
            repositories {
                mavenDeployer {
                    repository(url: pomProps.url) {
                        authentication(userName: pomProps.username, password: pomProps.password)
                    }

                    pom.groupId = pomProps.groupId
                    pom.artifactId = pomProps.artifactId
                    pom.version = pomProps.version
                    pom.project {
                        description 'git rev-parse HEAD'.execute([], project.projectDir).text.trim()
                    }
                }
            }
        }
        def task = project.tasks.create("androidSourcesJar", Jar)
        task.archiveClassifier = "sources"
        task.from(project.android.sourceSets.main.java.srcDirs)
        project.artifacts {
            archives task
        }
    }
}