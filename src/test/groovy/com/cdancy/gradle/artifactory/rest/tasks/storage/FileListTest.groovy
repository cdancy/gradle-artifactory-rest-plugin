package com.cdancy.gradle.artifactory.rest.tasks.storage


import com.cdancy.gradle.artifactory.rest.ArtifactoryRestPlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class FileListTest extends Specification {
    Project project
    FileList fileListTask
    ArtifactoryClient artifactoryClient
    ArtifactoryApi artifactoryApi
    ArtifactoryStorageApi storageApi

    def 'get file list'() {
        given:
        def retList = com.cdancy.artifactory.rest.domain.storage.FileList.create('', '', [])

        when:
        fileListTask.runRemoteCommand(artifactoryClient)

        then:
        1 * storageApi.fileList('test-repo', 'test/artifact/path', 0, null, 0, 0) >> retList
    }

    def setup() {
        project = ProjectBuilder.builder().withName('root').build()
        project.plugins.apply ArtifactoryRestPlugin
        fileListTask = project.tasks.create('getFileList', FileList) {
            repo = 'test-repo'
            artifactPath = 'test/artifact/path'
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
        def fileList(
            String repoKey,
            String itemPath,
            Integer deep,
            Integer depth,
            Integer listFolders,
            Integer includeRootPath)
    }
}
