/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cdancy.gradle.artifactory.rest

import com.cdancy.gradle.artifactory.rest.tasks.AbstractArtifactoryRestTask
import com.cdancy.gradle.artifactory.rest.utils.ArtifactoryRestThreadContextClassLoader
import com.cdancy.gradle.artifactory.rest.utils.ThreadContextClassLoader
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

/**
 * Gradle plugin that provides custom tasks for interacting with Docker via its remote API.
 */
class ArtifactoryRestPlugin implements Plugin<Project> {
    public static final String ARTIFACTORY_CONFIGURATION_NAME = 'artifactoryRest'
    public static final String ARTIFACTORY_REST_DEFAULT_VERSION = '0.9.8'
    public static final String EXTENSION_NAME = 'artifactoryRest'
    public static final String DEFAULT_TASK_GROUP = 'ArtifactoryRest'

    @Override
    void apply(Project project) {

        Configuration configuration = project.configurations.create(ARTIFACTORY_CONFIGURATION_NAME)
            .setVisible(false)
            .setTransitive(true)
            .setDescription('The Artifactory Java libraries to be used for this project.')

        ArtifactoryRestExtension extension = project.extensions.create(EXTENSION_NAME, ArtifactoryRestExtension)
        configureAbstractArtifactoryTask(project, extension)
    }

    private void configureAbstractArtifactoryTask(Project project, ArtifactoryRestExtension extension) {
        configurePluginClassPath(project)
        project.tasks.withType(AbstractArtifactoryRestTask) {
            group = DEFAULT_TASK_GROUP
            //default the task's values for artifactoryRest to the global extension's
            //values by default
            artifactoryRest.url.set(extension.url)
            artifactoryRest.credentials.set(extension.credentials)
            artifactoryRest.overrides.set(extension.overrides)
        }
    }

    private static Configuration configurePluginClassPath(Project project) {
        Configuration configuration = project.configurations.getByName(ARTIFACTORY_CONFIGURATION_NAME)
        configuration.defaultDependencies { dependencies ->
            def artifactoryRestGAVC = "com.cdancy:artifactory-rest:$ArtifactoryRestPlugin.ARTIFACTORY_REST_DEFAULT_VERSION:all"
            def artifactoryRestDep = project.dependencies.create(artifactoryRestGAVC) {
                transitive = false
            }
            dependencies.add(artifactoryRestDep)
            dependencies.add(project.dependencies.create('org.slf4j:slf4j-simple:1.7.5'))
        }
        configuration
    }
}
