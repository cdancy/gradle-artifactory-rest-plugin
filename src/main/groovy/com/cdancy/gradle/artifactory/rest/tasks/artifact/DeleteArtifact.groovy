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
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class DeleteArtifact extends ArtifactAware {

    @Input
    @Optional
    boolean failFast = false

    @Input
    @Optional
    long requestInterval = 1000

    @Input
    @Optional
    Map<String, List<String>> artifacts = new HashMap<>();

    @Override
    void runRemoteCommand(artifactoryClient) {

        if (repo != null && artifactPath != null) {
            artifact(repo(), artifactPath())
        }

        if (artifacts) {
            int errors = 0
            def api = artifactoryClient.api().artifactApi()
            gstringMapToStringMap(artifacts).each { k, v ->
                v.each { it ->
                    String localRepo = k.toString()
                    String localPath = it.toString()
                    boolean success = api.deleteArtifact(localRepo, localPath)
                    if (success) {
                        logger.quiet("Successfully deleted artifact @ ${localRepo}:${localPath}")
                    } else {
                        String errorMessage = "Failed to delete artifact @ ${localRepo}:${localPath}"
                        if (failFast) {
                            throw new GradleException(errorMessage)
                        } else {
                            errors = errors++
                            logger.error(errorMessage)
                        }
                    }
                    sleep(requestInterval)
                }
            }
            if (errors > 0)
                throw new GradleException("Failed to delete ${errors} artifact(s)")
        } else {
            logger.quiet "`artifacts` are empty. Nothing to do..."
        }
    }

    void artifact(String repo, String artifactPath) {
        repo = checkString(repo);
        artifactPath = checkString(artifactPath)
        List<String> possibleArtifacts = artifacts.get(repo);
        if (possibleArtifacts == null) {
            possibleArtifacts = new ArrayList<>()
            artifacts.put(repo, possibleArtifacts)
        }

        if (!possibleArtifacts.contains(artifactPath))
            possibleArtifacts.add(artifactPath)

        if (possibleArtifacts.isEmpty())
            artifacts.remove(repo)
    }
}
