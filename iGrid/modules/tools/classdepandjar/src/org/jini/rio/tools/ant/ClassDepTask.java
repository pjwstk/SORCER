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
package org.jini.rio.tools.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Reference;

import com.sun.jini.tool.ClassDep;


/**
 */
public class ClassDepTask extends Jar {
    private ClassDep classdep ;
    private Path compileClasspath = null;

    /** Holds value of property files. */
    private boolean files;
    /** Holds value of property edges. */
    private boolean edges;
    /** Holds value of property outer. */
    private boolean outer;
    /** Holds value of property hide. */
    private ArrayList hide = new ArrayList();
    /** Holds value of property in. */
    private ArrayList in = new ArrayList();
    /** Holds value of property out. */
    private ArrayList out = new ArrayList();
    /** Holds value of property prune. */
    private ArrayList prune = new ArrayList();
    /** Holds value of property show. */
    private ArrayList show = new ArrayList();
    /** Holds value of property skip. */
    private ArrayList skip = new ArrayList();
    /** Holds value of property tell. */
    private ArrayList tell = new ArrayList();
    /** Holds value of property element. */
    private ArrayList elements = new ArrayList();
    /** Holds value of property debug. */
    private boolean debug;

    /** Creates new ClassDepTask */
    public ClassDepTask() {
        super();
        classdep = new ClassDep(this);
    }

    /** 
     * Getter for property files.
     * @return Value of property files.
     */
    public boolean isFiles() {
        return(files);
    }

    /** 
     * Setter for property files.
     * @param files New value of property files.
     */
    public void setFiles(boolean files) {
        this.files = files;
    }

    /** 
     * Getter for property edges.
     * @return Value of property edges.
     */
    public boolean isEdges() {
        return(edges);
    }

    /** 
     * Setter for property edges.
     * @param edges New value of property edges.
     */
    public void setEdges(boolean edges) {
        this.edges = edges;
    }

    /** 
     * Getter for property outer.
     * @return Value of property outer.
     */
    public boolean isOuter() {
        return(outer);
    }

    /** 
     * Setter for property outer.
     * @param outer New value of property outer.
     */
    public void setOuter(boolean outer) {
        this.outer = outer;
    }

    /** 
     * Adder for property hide.
     */
    public Argument createHide() {
        Argument argument = new Argument();
        hide.add(argument);
        return(argument);
    }

    public void setHide(String arg) {
        StringTokenizer st = new StringTokenizer(arg,", \t\n\r\f");
        while(st.hasMoreTokens()) {
            hide.add(new Argument(st.nextToken()));
        }
    }

    /** 
     * Adder for property in.
     */
    public Argument createIn() {
        Argument argument = new Argument();
        in.add(argument);
        return(argument);
    }

    public void setIn(String arg) {
        StringTokenizer st = new StringTokenizer(arg,", \t\n\r\f");
        while(st.hasMoreTokens()) {
            in.add(new Argument(st.nextToken()));
        }
    }

    /** 
     * Adder for property out.
     */
    public Argument createOut() {
        Argument argument = new Argument();
        out.add(argument);
        return(argument);
    }

    public void setOut(String arg) {
        StringTokenizer st = new StringTokenizer(arg,", \t\n\r\f");
        while(st.hasMoreTokens()) {
            out.add(new Argument(st.nextToken()));
        }
    }

    /** 
     * Adder for property prune.
     */
    public Argument createPrune() {
        Argument argument = new Argument();
        prune.add(argument);
        return(argument);
    }

    public void setPrune(String arg) {
        StringTokenizer st = new StringTokenizer(arg,", \t\n\r\f");
        while(st.hasMoreTokens()) {
            prune.add(new Argument(st.nextToken()));
        }
    }

    /** 
     * Adder for property show.
     */
    public Argument createShow() {
        Argument argument = new Argument();
        show.add(argument);
        return(argument);
    }

    public void setShow(String arg) {
        StringTokenizer st = new StringTokenizer(arg,", \t\n\r\f");
        while(st.hasMoreTokens()) {
            show.add(new Argument(st.nextToken()));
        }
    }

    /** 
     * Adder for property skip.
     */
    public Argument createSkip() {
        Argument argument = new Argument();
        skip.add(argument);
        return(argument);
    }

