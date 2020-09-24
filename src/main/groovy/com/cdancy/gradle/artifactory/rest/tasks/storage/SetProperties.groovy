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

class SetProperties extends ArtifactAware {

    @Input
    final MapProperty<String, List<String>> properties = project.objects.mapProperty(String, List).convention([:])

    @Input
    @Optional
    final Property<Long> requestInterval = project.objects.property(Long).convention(1000L)

    @Input
    @Optional
    final MapProperty<String, List<String>> artifacts = project.objects.mapProperty(String, List).convention([:])

    @Input
    @Optional
    final Property<Integer> retries = project.objects.property(Integer).convention(0)

    @Override
    void runRemoteCommand(artifactoryClient) {
        def propertyMap = properties.orNull
        if (propertyMap && !propertyMap.isEmpty()) {
            propertyMap = gstringMapToStringMap(propertyMap)

            if (repo.present && artifactPath.present) {
                artifact(repo(), artifactPath())
            }

            def artifactList = artifacts.orNull
            if (artifactList && !artifactList.isEmpty()) {
                def retryCount = retries.get()
                def requestIntervalMs = requestInterval.get()

                def api = artifactoryClient.api()
                artifactList.each { k, v ->
                    v.each { it ->
                        boolean success
                        int retriesLeft = retryCount
                        while (!(success = setProperty(api, k.toString(), it.toString(), propertyMap)) && (retriesLeft > 0)) {
                            logger.debug("Could not successfully set properties '${propertyMap}' @ " +
                                "${repo}:${path}, will retry after ${requestInterval}ms")
                            retriesLeft -= 1
                            sleep(requestIntervalMs)
                        }
                        if (!success) {
                            throw new GradleException("Could not successfully set properties '${propertyMap}' @ " +
                                "${k}:${it}")
                        }
                        sleep(requestIntervalMs)
                    }
                }
            } else {
                logger.quiet "`artifacts` are empty. Nothing to do..."
            }
        } else {
            logger.quiet "`properties` are empty. Nothing to do..."
        }
    }

    protected boolean setProperty(api, repo, path, Map<String, List<String>> map) {
        try {
            if (api.storageApi().setItemProperties(repo, path, map)) {
                return true
            } else {
                logger.debug("Could not successfully set properties '${map}' @ " +
                    "${repo}:${path}")
                return false
            }
        } catch (e) {
            logger.debug("Could not successfully set properties '${map}' @ " +
                "${repo}:${path}", e)
            return false
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

    void artifactsFromAql(def aql, String repo, Transformer resultTransformer = null) {
        dependsOn aql
        def localRepo = checkString(repo)
        artifacts.set(project.provider {
            def artifactMap = [:]
            def aqlTask = aql instanceof Task ? aql : project.tasks.findByPath(aql)
            aqlTask.aqlResult().results.each { rawResult ->
                def result = resultTransformer ? resultTransformer.transform(rawResult) : rawResult
                if(result) {
                    def localArtifactPath = checkString(result.path)
                    artifactMap[localRepo] = artifactMap[localRepo] ?: []
                    artifactMap[localRepo].add(localArtifactPath)
                }
            }
            artifactMap
        })
    }
}

