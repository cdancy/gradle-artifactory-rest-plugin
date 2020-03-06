package com.cdancy.gradle.artifactory.rest.tasks.storage

import com.cdancy.gradle.artifactory.rest.ArtifactoryRestPlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class GetPropertiesTest extends Specification {
    Project project
    GetProperties getPropertiesTask
    ArtifactoryClient artifactoryClient
    ArtifactoryApi artifactoryApi
    ArtifactoryStorageApi storageApi

    def 'get properties'() {
        when:
        getPropertiesTask.runRemoteCommand(artifactoryClient)

        then:
        1 * storageApi.getItemProperties('test-repo', 'test/artifact/path/for.jar') >> [:]
    }

    def setup() {
        project = ProjectBuilder.builder().withName('root').build()
        project.plugins.apply ArtifactoryRestPlugin
        getPropertiesTask = project.tasks.create('getProperties', GetProperties) {
            repo = 'test-repo'
            artifactPath = 'test/artifact/path/for.jar'
        }

        artifactoryClient = Mock()
        artifactoryApi = Mock()
        storageApi = Mock()

        artifactoryClient.api() >> artifactoryApi
        artifactoryApi.storageApi() >> storageApi
    }

    interface ArtifactoryClient {
        ArtifactoryApi api()
    }

    interface ArtifactoryApi {
        ArtifactoryStorageApi storageApi()
    }

    interface ArtifactoryStorageApi {
        Map getItemProperties(String repo, String path)
    }
}
