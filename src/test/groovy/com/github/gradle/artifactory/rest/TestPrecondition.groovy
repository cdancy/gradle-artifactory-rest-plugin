package com.github.gradle.artifactory.rest

final class TestPrecondition {
    public static final List<String> ALLOWED_PING_PROTOCOLS = ['http', 'https']
    public static final boolean ARTIFACTORY_SERVER_INFO_URL_REACHABLE = isArtifactoryServerInfoUrlReachable()
    public static final boolean ARTIFACTORY_CREDENTIALS_AVAILABLE = hasArtifactoryCredentials()

    private TestPrecondition() {}

    private static boolean isArtifactoryServerInfoUrlReachable() {
        isUrlReachable(new URL("${TestConfiguration.artifactoryServerUrl}"))
    }

    private static boolean isUrlReachable(URL url) {
        if(!ALLOWED_PING_PROTOCOLS.contains(url.protocol)) {
            throw new IllegalArgumentException("Unsupported URL protocol '$url.protocol'")
        }

        try {
            HttpURLConnection connection = url.openConnection()
            connection.requestMethod = 'HEAD'
            connection.connectTimeout = 3000
            return connection.responseCode == HttpURLConnection.HTTP_OK
        }
        catch(Exception e) {
            return false
        }
    }

    private static boolean hasArtifactoryCredentials() {
        File gradlePropsFile = new File(System.getProperty('user.home'), '.gradle/gradle.properties')

        if(!gradlePropsFile.exists()) {
            return false
        }

        Properties properties = new Properties()

        gradlePropsFile.withInputStream {
            properties.load(it)
        }

        properties['dockerHubUsername'] != null && properties['dockerHubPassword'] != null && properties['dockerHubEmail'] != null
    }
}
