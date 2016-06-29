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

import com.cdancy.gradle.artifactory.rest.tasks.GAVCAware
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class LatestVersionFromLayout extends GAVCAware {

    @Input
    @Optional
    List<String> repos = []

    private String version

    @Override
    void runRemoteCommand(artifactoryClient) {
        version = artifactoryClient.api().searchApi().latestVersionWithLayout(groupName().toString(),
                artifactName().toString(),
                versionName().toString(),
                repos ? repos : null)
    }

    String version() { version }
}

