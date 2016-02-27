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
package com.github.gradle.artifactory.rest.tasks.artifact

import com.github.gradle.artifactory.rest.tasks.AbstractArtifactoryRestTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile

class RetrieveArtifact extends AbstractArtifactoryRestTask {

    @Input
    Closure<String> repo

    @Input
    Closure<String> artifactPath

    @Optional
    @OutputFile
    File destinationFile

    @Optional
    @OutputDirectory
    File destinationDir

    @Override
    void runRemoteCommand(artifactoryClient) {
        String tempRepo = repo ? repo.call() : null
        String tempArtifactPath = artifactPath ? artifactPath.call() : null
        if (tempRepo?.trim() && tempArtifactPath?.trim()) {

            def api = artifactoryClient.api().artifactApi()
            if (destinationFile) {
                writeStreamToDisk(api, tempRepo, tempArtifactPath, destinationFile)
            } else if (destinationDir) {
                File nestedFile = new File(destinationDir, new File(tempArtifactPath).name)
                writeStreamToDisk(api, tempRepo, tempArtifactPath, nestedFile)
            } else {
                throw new GradleException("Must specify at least one of destinationFile or destinationDir")
            }
        } else {
            throw new GradleException("`repo` and `artifactPath` do not resolve to " +
                    "valid Strings: repo=${tempRepo}, artifactPath=${tempArtifactPath}")
        }
    }

    private void writeStreamToDisk(def api, String localRepo, String localPath, File destination) {
        InputStream inputStream = api.retrieveArtifact(localRepo, localPath)
        if (inputStream) {
            threadContextClassLoader.copyInputStreamToFile(inputStream, destination)
        } else  {
            throw new GradleException("Artifact does not exist @ ${localRepo}:${localPath}")
        }
    }
}
