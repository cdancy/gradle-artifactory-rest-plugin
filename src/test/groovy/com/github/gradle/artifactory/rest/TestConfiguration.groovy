package com.github.gradle.artifactory.rest

final class TestConfiguration {
    private static final String ARTIFACTORY_SERVER_URL_SYS_PROP = 'artifactoryServerUrl'

    private TestConfiguration() {}

    static String getArtifactoryServerUrl() {
        System.properties[ARTIFACTORY_SERVER_URL_SYS_PROP] ?: 'http://127.0.0.1:8080/artifactory'
    }
}