    public void setSkip(String arg) {
        StringTokenizer st = new StringTokenizer(arg,", \t\n\r\f");
        while(st.hasMoreTokens()) {
            skip.add(new Argument(st.nextToken()));
        }
    }

    /** 
     * Adder for property tell.
     */
    public Argument createTell() {
        Argument argument = new Argument();
        tell.add(argument);
        return(argument);
    }

    public void setTell(String arg) {
        StringTokenizer st = new StringTokenizer(arg,", \t\n\r\f");
        while(st.hasMoreTokens()) {
            tell.add(new Argument(st.nextToken()));
        }
    }

    /** 
     * Adder for property element.
     */
    public Argument createElement() {
        Argument argument = new Argument();
        elements.add(argument);
        return(argument);
    }
    public void setElement(String arg) {
        StringTokenizer st = new StringTokenizer(arg,", \t\n\r\f");
        while(st.hasMoreTokens()) {
            elements.add(new Argument(st.nextToken()));
        }
    }

    /// The topclass element is the same as element above but is added to stay
    /// in tune with ClassDepAndJarTask.
    /**
     * Creator for topclass element.
     */
    public Argument createTopclass() {
        Argument argument = new Argument();
        elements.add(argument);
        return(argument);
    }

    /**
     * Set the topclass element.
     * @param arg Name of a class
     */
    public void setTopclass(String arg) {
        StringTokenizer st = new StringTokenizer(arg, ", \t\n\r\f");
        while(st.hasMoreTokens()) {
            elements.add(new Argument(st.nextToken()));
        }
    }

    /// The directory element is the same as element above but is added to 
    // better describe a directory element as opposed to a "topclass"
    /**
     * Creator for directory element.
     */
    public Argument createDirectory() {
        Argument argument = new Argument();
        elements.add(argument);
        return(argument);
    }
    /**
     * Set the directory element.
     * @param arg Name of a class
     */
    public void setDirectory(String arg) {
        StringTokenizer st = new StringTokenizer(arg, ", \t\n\r\f");
        while(st.hasMoreTokens()) {
            elements.add(new Argument(st.nextToken()));
        }
    }


    /**
     * Set the classpath to be used for this compilation.
     */
    public void setClasspath(Path classpath) {
        if(compileClasspath == null) {
            compileClasspath = classpath;
        } else {
            compileClasspath.append(classpath);
        }
    }

    /**
     * Maybe creates a nested classpath element.
     */
    public Path createClasspath() {
        if(compileClasspath == null) {
            compileClasspath = new Path(getProject());
        }
        return(compileClasspath.createPath());
    }

