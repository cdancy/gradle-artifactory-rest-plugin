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
class PropertySearchTest extends Specification {
    Project project
    PropertySearch propertySearchTask
    ArtifactoryClient artifactoryClient
    ArtifactoryApi artifactoryApi
    ArtifactorySearchApi searchApi

    def 'does not search if properties are empty'() {
        when:
        propertySearchTask.runRemoteCommand(artifactoryClient)

        then:
        0 * searchApi.propertySearch([:], []) >> []
    }

    def 'searches properties'() {
        given:
        propertySearchTask.properties.put('test-prop', ['test-val'])

        when:
        propertySearchTask.runRemoteCommand(artifactoryClient)

        then:
        1 * searchApi.propertySearch(['test-prop': ['test-val']], []) >> []
    }

    def setup() {
        project = ProjectBuilder.builder().withName('root').build()
        project.plugins.apply ArtifactoryRestPlugin
        propertySearchTask = project.tasks.create('propertySearch', PropertySearch) {
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
        List propertySearch(Map<String, List<String>> properties, List<String> repos)
    }
}
