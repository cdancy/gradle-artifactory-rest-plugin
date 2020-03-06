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
package com.cdancy.gradle.artifactory.rest

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 *
 * @author vagrant* @version $Id$ 3/6/20
 */
class ArtifactoryRestExtensionTest extends Specification {
    Project project
    ArtifactoryRestExtension artifactoryRestExtension

    def 'extension defaults are correct'() {
        expect:
        !artifactoryRestExtension.credentials.orNull
        !artifactoryRestExtension.url.orNull
        artifactoryRestExtension.overrides.get() == [:]
    }

    def setup() {
        project = ProjectBuilder.builder().withName('root').build()
        artifactoryRestExtension = project.extensions
            .create(ArtifactoryRestPlugin.EXTENSION_NAME, ArtifactoryRestExtension)

    }
}
