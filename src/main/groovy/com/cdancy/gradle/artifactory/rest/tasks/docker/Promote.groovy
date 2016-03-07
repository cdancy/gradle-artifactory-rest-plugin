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
package com.cdancy.gradle.artifactory.rest.tasks.docker

import com.cdancy.gradle.artifactory.rest.tasks.AbstractArtifactoryRestTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input

class Promote extends AbstractArtifactoryRestTask {

    @Input
    Closure<String> promotedRepo

    @Input
    Closure<String> image

    @Input
    Closure<String> tag

    @Input
    boolean copy

    @Override
    void runRemoteCommand(artifactoryClient) {
        def api = artifactoryClient.api().dockerApi()
        def dockerPromote = threadContextClassLoader.newPromote(promotedRepo(), image(), tag(), copy)
        boolean success = api.promote(repo(), dockerPromote)
        if (success) {
            logger.quiet("Successfully promoted image @ ${repo()}/${image()}:${tag()} to ${promotedRepo()}")
        } else {
            throw new GradleException("Failed promoting image @ ${repo()}/${image()}:${tag()} to ${promotedRepo()}")
        }
    }

    private String promotedRepo() {
        String var = promotedRepo ? promotedRepo.call() : null
        if (var?.trim()) {
            var
        } else {
            throw new GradleException("promotedRepo does not resolve to a valid String: promotedRepo=" + var)
        }
    }

    private String image() {
        String var = image ? image.call() : null
        if (var?.trim()) {
            var
        } else {
            throw new GradleException("image does not resolve to a valid String: image=" + var)
        }
    }

    private String tag() {
        String var = tag ? tag.call() : null
        if (var?.trim()) {
            var
        } else {
            throw new GradleException("tag does not resolve to a valid String: tag=" + var)
        }
    }
}
