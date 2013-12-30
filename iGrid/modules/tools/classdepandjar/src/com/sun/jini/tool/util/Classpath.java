/*
 * Copyright 2005 Sun Microsystems, Inc.
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
package com.sun.jini.tool.util;

// java.io
import java.io.File;

// java.util
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.jar.JarFile;

/**
 * Provides a convenient encapsulation of a Java classpath.
 * Provides access to path elements and checks for validity of each element.
 *
 * @author Steven Harris - SMI Software Development
 */
public class Classpath  {
    
    /**
     * The string that specifies the class path 
     */
    private String classpath;

    /**
     * Constructor requiring a string that is a presumably legal classpath.
     */
    public Classpath(String classpathString) {
	classpath = classpathString;
    }
    
    /**
     * Returns the elements of the classpath as an array of String.
     * 
     * @return an array of String in which each element is a classpath element
     */
    public String[] getElementsAsString() {
	StringTokenizer tok = 
	    new StringTokenizer(classpath, File.pathSeparator);
	String[] elements = new String[tok.countTokens()];
	for (int index = 0; index < elements.length; ++index) {
	    elements[index] = tok.nextToken();
	}

	return elements;
    }

    /**
     * Returns the elements of the classpath as an array of File.
     * 
     * @return an array of File in which each element is a classpath element
     */
    public File[] getElementsAsFile() {
	String[] strElement = getElementsAsString();
	File[] fileElements = new File[strElement.length];
	for (int index = 0; index < strElement.length; ++index) {
	    fileElements[index] = new File(strElement[index]);
	}

	return fileElements;
    }

    /**
     * Return those elements that represent directories in an array of File.
     * 
     * @return an array of File each element of which represents a directory
     *         in the classpath.
     */
    public File[] getDirectoryElements() {

	// identify all elements that are directories
	ArrayList dirList = new ArrayList();
	File[] files = getElementsAsFile();
	for (int index = 0; index < files.length; ++index) {
	    if (files[index].isDirectory()) {
		dirList.add(files[index]);
	    }
	}

	// convert list into an array
	Object[] objArray = dirList.toArray();
	File[] dirArray = new File[objArray.length];
	System.arraycopy(objArray, 0, dirArray, 0, objArray.length);
	
	return dirArray;
    }

    /**
     * Return those elements that represent jar/zip files.
     * 
     * @return an array of JarFile each element of which represents a jar/zip
     *         file in the classpath.
     * 
     * @exception java.io.IOException
     *          if directory can not be accessed
     */
    public JarFile[] getJarElements() throws java.io.IOException {

	// identify all elements that are not directories
	ArrayList jarList = new ArrayList();
	File[] files = getElementsAsFile();
	for (int index = 0; index < files.length; ++index) {
	    if (files[index].isDirectory() == false) {
		jarList.add(new JarFile(files[index]));
	    }
	}

	// convert list into an array
	Object[] objArray = jarList.toArray();
	JarFile[] jarArray = new JarFile[objArray.length];
	System.arraycopy(objArray, 0, jarArray, 0, objArray.length);
	
	return jarArray;
    }

    /**
     * A convenience method that checks the validity of the classpath by
     * checking each element in classpath for existence.
     * 
     * @return true if all elements can be found, false otherwise.
     */
    public boolean exists() {
	boolean isValid = true;

	File[] files = getElementsAsFile();
	for (int index = 0; index < 0; ++index) {
	    isValid = files[index].exists() && isValid;
	}

	return isValid;
    }

} // Classpath
