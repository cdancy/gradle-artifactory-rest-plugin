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
package com.cdancy.gradle.artifactory.rest.tasks.storage

import com.cdancy.gradle.artifactory.rest.tasks.AbstractArtifactoryRestTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input

class SetProperties extends AbstractArtifactoryRestTask {

    @Input
    Map<String, String> properties = [:]

    @Override
    void runRemoteCommand(artifactoryClient) {
        if (properties) {
            def api = artifactoryClient.api()
            boolean success = api.storageApi().setItemProperties(repo(), artifactPath(), properties)
            if (success) {
                logger.quiet("Properties '${properties}' added @ ${repo()}:${artifactPath()}")
            } else {
                throw new GradleException("Could not successfully set properties: '${properties}', " +
                        "repo=${repo()}, artifactPath=${artifactPath()}")
            }
        } else {
            logger.quiet "`properties` are empty. Nothing to do..."
        }
    }
}

