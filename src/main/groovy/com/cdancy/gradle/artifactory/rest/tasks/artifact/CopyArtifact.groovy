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

import com.cdancy.gradle.artifactory.rest.tasks.AbstractArtifactoryRestTask
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class CopyArtifact extends AbstractArtifactoryRestTask {

    @Input
    @Optional
    final Property<Long> requestInterval = project.objects.property(Long).convention(1000L)

    @Input
    @Optional
    final MapProperty<String, ArtifactToCopy> artifacts = project.objects.mapProperty(String, ArtifactToCopy).convention([:])

    private List<?> requestStatus = []

    @Override
    void runRemoteCommand(artifactoryClient) {
        def artifactMap = artifacts.orNull
        if (artifactMap && !artifactMap.isEmpty()) {
            def api = artifactoryClient.api()
            def outerRequestInterval = this.requestInterval.get()
            def outerRequestStatus = this.requestStatus
            artifactMap.each { k, v ->

                def innerRequestInterval = outerRequestInterval
                def innerRequestStatus = outerRequestStatus
                v.each { it ->

                    String completeSourceRepo = (it.sourceRepo.toString() + ":" + it.sourcePath.toString())
                    String completeTargetRepo = (it.targetRepo.toString() + "/" + it.targetPath.toString()).replaceAll("//", "/")
                    def status = api.artifactApi().copyArtifact(it.sourceRepo.toString(),
                        it.sourcePath.toString(),
                        completeTargetRepo.toString())

                    if (status.errors().size() > 0) {
                        status.errors().each { error ->
                            logger.quiet "Found error copying artifact @ ${completeSourceRepo} to ${completeTargetRepo.replaceFirst("/", ":")}: status=${error.status()}, message=${error.message()}"
                        }
                    }

                    if (status.messages().size() > 0) {
                        status.messages().each { message ->
                            if (message.level().equalsIgnoreCase("info")) {
                                logger.quiet "Copied artifact @ ${completeSourceRepo} to ${completeTargetRepo.replaceFirst("/", ":")}"
                            } else {
                                logger.quiet "Found illegal message copying artifact @ ${completeSourceRepo} to ${completeTargetRepo.replaceFirst("/", ":")}: level=${message.level()}, message=${message.message()}"
                            }
                        }
                    }

                    innerRequestStatus.add(status)
                    sleep(innerRequestInterval)
                }
            }
        } else {
            logger.quiet "`artifacts` are empty. Nothing to do..."
        }
    }

    void artifact(String sourceRepo, String sourcePath, String targetRepo, String targetPath) {
        def localSourceRepo = checkString(sourceRepo)
        def localSourcePath = checkString(sourcePath)
        def localTargetRepo = checkString(targetRepo)
        def localTargetPath = checkString(targetPath)
        artifacts.put(localSourceRepo + "/" + localSourcePath,
            new ArtifactToCopy(sourceRepo: localSourceRepo,
                sourcePath: localSourcePath,
                targetRepo: localTargetRepo,
                targetPath: localTargetPath)
        )
    }

    static class ArtifactToCopy {
        @Input
        String sourceRepo
        @Input
        String sourcePath
        @Input
        String targetRepo
        @Input
        String targetPath
    }

    List<?> requestStatus() { requestStatus }
}
