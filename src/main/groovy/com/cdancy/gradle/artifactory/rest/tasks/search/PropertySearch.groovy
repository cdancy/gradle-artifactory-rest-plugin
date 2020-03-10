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
package com.cdancy.gradle.artifactory.rest.tasks.search

import com.cdancy.gradle.artifactory.rest.tasks.AbstractArtifactoryRestTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class PropertySearch extends AbstractArtifactoryRestTask {

    @Input
    final MapProperty<String, List<String>> properties = project.objects.mapProperty(String, List).convention([:])

    @Input
    @Optional
    final ListProperty<String> repos = project.objects.listProperty(String).convention([])

    private List<?> results = []

    @Override
    void runRemoteCommand(artifactoryClient) {
        def props = properties.orNull
        if (props && !props.isEmpty()) {
            def api = artifactoryClient.api()
            results.addAll(api.searchApi().propertySearch(gstringMapToStringMap(props), repos.orNull))
            logger.quiet("Found '${results.size()}' artifacts with properties ${properties}")
        } else {
            logger.quiet "`properties` are empty. Nothing to do..."
        }
    }

    List<?> results() { results }
}