    /**
     * Adds a reference to a CLASSPATH defined elsewhere.
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    protected Path getClasspath() {
        Path classpath = new Path(getProject());
        // add our classpath to the mix
        if(compileClasspath != null) {
            classpath.addExisting(compileClasspath);
        }
        // add the system classpath
        classpath.addExisting(Path.systemClasspath);
        return(classpath);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        Path classpath = getClasspath();
        if(classpath != null) {
            sb.append("-cp ");
            sb.append(classpath.toString());
            sb.append(' ');
        }
        if(edges)
            sb.append("-edges ");
        if(files)
            sb.append("-files ");
        if(outer)
            sb.append("-outer ");
        for(Iterator iter = in.iterator();iter.hasNext();) {
            sb.append("-in ");
            sb.append(((Argument)iter.next()).getName());
            sb.append(' ');
        }
        for(Iterator iter = out.iterator();iter.hasNext();) {
            sb.append("-out ");
            sb.append(((Argument)iter.next()).getName());
            sb.append(' ');
        }
        for(Iterator iter = hide.iterator();iter.hasNext();) {
            sb.append("-hide ");
            sb.append(((Argument)iter.next()).getName());
            sb.append(' ');
        }
        for(Iterator iter = prune.iterator();iter.hasNext();) {
            sb.append("-prune ");
            sb.append(((Argument)iter.next()).getName());
            sb.append(' ');
        }
        for(Iterator iter = show.iterator();iter.hasNext();) {
            sb.append("-show ");
            sb.append(((Argument)iter.next()).getName());
            sb.append(' ');
        }
        for(Iterator iter = skip.iterator();iter.hasNext();) {
            sb.append("-skip ");
            sb.append(((Argument)iter.next()).getName());
            sb.append(' ');
        }
        for(Iterator iter = tell.iterator();iter.hasNext();) {
            sb.append("-tell ");
            sb.append(((Argument)iter.next()).getName());
            sb.append(' ');
        }
        for(Iterator iter = elements.iterator();iter.hasNext();) {
            String arg = ((Argument)iter.next()).getName(); 
            if(debug)
                System.out.println("Processing: " + arg);
            if(File.separatorChar == '/') {
                if(arg.indexOf('\\') >= 0) {
                    arg = arg.replace('\\', File.separatorChar);
                }
            } else if(File.separatorChar == '\\') {
                if(arg.indexOf('/') >= 0) {
                    arg = arg.replace('/', File.separatorChar);
                }
            }
            sb.append(arg);
            sb.append(' ');
        }
        return(sb.toString().trim());
    }

    public String[] toArgs() {
        ArrayList ar = new ArrayList();
        Path classpath = getClasspath();
        if(classpath != null) {
            ar.add("-cp");
            ar.add(classpath.toString());
        }
        if(edges)
            ar.add("-edges");
        if(files)
            ar.add("-files");
        if(outer)
            ar.add("-outer");
        for(Iterator iter = in.iterator();iter.hasNext();) {
            ar.add("-in");
            ar.add(((Argument)iter.next()).getName());
        }
        for(Iterator iter = out.iterator();iter.hasNext();) {
            ar.add("-out");
            ar.add(((Argument)iter.next()).getName());
        }
        for(Iterator iter = hide.iterator();iter.hasNext();) {
            ar.add("-hide");
            ar.add(((Argument)iter.next()).getName());
        }
        for(Iterator iter = prune.iterator();iter.hasNext();) {
            ar.add("-prune");
            ar.add(((Argument)iter.next()).getName());
        }
        for(Iterator iter = show.iterator();iter.hasNext();) {
            ar.add("-show");
            ar.add(((Argument)iter.next()).getName());
        }
        for(Iterator iter = skip.iterator();iter.hasNext();) {
            ar.add("-skip");
            ar.add(((Argument)iter.next()).getName());
        }
        for(Iterator iter = tell.iterator();iter.hasNext();) {
            ar.add("-tell");
            ar.add(((Argument)iter.next()).getName());
        }
        for(Iterator iter = elements.iterator();iter.hasNext();) {
            String arg = ((Argument)iter.next()).getName();
            if(File.separatorChar == '/') {
                if(arg.indexOf('\\') >= 0) {
                    arg = arg.replace('\\', File.separatorChar);
                }
            } else if(File.separatorChar == '\\') {
                if(arg.indexOf('/') >= 0) {
                    arg = arg.replace('/', File.separatorChar);
                }
            }
            ar.add(arg);
        }
        return(String[])ar.toArray(new String[ar.size()]);
    }

    private void compute() {
        //System.out.println("In Compute");
        log("identity:  " + this, Project.MSG_DEBUG);
        classdep.setupOptions(toArgs());
        String[] vals = classdep.compute();
        
        log("vals.length = " + vals.length, Project.MSG_DEBUG);

        //FileSet set = new FileSet();
        // always set the classdep dir to current dir.
        //set.setDir(new File("./"));

        for(int i = 0; i < vals.length; i++) {
            if(files)
                //vals[i] = vals[i].replace('.', File.separatorChar) + ".class";
                vals[i] = vals[i].replace('.', '/') + ".class";
            setIncludes(vals[i]);
            //PatternSet.NameEntry name = set.createInclude();
            PatternSet.NameEntry name = createInclude();
            name.setName(vals[i]);
            log(i + "). " + vals[i], Project.MSG_VERBOSE);
        }
        //if(vals.length > 0)
        //addFileset(set);
    }

    public void execute() throws BuildException {
        compute();
        super.execute();
    }

    /** 
     * Getter for property debug.
     * @return Value of property debug.
     */
    public boolean isDebug() {
        return(debug);
    }

    /** 
     * Setter for property debug.
     * @param debug New value of property debug.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
