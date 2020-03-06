/*
 * $Id$
 *
 * Copyright (c) 2020  Pegasystems Inc.
 * All rights reserved.
 *
 * This  software  has  been  provided pursuant  to  a  License
 * Agreement  containing  restrictions on  its  use.   The  software
 * contains  valuable  trade secrets and proprietary information  of
 * Pegasystems Inc and is protected by  federal   copyright law.  It
 * may  not be copied,  modified,  translated or distributed in  any
 * form or medium,  disclosed to third parties or used in any manner
 * not provided for in  said  License Agreement except with  written
 * authorization from Pegasystems Inc.
*/
package com.cdancy.gradle.artifactory.rest.tasks.artifact


import com.cdancy.gradle.artifactory.rest.ArtifactoryRestPlugin
import com.cdancy.gradle.artifactory.rest.utils.ThreadContextClassLoader
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.jclouds.io.Payload
import spock.lang.Specification

/**
 *
 * @author vagrant* @version $Id$ 3/6/20
 */
class DeployArtifactTest extends Specification {
    Project project
    DeployArtifact deployArtifactTask
    ArtifactoryClient artifactoryClient
    ArtifactoryApi artifactoryApi
    ArtifactoryArtifactApi artifactApi
    ThreadContextClassLoader threadContextClassLoader

    def 'does not deploy if file/directory are not set'() {
        when:
        deployArtifactTask.runRemoteCommand(artifactoryClient)

        then:
        thrown GradleException
    }


    def 'deploy artifact from file'() {
        given:
        def file = project.file('dummy.txt')
        file.text = 'dummy'
        deployArtifactTask.file = file

        when:
        deployArtifactTask.runRemoteCommand(artifactoryClient)

        then:
        1 * artifactApi.deployArtifact('test-repo', 'test/path', _, [:]) >> [:]
    }

    def 'deploy artifacts from directory'() {
        given:
        def dir = project.file('dummy')
        dir.mkdirs()
        def file1 = project.file("${dir}/dumm1.text")
        def file2 = project.file("${dir}/dumm2.text")
        file1.text = 'dummy'
        file2.text = 'dummy'

        deployArtifactTask.directory = dir

        when:
        deployArtifactTask.runRemoteCommand(artifactoryClient)

        then:
        1 * artifactApi.deployArtifact('test-repo', 'test/path/dumm1.text', _, [:]) >> [:]
        1 * artifactApi.deployArtifact('test-repo', 'test/path/dumm2.text', _, [:]) >> [:]
    }

    def setup() {
        project = ProjectBuilder.builder().withName('root').build()
        project.plugins.apply ArtifactoryRestPlugin
        deployArtifactTask = project.tasks.create('deployArtifact', DeployArtifact) {
            repo = 'test-repo'
            artifactPath = 'test/path'
        }

        artifactoryClient = Mock()
        artifactoryApi = Mock()
        artifactApi = Mock()
        threadContextClassLoader = Mock()
        threadContextClassLoader.newPayload(_) >> null

        artifactoryClient.api() >> artifactoryApi
        artifactoryApi.artifactApi() >> artifactApi
        deployArtifactTask.threadContextClassLoader = threadContextClassLoader
    }

    interface ArtifactoryClient {
        ArtifactoryApi api()
    }

    interface ArtifactoryApi {
        ArtifactoryArtifactApi artifactApi()
    }

    interface ArtifactoryArtifactApi {
        def deployArtifact(String repoKey, String itemPath, Payload inputStream, Map<String, List<String>> properties)
    }
}
