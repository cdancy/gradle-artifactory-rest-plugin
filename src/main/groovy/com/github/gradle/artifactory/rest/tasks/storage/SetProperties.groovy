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
package com.github.gradle.artifactory.rest.tasks.storage

import com.github.gradle.artifactory.rest.tasks.AbstractArtifactoryRestTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input

class SetProperties extends AbstractArtifactoryRestTask {

    @Input
    Closure<String> repo

    @Input
    Closure<String> artifactPath

    @Input
    Map<String, String> properties = [:]

    @Override
    void runRemoteCommand(artifactoryClient) {
        String tempRepo = repo ? repo.call() : null
        String tempArtifactPath = artifactPath ? artifactPath.call() : null
        if (tempRepo?.trim() && tempArtifactPath?.trim()) {
            if (properties) {

                def storageApi = artifactoryClient.api().storageApi()
                def setItemProperties = threadContextClassLoader.createSetItemProperties()

                // due to "bug" in the way artifactory requires you to specify properties
                // we have to set them one at a time
                properties.each { k,v ->
                    setItemProperties.add(k.toString(), v.toString())
                    boolean success = storageApi.setItemProperties(tempRepo, tempArtifactPath, setItemProperties)
                    if (!success) {
                        throw new GradleException("Could not successfully set property: ${k}=${v}, " +
                                "repo=${tempRepo}, artifactPath=${tempArtifactPath}")
                    } else {
                        logger.quiet("Property '${k}'='${v}' added @ ${tempRepo}:${tempArtifactPath}")
                    }
                }
            } else {
                logger.quiet "`properties` are empty. Nothing to do..."
            }
        } else {
            throw new GradleException("`repo` and `artifactPath` do not resolve to " +
                    "valid Strings: repo=${tempRepo}, artifactPath=${tempArtifactPath}")
        }
    }
}

