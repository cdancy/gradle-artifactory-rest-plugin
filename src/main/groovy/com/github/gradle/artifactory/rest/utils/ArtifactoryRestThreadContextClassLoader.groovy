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
package com.github.gradle.artifactory.rest.utils

import com.github.gradle.artifactory.rest.ArtifactoryRestExtension

import java.lang.reflect.Constructor
import java.lang.reflect.Method

class ArtifactoryRestThreadContextClassLoader implements ThreadContextClassLoader {
    public static final String CLIENT_CLASS = "com.github.artifactory.rest.ArtifactoryClient"

    public static final String SET_ITEM_PROPERTIES_CLASS = "com.github.artifactory.rest.options.SetItemProperties"
    public static final String FILE_UTILS_CLASS = "org.apache.commons.io.FileUtils"
    public static final String PAYLOADS_CLASS = "org.jclouds.io.Payloads"

    private final ArtifactoryRestExtension artifactoryRestExtension
    private final Set<File> classpath
    private def artifactoryClient

    ArtifactoryRestThreadContextClassLoader(ArtifactoryRestExtension artifactoryRestExtension, Set<File> classpath) {
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
        ClassLoader classLoader = ArtifactoryRestUtil.createClassLoader(classpath)
        Class clientClass = ArtifactoryRestUtil.loadClass(classLoader, CLIENT_CLASS)
		String endPoint = artifactoryRestExtension.url ? artifactoryRestExtension.url.call() : null;
		String credentials = artifactoryRestExtension.credentials ? artifactoryRestExtension.credentials.call() : null;
		clientClass.getConstructor(String, String).newInstance(endPoint,credentials)
    }

    @Override
    def createSetItemProperties() {
        Class enclosingClass = ArtifactoryRestUtil.
                loadClass(artifactoryClient.class.classLoader,
                        SET_ITEM_PROPERTIES_CLASS)
        enclosingClass.getConstructor().newInstance()
    }

    @Override
    def copyInputStreamToFile(InputStream inputStream, File file) {
        Class clazz = ArtifactoryRestUtil.loadClass(artifactoryClient.class.classLoader, FILE_UTILS_CLASS)
        Method method = clazz.getMethod("copyInputStreamToFile", InputStream, File);
        method.invoke(null, inputStream, file);
    }

    @Override
    def newPayload(Object file) {
        Class clazz = ArtifactoryRestUtil.loadClass(artifactoryClient.class.classLoader, PAYLOADS_CLASS)
        Method method = clazz.getMethod("newPayload", Object);
        method.invoke(null, file);
    }
}
