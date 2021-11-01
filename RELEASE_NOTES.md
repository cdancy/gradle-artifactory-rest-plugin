### Version 1.2.9 (11/1/21)
* Migrate to maven central and update project

### Version 1.1.1 (9/24/20)
* FIX: serialization error in ArtifactToCopy

### Version 1.1.0 (9/23/20)
* REFACTOR: General improvements - Build with gradle-java-plugin, cleanup task input warnings, & helpers for Aql.

### Version 1.0.0 (3/10/20)
* REFACTOR: bring the plugin into the 21st century of gradle operations.

### Version 0.0.18 (3/5/20)
* ADDED: Expose `targetTag` on `Promote` task.

### Version 0.0.17 (9/4/19)
* ADDED: SetProperties task can be setup to retry using `retries` task property

### Version 0.0.16 (8/05/19)
* FIX: Change setProperties logging to debug level

### Version 0.0.15 (5/13/19)
* ADDED: LatestVersionFromLayout task gained optional property 'remote'.

### Version 0.0.14 (8/20/18)
* BUGFIX: Bump `artifactory-rest` client to `0.9.4`

### Version 0.0.13 (8/20/18)
* ADDED: Bump `artifactory-rest` client to `0.9.3`.

### Version 0.0.12 (8/17/18)
* ADDED: bumped artifactory-rest to `0.9.2`
* FIX: bug in `PromoteImage` task where incorrect class was being referenced.

### Version 0.0.11 (2/6/18)
* FIX: bug fix for potential ExceptionInInitializerError

### Version 0.0.10 (2/1/18)
* ADDED: basic retry logic added for 'latestVersionFromLayout' endpoint as it appears to be special in some way and fails with cryptographic exceptions randomly.

### Version 0.0.9 (1/22/18)
* ADDED: Bump `artifactory-rest` library to `0.9.0` - [PR 1](https://github.com/cdancy/gradle-artifactory-rest-plugin/pull/1)
* ADDED: Exposed jclouds `overrides` options through extension - [PR 2](https://github.com/cdancy/gradle-artifactory-rest-plugin/pull/2)

### Version 0.0.8 (12/20/17)
* REFACTOR: Bump version of artifactory-rest to 0.0.10
* FIX: malformed base package structure when referencing the 'all' jar

### Version 0.0.7 (12/15/17)
* REFACTOR: bump version of artifactory-rest to 0.0.9 and use `all` classifier

### Version 0.0.6 (11/10/16)
* ADDED: task `FileList`

### Version 0.0.5 (8/23/16)
* REFACTOR: bump version of gradle to 2.14.1
* REFACTOR: bump version of artifactory-rest to 0.0.5

### Version 0.0.4 (6/29/16)
* ADDED: `DeleteArtifact` gained optional property `failFast`.
* ADDED: task `LatestVersionFromLayout`.
* REFACTOR: bump version of gradle to 2.13
* REFACTOR: bump version of artifactory-rest to 0.0.4

### Version 0.0.3 (April 10, 2016)
* ADDED: bumped to version 0.0.3 of artifactory-rest. 
* ADDED: bumped to version 2.12 for gradlew wrapper.

### Version 0.0.2 (April 6, 2016)
* ADDED: Task CopyArtifact.

### Version 0.0.1 (March 20, 2016)
* init for project.
