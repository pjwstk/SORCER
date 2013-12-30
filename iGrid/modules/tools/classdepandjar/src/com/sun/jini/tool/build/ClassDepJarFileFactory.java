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
package com.sun.jini.tool.build;

// java.io
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

// java.util
import java.util.Set;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

// com.sun.jini
import com.sun.jini.tool.ClassDep;
import com.sun.jini.tool.build.jar.JarElement;
import com.sun.jini.tool.build.jar.JarElementCollator;

import com.sun.jini.tool.util.CachedDirectoryFile;
import com.sun.jini.tool.util.Classpath;

import org.apache.tools.ant.Task;

/**
 * Populates a JarFile from the output of a ClassDep computation.
 * This factory uses the classpath given to ClassDep to load the 
 * required classes.
 *
 * @author Steven Harris - SMI Software Development
 */
public class ClassDepJarFileFactory {
    
    /**
     * The arguments to supply to ClassDep. 
     */
    private String[] classDepArgs;

    /**
     * The collator with which the jar file is written 
     */
    private JarElementCollator collator;

    /**
     * Flag indicates whether or not JarElementCollator was passed or created 
     */
    private boolean collatorCreated;
    
    /** the calling task */
    private Task callingTask;

    /**
     * Constructor requiring a jar file name and an array of arguments for
     * ClassDep and optional manifest file.
     * 
     * <P>Notes:</P>
     * See javadoc for com.sun.jini.tool.ClassDep.java for information about
     * accepted arguments.
     * a
     * @param jfName a String specifying the absolute path of output jar file
     * @param man a Manifest to include in jar file (null == no manifest)
     * @param args an array of String containing arguments for ClassDep
     */
    public ClassDepJarFileFactory(String jfName,
                                  Manifest man,
                                  String[] args,
                                  Task callingTask) throws IOException {
        this.callingTask = callingTask;
        Manifest manifest = man == null ? new Manifest() : man;
        collator = new JarElementCollator(jfName, manifest);
        collatorCreated = true;
        classDepArgs = args;
    }

    /**
     * Constructor requiring a jar file name and an optional manifest file
     * and a String representing the arguments for ClassDep.
     * 
     * <P>Notes:<BR>
     * See javadoc for com.sun.jini.tool.ClassDep.java for information about
     * accepted arguments.</P>
     * 
     * @param jfName a String specifying the absolute path of output jar file
     * @param man a Manifest to include in jar file (null == no manifest)
     * @param args a String representing arguments for ClassDep
     */
    public ClassDepJarFileFactory(String jfName,
                                  Manifest man,
                                  String  args,
                                  Task callingTask) throws IOException {
        this.callingTask = callingTask;
        Manifest manifest = man == null ? new Manifest() : man;
        collator = new JarElementCollator(jfName, manifest);
        collatorCreated = true;
        // ClassDep requires an array of String
        StringTokenizer st = new StringTokenizer(args);
        classDepArgs = new String[st.countTokens()];
        for (int i = 0; i < classDepArgs.length; ++i) {
            classDepArgs[i] = st.nextToken();
        }
    }

    /**
     * Constructor requiring a JarElementCollator and an array of 
     * arguments for ClassDep.
     * 
     * @param jarCollator a JarElementCollator with which jar file is created
     * @param args an array of String containing arguments for ClassDep
     */
    public ClassDepJarFileFactory(JarElementCollator jarCollator, 
				  String[] args, Task callingTask) {
        this.callingTask = callingTask;
	collator = jarCollator;
	collatorCreated = false;
	classDepArgs = args;
    }

    /**
     * Constructor requiring a JarElementCollator and an String of 
     * arguments for ClassDep.
     * 
     * @param jarCollator a JarElementCollator with which jar file is created
     * @param args a String representing arguments for ClassDep
     */
    public ClassDepJarFileFactory(JarElementCollator jarCollator, 
				  String args, Task callingTask) {

        this.callingTask = callingTask;
	collator = jarCollator;
	collatorCreated = false;

	// ClassDep requires an array of String
	StringTokenizer st = new StringTokenizer(args);
	classDepArgs = new String[st.countTokens()];
	for (int i = 0; i < classDepArgs.length; ++i) {
	    classDepArgs[i] = st.nextToken();
	}
    }

    /**
     * Creates the jar file from the ClassDep analysis.
     *
     * @exception IOException
     *          if class file is missing or could not be read
     */
    public Set createJarFile() throws IOException {
        // perform ClassDep computation to retrieve class dependencies
        String[] requiredClasses = getRequiredClasses();
        
        // load classes found and place into jar file
        Set set = loadClassesAndJar(requiredClasses);
        
        // if the JarElementCollator was internally created, then close
        if (collatorCreated) {
            collator.closeJar();
        }
        return set;
    }

