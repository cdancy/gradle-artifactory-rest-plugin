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
package com.cdancy.gradle.artifactory.rest.tasks.build

import com.cdancy.gradle.artifactory.rest.tasks.AbstractArtifactoryRestTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class Promote extends AbstractArtifactoryRestTask {

    @Input
    Closure<String> buildName

    @Input
    Closure<Integer> buildNumber

    @Input
    @Optional
    Closure<String> status = { "promoted" }

    @Input
    @Optional
    Closure<String> comment = { "promoted build" }

    @Input
    @Optional
    boolean copy = true

    @Input
    @Optional
    boolean artifacts = true

    @Input
    @Optional
    boolean dependencies = false

    @Input
    @Optional
    Map<String, List<String>> properties

    @Input
    Closure<String> sourceRepo

    @Input
    Closure<String> targetRepo

    @Override
    void runRemoteCommand(artifactoryClient) {
        def api = artifactoryClient.api().buildApi()

        String localSourceRepo = sourceRepo()
        String localTargetRepo = targetRepo()
        def buildPromote = threadContextClassLoader.newBuildPromote(status.call(), comment.call(), null, null,
                false, localSourceRepo, localTargetRepo, copy,
                artifacts, dependencies, null, properties, true)

        String localBuildName = buildName()
        Integer localBuildNumber = buildNumber()
        def requestStatus = api.promote(localBuildName, localBuildNumber, buildPromote)
        if (requestStatus.messages().size() == 0 && requestStatus.errors().size() == 0) {
            logger.quiet("Successfully promoted buildName '${localBuildName}' with buildNumber '${localBuildNumber}' from '${localSourceRepo}' to '${localTargetRepo}'")
        } else {
            throw new GradleException("Failed promoting buildName '${localBuildName}' with buildNumber '${localBuildNumber}' from '${localSourceRepo}' to '${localTargetRepo}'")
        }
    }

    private String buildName() {
        String var = buildName ? buildName.call() : null
        if (var?.trim()) { var } else
            throw new GradleException("buildName does not resolve to a valid String: buildName=" + var)
    }

    private Integer buildNumber() {
        Integer var = buildNumber ? buildNumber.call() : null
        if (var) { var } else
            throw new GradleException("buildNumber does not resolve to a valid Integer: buildNumber=" + var)
    }

    private String sourceRepo() {
        String var = sourceRepo ? sourceRepo.call() : null
        if (var?.trim()) { var } else
            throw new GradleException("sourceRepo does not resolve to a valid String: sourceRepo=" + var)
    }

    private String targetRepo() {
        String var = targetRepo ? targetRepo.call() : null
        if (var?.trim()) { var } else
            throw new GradleException("targetRepo does not resolve to a valid String: targetRepo=" + var)
    }
}
