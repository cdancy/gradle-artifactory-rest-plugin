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
package com.cdancy.gradle.artifactory.rest.tasks

import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

abstract class GAVCAware extends AbstractArtifactoryRestTask {

    @Input
    @Optional
    final Property<String> groupName = project.objects.property(String)

    @Input
    @Optional
    final Property<String> artifactName = project.objects.property(String)

    @Input
    @Optional
    final Property<String> versionName = project.objects.property(String)

    @Input
    @Optional
    final Property<String> classifierName = project.objects.property(String)

    String groupName() {
        validateString(groupName)
    }

    String artifactName() {
        validateString(artifactName)
    }

    String versionName() {
        validateString(versionName)
    }

    String classifierName() {
        validateString(classifierName)
    }

    private String validateString(Property<String> property) {
        String var = property.orNull
        if (var?.trim()) {
            var
        } else {
            throw new GradleException("closure does not resolve to a valid String")
        }
    }
}