    /**
     * Creates the jar file from the ClassDep analysis.
     *
     * @param requiredClasses An array of classes to include.
     * @exception IOException if class file is missing or could not be read
     */
    public Set createJarFile(String[] requiredClasses) throws IOException {

        // load classes found and place into jar file
        
        Set set = loadClassesAndJar(requiredClasses);
        
        // if the JarElementCollator was internally created, then close
        if (collatorCreated) {
            collator.closeJar();
        }
        return set;
    }

    /**
     * Search ClassDep arguments and find the classpath.
     * 
     * <P>Notes:</P>
     * It must be there. This routine does no checking.
     * 
     * @return the classpath passed to ClassDep as an argument
     */
    private String getClasspath() {
	for (int i = 0; i < classDepArgs.length; ++i) {
	    if (classDepArgs[i].equals("-cp")) {
		return classDepArgs[i+1];
	    }
	}

	return null; // this will cause a NullPointerException
    }

    /**
     * Return class dependencies found via ClassDep computation.
     * 
     * <P>Notes:</P>
     * 
     * @return an array of String containing names of classes found via 
     *         ClassDep analysis.
     * 
     * @see    ClassDep#compute() 
     */
    public String[] getRequiredClasses() {
	ClassDep dep = new ClassDep(callingTask);
	dep.setupOptions(classDepArgs);
	return dep.compute();
    }

    /**
     * Load classes found by ClassDep and place them into the jar file.
     * 
     * <P>Notes:</P>
     * 
     * @param classNames the names of classes to be placed in jar
     * 
     * @exception IOException
     *          if class file is missing or could not be read
     */
    private Set loadClassesAndJar(String[] classNames) throws IOException {
        
        // output each jar entry
        HashSet set = new HashSet();
        ClasspathFileFinder finder = new ClasspathFileFinder(getClasspath());
        for (int index = 0; index < classNames.length; ++index) {
            if (index > 1 && classNames[index].equals(classNames[index-1])) {
                continue;
            }
            String filename =
            classNames[index].replace('.', File.separatorChar) + ".class";
            JarElement je =finder.findFileInClasspath(filename);
            if (je != null) {
                set.add(je.getJarEntry().getName());
                collator.addJarElement(je);
            }
        }
        return set;
    }

    /**
     * Finds a File/JarEntry within a class path.
     */
    public class ClasspathFileFinder {

	/**
	 * Holds caching objects
	 */
	private Object[] cacheArray;

	/**
	 * Makes accessing classpath elements easier
	 */
	Classpath cPath;

	/**
	 * Constructor requiring a classpath.
	 * 
	 * <P>Notes:</P>
	 * 
	 * @param classpath the classpath in which to find files.
	 *
	 * @exception IOException
	 *          if a File can not be read
	 */
	public ClasspathFileFinder(String classpath) throws IOException {
	    cPath = new Classpath(classpath);
	    createCache();
	}

	/**
	 * Creates a caching object for each element in the classpath.
	 * 
	 * @exception IOException
	 *          if a File object can not be created
	 */
    private void createCache() throws IOException {
        
        // capture each element of the classpath as a File object
        File[] pathElement = cPath.getElementsAsFile();
        cacheArray = new Object[pathElement.length];
        
        // create an appropriate caching object
        for (int index = 0; index < pathElement.length; ++index) {
            Object element = null;
            if (pathElement[index].isDirectory()) {
                element =
                new CachedDirectoryFile(pathElement[index], true);
            } else { // it must be a jar file
                element = new JarFile(pathElement[index]);
            }
            
            // cacheda object will be used later
            cacheArray[index] = element;
        }
    }

	/**
	 * Search the classpath for the specified file and return a JarElement
	 * that represents the file.
	 * 
	 * @param filename the path of the class file to be returned
	 * 
	 * @return a JarElement representing the file found in the
	 *         classpath.
	 *
	 * @exception FileNotFoundException
	 *          if a File does not exist
	 * @exception IOException
	 *          if directory can not be read
	 */
	public JarElement findFileInClasspath(String filename) 
	                                     throws FileNotFoundException,
	                                            IOException {

	    // each element of the path must be searched in order
        for (int index = 0; index < cacheArray.length; ++index) {
            if (cacheArray[index] instanceof CachedDirectoryFile) {
                CachedDirectoryFile cdf =
                (CachedDirectoryFile) cacheArray[index];
                String fullPath =
                cdf.getAbsolutePath() + File.separator + filename;
                if (cdf.hasFile(fullPath)) {
                    filename = filename.replace('\\','/'); // for WIndows files
                    return new JarElement(filename, fullPath);
                }
            } else {
                JarFile jFile = (JarFile) cacheArray[index];
                String fName = filename.replace('\\', '/'); // Windows files
                JarEntry jEnt = jFile.getJarEntry(fName);
                if (jEnt != null) {
                    return new JarElement(jEnt, jFile.getInputStream(jEnt));
                }
            }
        }

	    /* Hmmm ... this should not happen since the classes and the
	       classpath both come from ClassDep. */
	    return null;
	}

    } // ClasspathFileFinder

} // ClassDepJarFileFactory
