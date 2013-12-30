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
package com.sun.jini.tool.build.jar;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class JarElement
{

    public JarElement()
    {
        jarEntry = null;
        in = null;
        jos = null;
    }

    public JarElement(String s)
        throws FileNotFoundException
    {
        this(s, s);
    }

    public JarElement(String s, String s1)
        throws NullPointerException, IllegalArgumentException, FileNotFoundException
    {
        jarEntry = null;
        in = null;
        jos = null;
        setJarEntry(s);
        setInputStream(s1);
    }

    public JarElement(JarEntry jarentry, InputStream inputstream)
    {
        jarEntry = null;
        in = null;
        jos = null;
        setJarEntry(jarentry);
        setInputStream(inputstream);
    }

    public InputStream getInputStream()
    {
        return in;
    }

    public JarEntry getJarEntry()
    {
        return jarEntry;
    }

    public void setInputStream(InputStream inputstream)
    {
        in = inputstream;
    }

    public void setInputStream(String s)
        throws FileNotFoundException
    {
        setInputStream(((InputStream) (new FileInputStream(s))));
    }

    public void setJarEntry(String s)
        throws NullPointerException, IllegalArgumentException
    {
        setJarEntry(new JarEntry(s));
    }

    public void setJarEntry(JarEntry jarentry)
    {
        this.jarEntry = jarentry;
    }

    private JarEntry jarEntry;
    private InputStream in;
    private JarOutputStream jos;
}
