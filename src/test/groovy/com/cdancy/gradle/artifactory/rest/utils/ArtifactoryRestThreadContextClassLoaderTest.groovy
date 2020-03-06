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
package com.cdancy.gradle.artifactory.rest.utils

import com.cdancy.gradle.artifactory.rest.ArtifactoryRestExtension
import com.cdancy.gradle.artifactory.rest.ArtifactoryRestPlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import java.nio.file.FileSystems

/**
 *
 * @author vagrant* @version $Id$ 3/6/20
 */
class ArtifactoryRestThreadContextClassLoaderTest extends Specification {
    Project project
    ArtifactoryRestExtension artifactoryRestExtension
    ArtifactoryRestThreadContextClassLoader artifactoryRestThreadContextClassLoader

    def 'creates client, payload, and promote in withClosure{}'() {
        given:
        def file = project.file('test.json')
        file.text = '{}'

        when:
        def outsideClient
        def outsidePayload
        def outsidePromote
        artifactoryRestThreadContextClassLoader.withClosure { closureClient ->
            outsideClient = closureClient
            outsidePayload = artifactoryRestThreadContextClassLoader.newPayload(file)
            outsidePromote = artifactoryRestThreadContextClassLoader.newPromote(
                'promote-repo',
                'image/my',
                '1.0.0',
                'latest',
                true
            )
        }

        then:
        outsideClient
        outsidePayload
        outsidePromote
    }

    File getClassFile(String className) {
        //first load the class to make sure that it even exists
        this.class.classLoader.loadClass(className)
        //get the URL of the resource
        def url = this.class.classLoader.getResource("${className.replace('.', '/')}.class")
        //Convert it to a URI
        def uri = URI.create("${url.file[0..url.file.indexOf('!') - 1]}")
        //Create & return the file object from the URI
        FileSystems.default.provider().getPath(uri).toFile()
    }

    def setup() {
        project = ProjectBuilder.builder().withName('root').build()
        artifactoryRestExtension = project.extensions
            .create(ArtifactoryRestPlugin.EXTENSION_NAME, ArtifactoryRestExtension)
        artifactoryRestExtension.credentials.set('user:password')

        artifactoryRestThreadContextClassLoader = new ArtifactoryRestThreadContextClassLoader(
            artifactoryRestExtension,
            project.files(
                getClassFile(ArtifactoryRestThreadContextClassLoader.CLIENT_CLASS),
                getClassFile(ArtifactoryRestThreadContextClassLoader.PAYLOADS_CLASS),
                getClassFile(ArtifactoryRestThreadContextClassLoader.PROMOTE_CLASS)
            )
        )

    }
}
