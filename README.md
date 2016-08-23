
# gradle-artifactory-rest-plugin

Gradle plugin for interacting with Artifactory's REST API.

## Setup

```
buildscript() {
 	repositories {
 		jcenter()
 	}
 	dependencies {
 		classpath group: 'com.cdancy', name: 'gradle-artifactory-rest-plugin', version: '0.0.5'
 	}
 }

 apply plugin: 'gradle-artifactory-rest-plugin'
 ```
## Extension

The `artifactoryRest` extension is provided to define the `endpoint` and `credentials` for connecting to an Artifactory instance.
Using the extension, and subsequently exposing this potentially private information, is required only if one does NOT want to use
the various means of setting the aforementioned properties noted in the `Credentials` section below.

```
 artifactoryRest {
 	url { "https://localhost:8081/artifactory" }
 	credentials { "admin:password" }
 }
```

## Credentials

Because this plugin builds on top of [artifactory-rest](https://github.com/cdancy/artifactory-rest) library one can supply
[credentials](https://github.com/cdancy/artifactory-rest#credentials) in any form this library accepts. Furthermore,
[artifactory-rest](https://github.com/cdancy/artifactory-rest#property-based-setup) allows the `endpoint` and `credentials`
to be optionally supplied through properties or environment variables. This gives great flexibility in the way the user
wants to define and/or hide their credentials assuming one does not want to use the `artifactoryRest` extension.

## Examples

TODO...
    
## Testing

TODO...
