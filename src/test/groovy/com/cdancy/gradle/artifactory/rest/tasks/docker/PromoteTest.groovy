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
package com.cdancy.gradle.artifactory.rest.tasks.docker

import com.cdancy.gradle.artifactory.rest.ArtifactoryRestPlugin
import com.cdancy.gradle.artifactory.rest.utils.ThreadContextClassLoader
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 *
 * @author vagrant* @version $Id$ 3/5/20
 */
class PromoteTest extends Specification {
    Project project
    Promote promoteTask
    ThreadContextClassLoader threadContextClassLoader
    ArtifactoryClient artifactoryClient
    ArtifactoryApi artifactoryApi
    ArtifactoryDockerApi dockerApi

    def 'promotes image'() {
        given:
        dockerApi.promote('source-repo', _) >> true

        when:
        promoteTask.runRemoteCommand(artifactoryClient)

        then:
        1 * threadContextClassLoader.newPromote(
            'target-repo',
            'test/image',
            '1.0.0',
            null,
            false)
    }

    def 'promotes image with target tag'() {
        given:
        promoteTask.targetTag = { 'latest' }
        dockerApi.promote('source-repo', _) >> true

        when:
        promoteTask.runRemoteCommand(artifactoryClient)

        then:
        1 * threadContextClassLoader.newPromote(
            'target-repo',
            'test/image',
            '1.0.0',
            'latest',
            false)
    }

    def setup() {
        project = ProjectBuilder.builder().withName('root').build()
        project.plugins.apply ArtifactoryRestPlugin
        promoteTask = project.tasks.create('promoteImage', Promote) {
            repo = { 'source-repo' }
            promotedRepo = { 'target-repo' }
            image = { 'test/image' }
            tag = { '1.0.0' }
        }

        artifactoryClient = Mock()
        artifactoryApi = Mock()
        dockerApi = Mock()
        threadContextClassLoader = Mock()

        artifactoryClient.api() >> artifactoryApi
        artifactoryApi.dockerApi() >> dockerApi
        threadContextClassLoader.newPromote(_, _, _, _, _) >> Mock(PromoteImage)

        promoteTask.threadContextClassLoader = threadContextClassLoader
    }

    interface ArtifactoryClient {
        ArtifactoryApi api()
    }

    interface ArtifactoryApi {
        ArtifactoryDockerApi dockerApi()
    }

    interface ArtifactoryDockerApi {
        boolean promote(String repo, Object promoteImage)
    }

    interface PromoteImage {

    }
}
