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
import java.util.HashSet;
import java.util.Set;

/**
 * Caches the names of all files under the supplied directory (File) to 
 * provide fast access. All file names are stored as absolute paths.
 *
 * @author Steven Harris - SMI Software Development
 */
public class CachedDirectoryFile  {

    /**
     * Cache which stores the File objects. 
     */
    private HashSet contents;

    /**
     * The absolute path represented by this directory 
     */
    private String absolutePath;

    /**
     * Constructor requiring a File. The File must represent a directory.
     *
     * @param directory File (directory) whose contents are cached.
     * @param recurse true caches entire directory tree, false just top level.
     *
     * @exception IllegalArgumentException
     *          If the File argument does not represent a directory. 
     */
    public CachedDirectoryFile(File directory, boolean recurse) 
	                               throws IllegalArgumentException {

	if (!directory.isDirectory()) {
	    String mess = "File is not a directory.";
	    throw new IllegalArgumentException(mess);
	}

	absolutePath = directory.getAbsolutePath();
	contents = new HashSet();
	loadDirectoryContent(directory, contents, recurse);
    }
    
    /**
     * Load the name strings of all files in the supplied directory.
     * 
     * @param directory the directory whose file contents are loaded.
     * @param fileSet the Set into which file names are stored.
     * @param recurse true means get all files in the entire directory tree.
     */
    private void loadDirectoryContent(File directory, Set fileSet,
				      boolean recurse) {
	File[] files = directory.listFiles();
	for (int index = 0; index < files.length; ++index) {
	    if (files[index].isDirectory() && recurse) {
		loadDirectoryContent(files[index], fileSet, recurse);
	    } else {
		fileSet.add(files[index].getAbsolutePath());
	    }
	}
    }

    /**
     * Return true if the File represented by the filePath is in the cache.
     * 
     * @param filePath a String representing a file path
     * 
     * @return true if File represented by the String is in the cache.
     */
    public boolean hasFile(String filePath) {
	return contents.contains(filePath);
    }

    /**
     * Returns a array of all File objects contained in/under this directory
     * 
     * @return an array of File representing the contents of this directory
     */
    public File[] getFileContent() {
	Object[] objArray = contents.toArray();
	File[] fileArray = new File[objArray.length];
	System.arraycopy(objArray, 0, fileArray, 0, fileArray.length);
	return fileArray;
    }

    /**
     * Returns the absolute path of the directory.
     * 
     * @return a String representing the absolute path of this directory
     */
    public String getAbsolutePath() {
	return absolutePath;
    }

} // CachedDirectoryFile
