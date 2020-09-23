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
import com.cdancy.gradle.artifactory.rest.tasks.search.Aql
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.Transformer
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class DeleteArtifact extends ArtifactAware {

    @Input
    @Optional
    final Property<Boolean> failFast = project.objects.property(Boolean).convention(false)

    @Input
    @Optional
    final Property<Long> requestInterval = project.objects.property(Long).convention(1000L)

    @Input
    @Optional
    final MapProperty<String, List<String>> artifacts = project.objects.mapProperty(String, List).convention([:])

    @Override
    void runRemoteCommand(artifactoryClient) {

        if (repo.present && artifactPath.present) {
            artifact(repo(), artifactPath())
        }
        def artifactsMap = artifacts.orNull
        if (artifactsMap && !artifactsMap.isEmpty()) {
            int errors = 0
            def requestIntervalMS = requestInterval.get()
            def shouldFailFast = failFast.get()

            def api = artifactoryClient.api().artifactApi()
            gstringMapToStringMap(artifactsMap).each { k, v ->
                v.each { it ->
                    String localRepo = k.toString()
                    String localPath = it.toString()
                    boolean success = api.deleteArtifact(localRepo, localPath)
                    if (success) {
                        logger.quiet("Successfully deleted artifact @ ${localRepo}:${localPath}")
                    } else {
                        String errorMessage = "Failed to delete artifact @ ${localRepo}:${localPath}"
                        if (shouldFailFast) {
                            throw new GradleException(errorMessage)
                        } else {
                            errors = errors++
                            logger.error(errorMessage)
                        }
                    }
                    sleep(requestIntervalMS)
                }
            }
            if (errors > 0)
                throw new GradleException("Failed to delete ${errors} artifact(s)")
        } else {
            logger.quiet "`artifacts` are empty. Nothing to do..."
        }
    }

    void artifact(String repo, String artifactPath) {
        def localRepo = checkString(repo)
        def localArtifactPath = checkString(artifactPath)
        List<String> possibleArtifacts = artifacts.get().get(localRepo)
        if (!possibleArtifacts) {
            possibleArtifacts = []
            artifacts.put(localRepo, possibleArtifacts)
        }

        if (!possibleArtifacts.contains(localArtifactPath)) {
            possibleArtifacts.add(localArtifactPath)
        }
    }

    void artifactsFromAql(def aql, Transformer resultTransformer = null) {
        dependsOn aql
        artifacts.set(project.provider {
            def artifactMap = [:]
            def aqlTask = aql instanceof Task ? aql : project.tasks.findByPath(aql)
            aqlTask.aqlResult().results.each { rawResult ->
                def result = resultTransformer ? resultTransformer.transform(rawResult) : rawResult
                if(result) {
                    def localRepo = checkString(result.repo)
                    def localArtifactPath = checkString(result.path)
                    artifactMap[localRepo] = artifactMap[localRepo] ?: []
                    artifactMap[localRepo].add(localArtifactPath)
                }
            }
            artifactMap
        })
    }
}
