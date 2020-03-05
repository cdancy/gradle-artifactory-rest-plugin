/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cdancy.gradle.artifactory.rest.utils

import com.cdancy.gradle.artifactory.rest.ArtifactoryRestExtension
import java.lang.reflect.Constructor
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection

import java.lang.reflect.Method

class ArtifactoryRestThreadContextClassLoader implements ThreadContextClassLoader {
    public static final String PAYLOADS_CLASS = "com.cdancy.artifactory.rest.shaded.org.jclouds.io.Payloads"
    public static final String PROMOTE_CLASS = "com.cdancy.artifactory.rest.domain.docker.PromoteImage"
    public static final String CLIENT_CLASS = "com.cdancy.artifactory.rest.ArtifactoryClient\$Builder"

    private final ArtifactoryRestExtension artifactoryRestExtension
    private final FileCollection classpath
    private def artifactoryClient

    ArtifactoryRestThreadContextClassLoader(ArtifactoryRestExtension artifactoryRestExtension, FileCollection classpath) {
        this.artifactoryRestExtension = artifactoryRestExtension
        this.classpath = classpath
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void withClosure(Closure closure) {
        if (!artifactoryClient) {
            artifactoryClient = generateClient()
        }
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure(artifactoryClient)
    }

    private def generateClient() {

        // These can be null but if so then System Properties and
        // Environment Variables will be searched instead.
        final String foundEndPoint = artifactoryRestExtension.url
            ? artifactoryRestExtension.url.call()
            : null
        final String foundCredentials = artifactoryRestExtension.credentials
            ? artifactoryRestExtension.credentials.call()
            : null
        final Map<String, String> possibleOverrides = ArtifactoryRestUtil.gstringMapToStringMap(artifactoryRestExtension.overrides)
        final Properties foundOverrides = new Properties();
        foundOverrides.putAll(possibleOverrides);

        final ClassLoader classLoader = ArtifactoryRestUtil.createClassLoader(classpath.files)
        final Class clientClass = ArtifactoryRestUtil.loadClass(classLoader, CLIENT_CLASS)
        final Constructor<?> ctor = clientClass.getDeclaredConstructors()[0]
        ctor.setAccessible(true)

        return ctor.newInstance()
            .endPoint(foundEndPoint)
            .credentials(foundCredentials)
            .overrides(foundOverrides)
            .build();
    }

    @Override
    def newPayload(Object file) {
        Class clazz = ArtifactoryRestUtil.loadClass(artifactoryClient.class.classLoader, PAYLOADS_CLASS)
        Method method = clazz.getMethod("newPayload", Object);
        method.invoke(null, file);
    }

    @Override
    def newPromote(String promotedRepo, String image, String tag, String targetTag, boolean copy) {
        Class clazz = ArtifactoryRestUtil.loadClass(artifactoryClient.class.classLoader, PROMOTE_CLASS)
        Method method = clazz.getMethod("create", String, String, String, String, boolean);
        method.invoke(null, promotedRepo, image, tag, targetTag, copy);
    }
}
