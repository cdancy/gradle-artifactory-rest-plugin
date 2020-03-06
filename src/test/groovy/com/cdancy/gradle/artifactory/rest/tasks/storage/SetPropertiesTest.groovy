package com.cdancy.gradle.artifactory.rest.tasks.storage

import com.cdancy.gradle.artifactory.rest.ArtifactoryRestPlugin
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class SetPropertiesTest extends Specification {
    Project project
    SetProperties setPropertiesTask
    ArtifactoryClient artifactoryClient
    ArtifactoryApi artifactoryApi
    ArtifactoryStorageApi storageApi

    def 'sets properties'() {
        when:
        setPropertiesTask.runRemoteCommand(artifactoryClient)

        then:
        1 * storageApi.setItemProperties('test-repo', 'test/artifact/path/for.jar', _) >> true
    }

    def 'sets properties fails'() {
        given:
        storageApi.setItemProperties('test-repo', 'test/artifact/path/for.jar', _) >> false

        when:
        setPropertiesTask.runRemoteCommand(artifactoryClient)

        then:
        thrown(GradleException)
    }

    def 'sets properties with retry'() {
        given:
        setPropertiesTask.retries = 1

        when:
        setPropertiesTask.runRemoteCommand(artifactoryClient)

        then:
        2 * storageApi.setItemProperties('test-repo', 'test/artifact/path/for.jar', _) >> false >> true
    }

    def 'sets properties fails with retry'() {
        given:
        setPropertiesTask.retries = 1
        storageApi.setItemProperties('test-repo', 'test/artifact/path/for.jar', _) >> false

        when:
        setPropertiesTask.runRemoteCommand(artifactoryClient)

        then:
        thrown(GradleException)
    }

    def setup() {
        project = ProjectBuilder.builder().withName('root').build()
        project.plugins.apply ArtifactoryRestPlugin
        setPropertiesTask = project.tasks.create('setProperties', SetProperties) {
            properties = ['test-name': ['test-value']]
            artifact('test-repo', 'test/artifact/path/for.jar')
            requestInterval = 1L
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
        boolean setItemProperties(String repo, String path, Map properties)
    }
}
