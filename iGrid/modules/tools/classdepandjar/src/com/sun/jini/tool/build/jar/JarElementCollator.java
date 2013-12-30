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
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.*;

// Referenced classes of package com.sun.jini.tool.build.jar:
//            JarElement

public class JarElementCollator
{

    public JarElementCollator()
    {
        jarFilename = null;
        jarOutputStream = null;
        manifest = null;
    }

    public JarElementCollator(String s)
        throws IOException
    {
        this(s, new Manifest());
        jarFilename = s;
    }

    public JarElementCollator(String s, Manifest manifest1)
        throws IOException
    {
        jarFilename = null;
        jarOutputStream = null;
        manifest = null;
        if(manifest1 == null)
            setJarOutputStream(new JarOutputStream(new FileOutputStream(s)));
        else
            setJarOutputStream(new JarOutputStream(new FileOutputStream(s), manifest1));
    }

    public JarElementCollator(JarOutputStream jaroutputstream)
    {
        jarFilename = null;
        jarOutputStream = null;
        manifest = null;
        setJarOutputStream(jaroutputstream);
    }

    public void addJarElement(JarElement jarelement)
        throws IOException
    {
        jarOutputStream.putNextEntry(jarelement.getJarEntry());
        byte abyte0[] = new byte[1024];
        int i;
        while((i = jarelement.getInputStream().read(abyte0)) >= 0) 
            jarOutputStream.write(abyte0, 0, i);
    }

    public void batchAddElements(JarElement ajarelement[])
    {
        for(int i = 0; i < ajarelement.length; i++)
            try
            {
                addJarElement(ajarelement[i]);
            }
            catch(IOException ioexception)
            {
                System.out.println("Could not add:" + ajarelement[i].getJarEntry().getName());
                ioexception.printStackTrace();
            }

    }

    public void closeJar()
        throws ZipException, IOException
    {
        jarOutputStream.closeEntry();
        jarOutputStream.close();
    }

    public JarOutputStream getOutputStream()
    {
        return jarOutputStream;
    }

    public void setJarOutputStream(JarOutputStream jaroutputstream)
    {
        jarOutputStream = jaroutputstream;
    }

    private String jarFilename;
    private JarOutputStream jarOutputStream;
    private Manifest manifest;
}
