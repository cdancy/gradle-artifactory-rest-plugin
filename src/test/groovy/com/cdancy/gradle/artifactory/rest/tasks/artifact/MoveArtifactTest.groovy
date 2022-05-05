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
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 *
 * @author vagrant* @version $Id$ 3/6/20
 */
class MoveArtifactTest extends Specification {
    Project project
    MoveArtifact moveArtifactTask
    ArtifactoryClient artifactoryClient
    ArtifactoryApi artifactoryApi
    ArtifactoryArtifactApi artifactApi

    def 'does not move if artifacts are empty'() {
        when:
        moveArtifactTask.runRemoteCommand(artifactoryClient)

        then:
        0 * artifactApi.moveArtufact(_, _, _, _) >> null
    }

    def 'move artifacts'() {
        given:
        def status = com.cdancy.artifactory.rest.domain.error.RequestStatus.create([], [])
        moveArtifactTask.artifact('source-repo-1', 'source-path-1', 'target-repo-1', 'target-path-1')
        moveArtifactTask.artifact('source-repo-1', 'source-path-2', 'target-repo-2', 'target-path-2')

        when:
        moveArtifactTask.runRemoteCommand(artifactoryClient)

        then:
        1 * artifactApi.moveArtifact('source-repo-1', 'source-path-1', 'target-repo-1/target-path-1') >> status
        1 * artifactApi.moveArtifact('source-repo-1', 'source-path-2', 'target-repo-2/target-path-2') >> status
    }

    def 'move artifacts from Aql results'() {
        given:
        def status = com.cdancy.artifactory.rest.domain.error.RequestStatus.create([], [])
        AqlTask aql = project.tasks.create('aql', AqlTask) {
            aqlResult = Mock(AqlResults)
            aqlResult.results >> [[repo: 'source-repo-1', path: 'source-path-1'],[repo: 'source-repo-2', path: 'source-path-2']]
        }
        moveArtifactTask.artifactsFromAql('aql', 'target-repo')

        when:
        moveArtifactTask.runRemoteCommand(artifactoryClient)

        then:
        1 * artifactApi.moveArtifact('source-repo-1', 'source-path-1', 'target-repo/source-path-1') >> status
        1 * artifactApi.moveArtifact('source-repo-2', 'source-path-2', 'target-repo/source-path-2') >> status
    }

    def 'move artifacts from Aql results with transformer'() {
        given:
        def status = com.cdancy.artifactory.rest.domain.error.RequestStatus.create([], [])
        AqlTask aql = project.tasks.create('aql', AqlTask) {
            aqlResult = Mock(AqlResults)
            aqlResult.results >> [[repo: 'source-repo-1', path: 'source-path-1'],[repo: 'source-repo-2', path: 'source-path-2']]
        }
        moveArtifactTask.artifactsFromAql(aql, 'target-repo') {
            [repo: it.repo, path: "art/$it.path"]
        }

        when:
        moveArtifactTask.runRemoteCommand(artifactoryClient)

        then:
        1 * artifactApi.moveArtifact('source-repo-1', 'art/source-path-1', 'target-repo/art/source-path-1') >> status
        1 * artifactApi.moveArtifact('source-repo-2', 'art/source-path-2', 'target-repo/art/source-path-2') >> status
    }

    def setup() {
        project = ProjectBuilder.builder().withName('root').build()
        project.plugins.apply ArtifactoryRestPlugin
        moveArtifactTask = project.tasks.create('moveArtifact', MoveArtifact)

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
        def moveArtifact(String sourceRepo, String sourcePath, String targetPath)
    }

    interface AqlResults {
        List getResults()
    }

    static class AqlTask extends DefaultTask {
        AqlResults aqlResult

        def aqlResult() { aqlResult }
    }
}
