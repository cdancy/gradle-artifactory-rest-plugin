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
package com.cdancy.gradle.artifactory.rest.tasks.search

import com.cdancy.gradle.artifactory.rest.ArtifactoryRestPlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 *
 * @author vagrant* @version $Id$ 3/6/20
 */
class AqlTest extends Specification {
    Project project
    Aql aqlTask
    ArtifactoryClient artifactoryClient
    ArtifactoryApi artifactoryApi
    ArtifactorySearchApi searchApi

    def 'searches artifactory'() {
        when:
        aqlTask.runRemoteCommand(artifactoryClient)

        then:
        1 * searchApi.aql('some query') >> []
    }

    def setup() {
        project = ProjectBuilder.builder().withName('root').build()
        project.plugins.apply ArtifactoryRestPlugin
        aqlTask = project.tasks.create('aql', Aql) {
            query = 'some query'
        }

        artifactoryClient = Mock()
        artifactoryApi = Mock()
        searchApi = Mock()

        artifactoryClient.api() >> artifactoryApi
        artifactoryApi.searchApi() >> searchApi
    }

    interface ArtifactoryClient {
        ArtifactoryApi api()
    }

    interface ArtifactoryApi {
        ArtifactorySearchApi searchApi()
    }

    interface ArtifactorySearchApi {
        def aql(String query)
    }
}
