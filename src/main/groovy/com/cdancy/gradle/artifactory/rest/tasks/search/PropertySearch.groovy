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
import org.gradle.api.tasks.Input

class PropertySearch extends AbstractArtifactoryRestTask {

    @Input
    Map<String, List<String>> properties = [:]

    @Input
    List<String> repos = []

    private def urls

    @Override
    void runRemoteCommand(artifactoryClient) {
        if (properties) {
            def api = artifactoryClient.api()
            urls = api.searchApi().propertySearch(properties, repos ? repos : null)
            logger.quiet("Found '${urls.size()}' artifacts with properties ${properties}")
        } else {
            logger.quiet "`properties` are empty. Nothing to do..."
        }
    }

    public def urls() { urls }
}

