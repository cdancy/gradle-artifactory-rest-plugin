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

import com.cdancy.gradle.artifactory.rest.tasks.ArtifactAware
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class Promote extends ArtifactAware {

    @Input
    final Property<String> promotedRepo = project.objects.property(String)

    @Input
    final Property<String> image = project.objects.property(String)

    @Input
    final Property<String> tag = project.objects.property(String)

    @Input
    @Optional
    final Property<String> targetTag = project.objects.property(String)

    @Input
    final Property<Boolean> copy = project.objects.property(Boolean).convention(false)

    @Override
    void runRemoteCommand(artifactoryClient) {
        def api = artifactoryClient.api().dockerApi()
        def dockerPromote = threadContextClassLoader.newPromote(promotedRepo(), image(), tag(), targetTag(), copy.get())
        boolean success = api.promote(repo().toString(), dockerPromote)
        if (success) {
            logger.quiet("Successfully promoted image @ ${repo()}/${image()}:${tag()} to ${promotedRepo()}")
        } else {
            throw new GradleException("Failed promoting image @ ${repo()}/${image()}:${tag()} to ${promotedRepo()}")
        }
    }

    private String promotedRepo() {
        String var = promotedRepo.orNull
        if (var?.trim()) {
            var
        } else {
            throw new GradleException("promotedRepo does not resolve to a valid String: promotedRepo=" + var)
        }
    }

    private String image() {
        String var = image.orNull
        if (var?.trim()) {
            var
        } else {
            throw new GradleException("image does not resolve to a valid String: image=" + var)
        }
    }

    private String tag() {
        String var = tag.orNull
        if (var?.trim()) {
            var
        } else {
            throw new GradleException("tag does not resolve to a valid String: tag=" + var)
        }
    }

    private String targetTag() {
        String var = targetTag.orNull
        if (!var || var?.trim()) {
            var
        } else {
            throw new GradleException("targetTag does not resolve to a valid String: tag=" + var)
        }
    }
}
