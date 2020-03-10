package com.cdancy.gradle.artifactory.rest.tasks.storage

import com.cdancy.gradle.artifactory.rest.ArtifactoryRestPlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DeletePropertiesTest extends Specification {
    Project project
    DeleteProperties deletePropertiesTask
    ArtifactoryClient artifactoryClient
    ArtifactoryApi artifactoryApi
    ArtifactoryStorageApi storageApi

    def 'skips delete properties if no properties defined'() {
        when:
        deletePropertiesTask.runRemoteCommand(artifactoryClient)

        then:
        0 * storageApi.deleteItemProperties('test-repo', 'test/artifact/path/for.jar', _) >> true
    }

    def 'delete properties'() {
        given:
        deletePropertiesTask.properties.add('prop1')

        when:
        deletePropertiesTask.runRemoteCommand(artifactoryClient)

        then:
        1 * storageApi.deleteItemProperties('test-repo', 'test/artifact/path/for.jar', ['prop1']) >> true
    }

    def setup() {
        project = ProjectBuilder.builder().withName('root').build()
        project.plugins.apply ArtifactoryRestPlugin
        deletePropertiesTask = project.tasks.create('deleteProperties', DeleteProperties) {
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
        boolean deleteItemProperties(String repo, String path, List<String> props)
    }
}
