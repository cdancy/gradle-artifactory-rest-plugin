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
import org.gradle.api.GradleException
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class LatestVersionFromLayout extends GAVCAware {

    @Input
    @Optional
    final Property<Long> sleepTime = project.objects.property(Long).convention(10000L)

    @Input
    @Optional
    final Property<Integer> retries = project.objects.property(Integer).convention(5)

    @Input
    @Optional
    final ListProperty<String> repos = project.objects.listProperty(String).convention([])

    // remote=1 to query mirror artifactory
    @Input
    @Optional
    final Property<String> remote = project.objects.property(String)

    private String version

    @Override
    void runRemoteCommand(artifactoryClient) {
        def sleep = sleepTime.get()
        def retry = retries.get()

        // check sanity of passed sleep/retry options
        if (sleep < 0) {
            throw new GradleException("Parameter sleepTime can NOT be less than 0");
        }
        if (retry < 0) {
            throw new GradleException("Parameter retries can NOT be less than 0");
        }

        int localRetries = retry
        while (localRetries >= 0) {
            def someExceptionType
            try {
                version = artifactoryClient.api().searchApi().latestVersionWithLayout(groupName().toString(),
                    artifactName().toString(),
                    versionName().toString(),
                    remote.orNull,
                    repos.orNull)
                break
            } catch (ExceptionInInitializerError error) {
                someExceptionType = error
            } catch (Exception | Error exception) {
                someExceptionType = exception
            }

            if (someExceptionType) {
                if (localRetries > 0) {
                    logger.quiet "Failed querying for latestVersionFromLayout: message=${someExceptionType.message}"
                    sleep(sleep)
                } else if (localRetries == 0) {
                    throw someExceptionType
                }
                localRetries--
            }
        }
    }

    String version() { version }
}

