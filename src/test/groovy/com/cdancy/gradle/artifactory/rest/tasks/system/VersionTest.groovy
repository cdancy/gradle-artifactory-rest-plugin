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
package com.cdancy.gradle.artifactory.rest.tasks.system

import com.cdancy.gradle.artifactory.rest.ArtifactoryRestPlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 *
 * @author vagrant* @version $Id$ 3/6/20
 */
class VersionTest extends Specification {
    Project project
    Version versionTask
    ArtifactoryClient artifactoryClient
    ArtifactoryApi artifactoryApi
    ArtifactorySystemApi systemApi

    def 'gets version'() {
        when:
        versionTask.runRemoteCommand(artifactoryClient)

        then:
        1 * systemApi.version()
    }

    def setup() {
        project = ProjectBuilder.builder().withName('root').build()
        project.plugins.apply ArtifactoryRestPlugin
        versionTask = project.tasks.create('version', Version)

        artifactoryClient = Mock()
        artifactoryApi = Mock()
        systemApi = Mock()

        artifactoryClient.api() >> artifactoryApi
        artifactoryApi.systemApi() >> systemApi
    }


    interface ArtifactoryClient {
        ArtifactoryApi api()
    }

    interface ArtifactoryApi {
        ArtifactorySystemApi systemApi()
    }

    interface ArtifactorySystemApi {
        def version()
    }
}
