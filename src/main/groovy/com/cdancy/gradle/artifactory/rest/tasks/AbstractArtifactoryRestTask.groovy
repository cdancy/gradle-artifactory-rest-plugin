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

import com.cdancy.gradle.artifactory.rest.utils.ThreadContextClassLoader
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

abstract class AbstractArtifactoryRestTask extends DefaultTask {

    String checkString(String dirtyString) {
        if (dirtyString == null) throw new GradleException("String to sanitize must not be null");
        String tempString = dirtyString.trim();
        if (tempString.length() > 0) {
            return tempString;
        } else {
            throw new GradleException("String to sanitize must not be empty")
        }
    }

    Map<String, List<String>> gstringMapToStringMap(Map<String, List<String>> gStringMap) {
        Map<String, List<String>> convertedMap = new HashMap<>();
        gStringMap.each { k, v ->
            convertedMap.put(k.toString(), v*.toString())
        }
        convertedMap
    }

    String randomString() {
        UUID.randomUUID().toString().replaceAll("-", "")
    }

    ThreadContextClassLoader threadContextClassLoader

    @TaskAction
    void start() {
        runInArtifactoryClassPath { artifactoryClient ->
            runRemoteCommand(artifactoryClient)
        }
    }

    void runInArtifactoryClassPath(Closure closure) {
        threadContextClassLoader.withClosure(closure)
    }

    abstract void runRemoteCommand(artifactoryClient)
}
