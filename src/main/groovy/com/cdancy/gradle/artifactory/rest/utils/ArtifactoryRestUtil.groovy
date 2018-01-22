package com.cdancy.gradle.artifactory.rest.utils

import java.lang.reflect.Array

final class ArtifactoryRestUtil {
	
    private ArtifactoryRestUtil() {}

    static Object[] toArray(List list) {
        if(list == null) { return null }
        Class clazz = list[0].getClass()
        list.toArray(Array.newInstance(clazz, 0))
    }

    static Class loadClass(ClassLoader classLoader, String className) {
        classLoader.loadClass(className)
    }

    /**
     * Creates the classloader with the given classpath files.
     *
     * @param classpathFiles Classpath files
     * @return URL classloader
     */
    static URLClassLoader createClassLoader(Set<File> classpathFiles) {
        new URLClassLoader(toURLArray(classpathFiles), ClassLoader.systemClassLoader.parent)
    }

    /**
     * Creates URL array from a set of files.
     *
     * @param files Files
     * @return URL array
     */
    static URL[] toURLArray(Set<File> files) {
        files.collect { file -> file.toURI().toURL() } as URL[]
    }

    static Map<String, String> gstringMapToStringMap(Map<String, String> gStringMap) {
        final Map<String, String> convertedMap = new HashMap<>();
        if (gStringMap) {
            gStringMap.each { k,v ->
                convertedMap.put(k.toString(), v.toString())
            }
        }
        convertedMap
    }
}
