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
package com.github.gradle.artifactory.rest.tasks.artifact

import com.github.gradle.artifactory.rest.tasks.AbstractArtifactoryRestTask
import groovy.io.FileType
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile

class DeployArtifact extends AbstractArtifactoryRestTask {

    @Input
    Closure<String> repo

    @Input
    Closure<String> artifactPath

    @Optional
    @Input
    Map<String, String> properties = [:]

    @Optional
    @Input
    File file

    @Optional
    @Input
    File directory

    private def deployedArtifact

    @Override
    void runRemoteCommand(artifactoryClient) {
        String tempRepo = repo ? repo.call() : null
        String tempArtifactPath = artifactPath ? artifactPath.call() : null
        if (tempRepo?.trim() && tempArtifactPath?.trim()) {

            def api = artifactoryClient.api().artifactApi()

            if (file && file.exists() && !file.isDirectory()) {
                def payload = threadContextClassLoader.newPayload(file)
                deployedArtifact = deployToArtifactory(api, tempRepo, tempArtifactPath, payload)
            } else if (directory && directory.exists() && directory.isDirectory()) {

                def fileList = []
                directory.eachFileRecurse (FileType.FILES) { fileList << it }
                if (fileList) {
                    deployedArtifact = [] // set deployedArtifact to be a List as we have potentially N number of files
                    for (File it : fileList) {
                        def payload = threadContextClassLoader.newPayload(it)
                        String newBasePath = it.path.replaceFirst(directory.path, "").replaceFirst("/", "")
                        def possibleArtifact = deployToArtifactory(api, tempRepo, "${tempArtifactPath}/${newBasePath}", payload)
                        deployedArtifact.add(possibleArtifact)
                    }
                } else {
                    throw new GradleException("Could not find any regular files under directory @ ${directory.path}")
                }
            } else {
                throw new GradleException("`file` and/or `directory` are not valid")
            }
        } else {
            throw new GradleException("`repo` and `artifactPath` do not resolve to " +
                    "valid Strings: repo=${tempRepo}, artifactPath=${tempArtifactPath}")
        }
    }

    private def deployToArtifactory(def api, String repository, String itemPath, def payload) {
        println "deploy to ${repository}:${itemPath}"
        def possibleArtifact = api.deployArtifact(repository, itemPath, payload, properties)
        logger.quiet "Artifact successfully deployed @ ${possibleArtifact.repo}:${possibleArtifact.path}"
        possibleArtifact

    }

    public def deployedArtifact() { deployedArtifact }
}