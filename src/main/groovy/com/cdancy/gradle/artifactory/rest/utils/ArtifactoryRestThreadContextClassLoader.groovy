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
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection

import java.lang.reflect.Method

class ArtifactoryRestThreadContextClassLoader implements ThreadContextClassLoader {
    public static final String PAYLOADS_CLASS = "org.jclouds.io.Payloads"
    public static final String PROMOTE_CLASS = "com.cdancy.artifactory.rest.domain.docker.Promote"
    public static final String CLIENT_CLASS = "com.cdancy.artifactory.rest.ArtifactoryClient"

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
        ClassLoader classLoader = ArtifactoryRestUtil.createClassLoader(classpath.files)
        Class clientClass = ArtifactoryRestUtil.loadClass(classLoader, CLIENT_CLASS)
		String endPoint = artifactoryRestExtension.url ? artifactoryRestExtension.url.call() : null;
		String credentials = artifactoryRestExtension.credentials ? artifactoryRestExtension.credentials.call() : null;
		clientClass.getConstructor(String, String).newInstance(endPoint,credentials)
    }

    @Override
    def newPayload(Object file) {
        Class clazz = ArtifactoryRestUtil.loadClass(artifactoryClient.class.classLoader, PAYLOADS_CLASS)
        Method method = clazz.getMethod("newPayload", Object);
        method.invoke(null, file);
    }

    def newPromote(String promotedRepo, String image, String tag, boolean copy) {
        Class clazz = ArtifactoryRestUtil.loadClass(artifactoryClient.class.classLoader, PROMOTE_CLASS)
        Method method = clazz.getMethod("create", String, String, String, boolean);
        method.invoke(null, promotedRepo, image, tag, copy);
    }
}
