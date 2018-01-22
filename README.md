

[![Build Status](https://travis-ci.org/cdancy/gradle-artifactory-rest-plugin.svg?branch=master)](https://travis-ci.org/cdancy/gradle-artifactory-rest-plugin)
[![codecov](https://codecov.io/gh/cdancy/gradle-artifactory-rest-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/cdancy/gradle-artifactory-rest-plugin)
[![Download](https://api.bintray.com/packages/cdancy/gradle-plugins/gradle-artifactory-rest-plugin/images/download.svg) ](https://bintray.com/cdancy/gradle-plugins/gradle-artifactory-rest-plugin/_latestVersion)
[![Stack Overflow](https://img.shields.io/badge/stack%20overflow-gradle&#8211;artifactory&#8211;rest&#8211;plugin-4183C4.svg)](https://stackoverflow.com/questions/tagged/gradle+artifactory+rest+plugin)

# gradle-artifactory-rest-plugin

Gradle plugin, based on jclouds, for interacting with Artifactory's REST API.

## Setup

```
buildscript() {
    repositories {
        jcenter()
    }
    dependencies {
        classpath group: 'com.cdancy', name: 'gradle-artifactory-rest-plugin', version: 'X.Y.Z'
    }
}

apply plugin: 'gradle-artifactory-rest-plugin'
```
## Extension

The `artifactoryRest` extension is provided to define the `endpoint` and `credentials` for connecting to an Artifactory instance. Using the extension, and subsequently exposing this potentially private information, is required only if one does NOT want to use the various means of setting the aforementioned properties noted in the `Credentials` section below.

Because we are built on top of jclouds we can take advantage of overriding various internal _HTTP_ properties by
setting the `overrides` property or, and in following with the spirit of this plugin, configuring them
through `System Properties` of `Environment Variables`. Further directions on how to set them through `System Properties` and `Environment Variables` can be found [HERE](https://github.com/cdancy/bitbucket-rest#on-system-property-and-environment-variable-setup). 

The properties a given client can configure can be
found [HERE](https://github.com/jclouds/jclouds/blob/master/core/src/main/java/org/jclouds/Constants.java).

```
artifactoryRest {
    url { "https://localhost:8081/artifactory" } // Optional and can be sourced from sys-prop/env-var. Default to shown URL.
    credentials { "admin:password" } // Optional and can be sourced from sys-prop/env-var. Default to anonymous auth.

    // Optional and can be sourced from sys-prop/env-var.
    overrides = ["jclouds.so-timeout" : "300000", 
                 "jclouds.connection-timeout" : "300000",
                 "jclouds.retries-delay-start" : "60000",
                 "jclouds.max-retries" : "5" ]
}
```

## Credentials

Because this plugin builds on top of [artifactory-rest](https://github.com/cdancy/artifactory-rest) library one can supply
[credentials](https://github.com/cdancy/artifactory-rest#credentials) in any form this library accepts. Furthermore,
[artifactory-rest](https://github.com/cdancy/artifactory-rest#property-based-setup) allows the `endpoint` and `credentials`
to be optionally supplied through properties or environment variables. This gives great flexibility in the way the user
wants to define and/or hide their credentials assuming one does not want to use the `artifactoryRest` extension.

