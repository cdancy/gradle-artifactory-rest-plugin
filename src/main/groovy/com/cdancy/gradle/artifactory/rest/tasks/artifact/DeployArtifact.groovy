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
package com.cdancy.gradle.artifactory.rest.tasks.artifact

import com.cdancy.gradle.artifactory.rest.tasks.ArtifactAware
import groovy.io.FileType
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional

class DeployArtifact extends ArtifactAware {

    @Optional
    @Input
    final MapProperty<String, List<String>> properties = project.objects.mapProperty(String, List).convention([:])

    @Optional
    @InputFile
    final RegularFileProperty file = project.objects.fileProperty()

    @Optional
    @InputDirectory
    final DirectoryProperty directory = project.objects.directoryProperty()

    private def artifacts = []

    @Override
    void runRemoteCommand(artifactoryClient) {

        def api = artifactoryClient.api().artifactApi()
        def theFile = file.orNull?.asFile
        def theDirectory = directory.orNull?.asFile

        if (theFile && theFile.exists() && !theFile.isDirectory()) {
            def payload = threadContextClassLoader.newPayload(theFile)
            artifacts.add(deployToArtifactory(api, repo(), artifactPath(), payload))
        } else if (theDirectory && theDirectory.exists() && theDirectory.isDirectory()) {

            def fileList = []
            theDirectory.eachFileRecurse(FileType.FILES) { fileList << it }
            if (fileList) {
                for (File it : fileList) {
                    def payload = threadContextClassLoader.newPayload(it)
                    String newBasePath = it.path.replaceFirst(theDirectory.path, "").replaceFirst("/", "")
                    def possibleArtifact = deployToArtifactory(api, repo(), "${artifactPath()}/${newBasePath}", payload)
                    artifacts.add(possibleArtifact)
                }
            } else {
                throw new GradleException("Could not find any regular files under directory @ ${directory.path}")
            }
        } else {
            throw new GradleException("`file` and/or `directory` are not valid")
        }
    }

    private def deployToArtifactory(def api, String repository, String itemPath, def payload) {
        println "deploy to ${repository}:${itemPath}"
        def possibleArtifact = api.deployArtifact(repository.toString(), itemPath.toString(), payload, gstringMapToStringMap(properties.get()))
        logger.quiet "Artifact successfully deployed @ ${possibleArtifact.repo}:${possibleArtifact.path}"
        possibleArtifact

    }

    def artifacts() { artifacts }
}
