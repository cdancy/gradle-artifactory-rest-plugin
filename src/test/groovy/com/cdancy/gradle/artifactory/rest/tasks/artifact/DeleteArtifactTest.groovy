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
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 *
 * @author vagrant* @version $Id$ 3/6/20
 */
class DeleteArtifactTest extends Specification {
    Project project
    DeleteArtifact deleteArtifactTask
    ArtifactoryClient artifactoryClient
    ArtifactoryApi artifactoryApi
    ArtifactoryArtifactApi artifactApi

    def 'does not delete if artifacts are empty'() {
        when:
        deleteArtifactTask.runRemoteCommand(artifactoryClient)

        then:
        0 * artifactApi.deleteArtifact(_, _) >> false
    }

    def 'delete artifacts'() {
        given:
        deleteArtifactTask.artifact('source-repo-1', 'source-path-1')
        deleteArtifactTask.artifact('source-repo-1', 'source-path-2')
        deleteArtifactTask.artifact('source-repo-3', 'source-path-3')

        when:
        deleteArtifactTask.runRemoteCommand(artifactoryClient)

        then:
        1 * artifactApi.deleteArtifact('source-repo-1', 'source-path-1') >> true
        1 * artifactApi.deleteArtifact('source-repo-1', 'source-path-2') >> true
        1 * artifactApi.deleteArtifact('source-repo-3', 'source-path-3') >> true
    }

    def setup() {
        project = ProjectBuilder.builder().withName('root').build()
        project.plugins.apply ArtifactoryRestPlugin
        deleteArtifactTask = project.tasks.create('deleteArtifact', DeleteArtifact)

        artifactoryClient = Mock()
        artifactoryApi = Mock()
        artifactApi = Mock()

        artifactoryClient.api() >> artifactoryApi
        artifactoryApi.artifactApi() >> artifactApi
    }

    interface ArtifactoryClient {
        ArtifactoryApi api()
    }

    interface ArtifactoryApi {
        ArtifactoryArtifactApi artifactApi()
    }

    interface ArtifactoryArtifactApi {
        boolean deleteArtifact(String repoKey, String itemPath)
    }
}
