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
package com.sun.jini.tool;

import sun.tools.java.BinaryClass;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassFile;
import sun.tools.java.ClassNotFound;
import sun.tools.java.ClassPath;
import sun.tools.java.Constants;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Package;
import sun.tools.java.Type;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;

/**
 * Utility class to inspect a set of binary classes
 * and determine what other classes they depend upon.
 * This set of classes is defined by the intersection
 * of several sets:
 * <ul>
 *   <li> inside set  - this is the set of packages 
 *                      that we are interested in.
 *                      <p>
 *   <li> outside set - this is the set of packages
 *                      that we are not interested in.
 *                      This should be a subset of the 
 *                      inside set.
 *                      <p>
 *   <li> class set   - this is a set of individually 
 *                      listed classes that we want to 
 *                      include in our working set.
 *                      <p>
 *   <li> skip set    - this is a set of classes that 
 *                      should be ignored during the 
 *                      generation of dependencies.
 *                      <p>
 *   <li> prune set   - this is a set of packages to 
 *                      skip over during the processing 
 *                      of dependencies.
 *                      <p>
 *   <li> edges set   - this is the set of classes that 
 *                      have references that lie outside 
 *                      the inside set.
 * </ul>
 * The final output of the results can be further 
 * modified by the following:
 * <ul>
 *   <li> hide set    - this is a set of packages whose
 *                      classes we want to inhibit from
 *                      being displayed at all. This is
 *                      normally a subset of the inside
 *                      set.
 *                      <p>
 *  <li> show set     - this is a set of packages whose
 *                      classes we want to display. This
 *                      is normally a subset of the inside
 *                      set.
 *                      <p>
 *  <li> files/dots   - the output can be generated with
 *                      the system file separator or
 *                      using dots e.g foo/bar/blue
 *                      versus foo.bar.blue .
 *                      <p>
 * </ul>  
 *                       
 * And for Debugging purposes:
 * <ul>
 *   <li> tell set    - this is a set of classes for which
 *                      we want to find out what other classes
 *                      depend upon them.
 * </ul>
 * 
 * For optimization purposes we have a way to include
 * the outer parent of a static inner class in the 
 * the list, if an inner classes is found to be dependent.
 * The default is to not include the parent.
 *
 * @author Sun Microsystems, Inc.
 *
 */
public class ClassDep {

    /**
     * Container for all the classes that we have seen.
     */
    private final HashSet seen = new HashSet();

    /**
     * Object used to load our classes.
     */
    private Env env;

    /**
     * If true class names are printed using
     * the system's File.separator, else the 
     * fully qualified class name is printed.
     */
    private boolean files = false;

    /**
     * Set of paths to find class definitions in order to determine 
     * dependencies.
     */
    private String classpath = "";

    /** the calling task */
    private Task callingTask;

    /**
     * Flag to determine whether there is interest
     * in dependencies that go outside the set of 
     * interested classes. If false then outside,
     * references are ignored, if true they are noted.
     * i.e, if looking only under <code>net.jini.core.lease</code>
     * a reference to a class in <code>net.jini</code> is found it
     * will be noted if the flag is set to true, else
     * it will be ignored. <p>
     * <b>Note:</b> these edge case dependencies must be
     * included in the classpath in order to find their
     * definitions.
     */
    private  boolean edges = false;

    /**
     * Static inner classes have a dependency on their outer
     * parent class. Because the parent class may be really
     * big and may pull other classes along with it we allow the
     * choice to ignore the parent or not. If the flag is set to
     * true we pull in the parent class. If it is false we don't
     * look at the parent. The default is is to not include the 
     * parent. <p>
     * <b>Note:</b> This is an optimization for those who plan
     * on doing work with the output of this utility. It does
     * not impact this utility, but the work done on its 
     * generated output  may have an impact. 
     */
    private  boolean ignoreOuter = true;

    /**
     * Package set that we have interest to work in.
     */
    private  final ArrayList inside  = new ArrayList();

    /**
     * Package set to not work with. This is useful if
     * there is a subpackage that needs to be ignored.
     */
    private  final ArrayList outside = new ArrayList();

    /**
     * Class set to look at for dependencies. These are
     * fully qualified names, ie, net.jini.core.lease.Lease.
     * This is a subset of the values in
     * <code>inside</code>.
     */
    private  final ArrayList classes = new ArrayList();

    /**
     * Set of directories to find dependencies in.
     */
    private  final ArrayList roots   = new ArrayList();

    /**
     * Set of packages to skip over in the processing of dependencies.
     * This can be used in conjunction with <em>-out</em> option.
     */
    private  final ArrayList prunes  = new ArrayList();

    /**
     * Set of package prefixes to skip over in the processing 
     * of dependencies. 
     */
    private  final ArrayList skips   = new ArrayList();

    /**
     * Given a specific fully qualified classes, what other classes
     * in the roots list depend on it. This is more for debugging
     * purposes rather then normal day to day usage.
     */
    private  final ArrayList tells   = new ArrayList();

    /**
     * Only display found dependencies that fall under the provided
     * <code>roots</code> subset.
     */
    private  final ArrayList shows   = new ArrayList();

    /**
     * Suppress display of found dependencies that are under
     * the provided package prefixes subset.
     */
    private  final ArrayList hides   = new ArrayList();

    /**
     * Container for found dependency classes.
     */
    private final ArrayList results = new ArrayList();

    /**
     * No argument constructor. The user must fill in the
     * appropriate fields prior to asking for the processing
     * of dependencies.
     *
     * @see #addHides
     */
    public ClassDep(Task callingTask) {
        this.callingTask = callingTask;
    }

    /**
     * Constructor that takes the commandLine arguments and
     * fills in the appropriate fields.
     * 
     * @see #main
     */
    public ClassDep(String[] cmdLine, Task callingTask) {
        this.callingTask = callingTask;        
        setupOptions(cmdLine);
        log("command line is: ");
        for(int i=0; i<cmdLine.length; i++) {
            log("\n\t arg["+i+"]= "+cmdLine[i]);
        }
    }

    /**
     * Take the given argument and add it to the provided container.
     * We make sure that each inserted package-prefix is unique. For
     * example if we had the following packages:
     * <ul>
     *     <li>a.b
     *     <li>a.bx
     *     <li>a.b.c
     * </ul>
     * Looking for <code>a.b</code> should not match 
     * <code>a.bx</code> and <code>a.b</code>, 
     * just <code>a.b</code>.
     * 
     * @param arg  the package-prefix in string form.
     * @param elts container to add elements to.
     *
     */
    private static void add(String arg, ArrayList elts) {
        if(!arg.endsWith("."))
            arg = arg + '.';
        if(".".equals(arg))
            arg = null;
        elts.add(arg);
    }

    /**
     * See if the provided package-prefix can be found within the
     * handed set of elements.
     *
     * @param n    the string that we match to look for.
     * @param elts the list of elements to search on.
     *
     * @return true if there is match, false otherwise.
     * 
     */
    private static boolean matches(String n, ArrayList elts) {
        for(int i = 0; i < elts.size(); i++) {
            String elt = (String)elts.get(i);
            /*
             * If we get a null element then see if we are looking
             * at an anonymous package.
             */
            if(elt == null) {
                int j = n.indexOf('.');
                /*
                 * If we did not find a dot, or we have a space 
                 * at the beginning then we have an anonymous package.
                 */
                if(j < 0 || n.charAt(j + 1) == ' ')
                    return true;
            } else if(n.startsWith(elt))
                return true;
        }
        return false;
    }

    /**
     * Recursively traverse a given path, finding all the classes that
     * make up the set to work with. We take into account skips,
     * prunes, and out sets defined.
     *
     * @param path String representation of the directory to look under.
     *
     */
    private void traverse(String path) {

        String apath = path;

        /*
         * We append File.separator to make sure that the path
         * is unique for the matching that we are going to do
         * next. 
         */
        if(!apath.startsWith(File.separator))
            apath = File.separator + apath;

        log("ClassDep(local): in traversal = prunes size=" + prunes.size());
        for(int i = 0; i < prunes.size(); i++) {
            /*
             * If we are on a root path that needs to be 
             * pruned leave this current recursive thread.
             */
            if(apath.endsWith((String)prunes.get(i))) {
                log("ClassDep(local): popping (prune match test) wrong file ending!!!");
                return;
            }
        }

        /*
         * Get the current list of files at the current directory 
         * we are in. If there are no files then leave this current
         * recursive thread.
         */
        String[] files = new File(path).list();
        if(files == null) {
            log("ClassDep(local): popping (empty directory test) no files found at <"+path+"> ??????");
            return;
        }
        outer:
        /*
         * Now, take the found list of files and iterate over them.
         */

        for(int i = 0; i < files.length; i++) {
            String file = files[i];

            log("ClassDep(local): in traversal = files size=" + files.length);
            /*
             * Now see if we have a ".class" file.
             * If we do not then we lets call ourselves again.
             * The assumption here is that we have a directory. If it
             * is a class file we would have already been throw out
             * by the empty directory contents test above.
             */
            if(!file.endsWith(".class")) {
                log("ClassDep(local): in traversal **(recursing on)** on " + path + File.separatorChar + file);
                traverse(path + File.separatorChar + file);
            } else {
                /*
                 * We have a class file, so remove the ".class" from it
                 * using the pattern:
                 * 
                 *     directory_name + File.Separator + filename = ".class"
                 *
                 * At this point the contents of the skip container follow
                 * the pattern of:
                 *
                 *     "File.Separator+DirectoryPath"
                 *
                 * with dots converted to File.Separators
                 */
                file = apath + File.separatorChar +
                       file.substring(0, file.length() - 6);
                /*
                 * See if there are any class files that need to be skipped.
                 */
                log("ClassDep(local): in traversal = skips size=" + skips.size());
                for(int j = 0; j < skips.size(); j++) {
                    String skip = (String)skips.get(j);
                    int k = file.indexOf(skip);
                    if(k < 0)
                        continue;//leave this current loop.
                    k += skip.length();
                    /*
                     * If we matched the entire class or if we have
                     * a class with an inner class, skip it and go
                     * on to the next outer loop.
                     */
                    if(file.length() == k || file.charAt(k) == '$')
                        continue outer;
                }
                /*
                 * things to do:
                 * prune when outside.
                 * handle inside when its empty.
                 *
                 * Now see if we have classes within our working set "in".
                 * If so add them to our working list "classes".
                 */
                for(int j = 0; j < inside.size(); j++) {
                    int k = file.indexOf(File.separatorChar +
                                         ((String)inside.get(j)).replace(
                                                                        '.', File.separatorChar));
                    if(k >= 0) {
                        /*
                         * Insert the class and make sure to replace
                         * File.separators into dots.
                         */
                        classes.add(file.substring(k + 1).replace(
                                                                 File.separatorChar, '.'));
                    }
                }
            }
        }
    }

    /**
     * Depending on the part of the class file
     * that we are on the class types that we are
     * looking for can come in several flavors.
     * They can be embedded in arrays, they can
     * be labeled as Identifiers, or they can be
     * labeled as Types. This method handles 
     * Types referenced by Identifiers. It'll take
     * the Type and proceed to get its classname 
     * and then continue with the processing it
     * for dependencies.
     */
    private void process(Identifier from, Type type) {
        while(type.isType(Constants.TC_ARRAY))
            type = type.getElementType();
        if(type.isType(Constants.TC_CLASS))
            process(from, type.getClassName());
    }

    /**
     * Depending on the part of the class file
     * that we are on the class types that we are
     * looking for can come in several flavors.
     * This method handles Identifiers and 
     * Identifiers referenced from other Identifiers.
     * <p>
     * Several actions happen here with the goal of 
     * generating the list of dependencies within the domain
     * space provided by the user.
     * These actions are:
     * <ul>
     *    <li> printing out "-tell" output if user asks for it.
     *    <li> extracting class types from the class file.
     *         <ul>
     *             <li> either in arrays or by
     *             <li> themselves
     *         </ul>
     *    <li> noting classes we have already seen.
     *    <li> traversing the remainder of the class file.
     *    <li> resolving and looking for dependencies in
     *         inner classes.
     *    <li> saving found results for later use.
     * </ul>
     *
     * @param from the Identifier referenced from <code>id</code>
     * @param id   the Identifier being looked at
     */
    private void process(Identifier from, Identifier id) {
        /*
         * If <code>from</code> is not null see if the "id" that
         * references it is in our "tells" container. If there
         * is a match show the class. This is for debugging purposes,
         * in case you want to find out what classes use a particular class.
         */ 
        if(from != null) {
            for(int i = 0; i < tells.size(); i++) {
                if(id.toString().equals((String)tells.get(i))) {
                    if(tells.size() > 1)
                        print("classdep.cause", id, from);
                    else
                        print("classdep.cause1", from);
                }
            }
        }

        /*
         * Having taken care of the "-tells" switch, lets
         * proceed with the rest by getting the id's string
         * representation.
         */
        String n = id.toString();

        /*
         * Remove any array definitions so we can get to the
         * fully qualified class name that we are seeking.
         */
        if(n.charAt(0) == '[') {
            int i = 1;
            while(n.charAt(i) == '[')
                i++;
            /*
             * Now that we have removed possible array information 
             * see if we have a Class definition e.g Ljava/lang/Object;.
             * If so, remove the 'L' and ';' and call ourselves
             * with this newly cleaned up Identifier.
             */
            if(n.charAt(i) == 'L')
                process(from,
                        Identifier.lookup(n.substring(i + 1, n.length() - 1)));
            /*
             * Pop out of our recursive path, since the real work
             * is being down in another recursive thread.
             */
            return;
        }

        /*
         * If we have already seen the current Identifier, end this
         * thread of recursion.
         */
        if(seen.contains(id))
            return;

        /*
         * See if we have an empty set OR the Identifier is in our
         * "inside" set and the matched Identifier is not on the
         * "outside" set.
         *
         * If we are not in the "inside" set and we are not asking
         * for edges then pop out of this recursive thread.
         */
        boolean in = ((inside.isEmpty() || matches(n, inside)) &&
                      !matches(n, outside));
        if(!in && !edges)
            return;

        /*
         * We have an actual Identifier, so at this point mark it
         * as seen, so we don't create another recursive thread if
         * we see it again.
         */
        seen.add(id);

        /*
         * This is the test that decides whether this current
         * Identifier needs to be added to the list of dependencies
         * to save.
         *
         * "in" can be true in the following cases:
         * <ul>
         *   <li>the in set is empty
         *   <li>the Identifier is in the "in" set and not on the "out" set.
         * </ul>
         */       
        if(in != edges &&
           (shows.isEmpty() || matches(n, shows)) &&
           !matches(n, hides))
            results.add(Type.mangleInnerType(id).toString());

        /*
         * If we are not in the "inside" set and we want edges
         * pop out of our recursive thread.
         */
        if(!in && edges)
            return;

        /*
         * At this point we have either added an Identifier
         * to our save list, or we have not. In either case
         * we need get the package qualified name of this so
         * we can see if it has any nested classes.
         */
        id = env.resolvePackageQualifiedName(id);
        BinaryClass cdef;
        try {
            cdef = (BinaryClass)env.getClassDefinition(id);
            cdef.loadNested(env);
        } catch(ClassNotFound e) {
            print("classdep.notfound", id);
            return;
        } catch(IllegalArgumentException e) {
            print("classdep.illegal", id, e.getMessage());
            return;
        } catch(Exception e) {
            print("classdep.failed", id);
            e.printStackTrace();
            return;
        }

        /*
         * If the user asked to keep the outer parent for an
         * inner class then we'll get the list of dependencies
         * the inner class may have and iterate over then by
         * "processing" them as well.
         */
        Identifier outer = null;
        if(ignoreOuter && cdef.isInnerClass() && cdef.isStatic())
            outer = cdef.getOuterClass().getName();
        for(Enumeration en = cdef.getDependencies();
           en.hasMoreElements();) {
            Identifier dep = ((ClassDeclaration)en.nextElement()).getName();
            /*
             * If we dont' want the outer parent class of an inner class
             * make this comparison.
             */
            if(outer != dep)
                process(id, dep);
        }


        /*
         * Now we are going to walk the rest of the class file and see
         * if we can find any other class references.
         */
        for(MemberDefinition mem = cdef.getFirstMember();
           mem != null;
           mem = mem.getNextMember()) {
            if(mem.isVariable()) {
                process(id, mem.getType());
            } else if(mem.isMethod() || mem.isConstructor()) {
                Type[] args = mem.getType().getArgumentTypes();
                for(int i = 0; i < args.length; i++) {
                    process(id, args[i]);
                }
                process(id, mem.getType().getReturnType());
            }
        }
    }

    private void log(String logMessage) {
        if(callingTask != null) {
            callingTask.log(logMessage, Project.MSG_DEBUG);
        }
    }

    /**
     * Method that takes the user provided switches that
     * logically define the domain in which to look for
     * dependencies. 
     */
    public String[] compute() {
        /*
         * Create the environment from which we are going to be
         * loading classes from.
         */
        env = new Env(classpath);

        /*
         * Traverse the roots i.e the set of handed directories.
         */
        log("ClassDep(local): before traversal : classes size = " + classes.size());
        log("ClassDep(local): roots size = " + roots.size());
        for(int i = 0; i < roots.size(); i++) {
            /*
             * Get the classes that we want do to dependency checking on.
             */
            String rootReturn = (String) roots.get(i);
            log("ClassDep(local): roots is = " + rootReturn);
            traverse(rootReturn);
        }
        log("ClassDep(local): after traversal : classes size=" + classes.size());
        for(int i = 0; i < classes.size(); i++) {
            process(null, Identifier.lookup((String)classes.get(i)));
        }
        if(!tells.isEmpty())
            return new String[0];
        String[] vals = (String[])results.toArray(new String[results.size()]);
        Arrays.sort(vals);
        return vals;
    }

    /**
     * Print out the usage for this utility.
     */
    public static void usage() {
        print("classdep.usage", null);
    }

    /**
     * Set the classpath to use for finding our class definitions.
     */
    public void setClassPath(String classpath) {
        this.classpath = classpath;
    }

    /**
     * Determines how to print out the fully qualified
     * class names. If <code>true</code> it will use
     * <code>File.separator</code>, else <code>.</code>'s
     * will be used. 
     * If not set the default is <code>false</code>.
     */
    public void setFiles(boolean files) {
        this.files = files;
    }

    /**
     * Add an entry into the set of package prefixes that
     * are to remain hidden from processing.
     */
    public void addHides(String packagePrefix) {
        add(packagePrefix, hides);
    }

    /**
     * Add an entry into the working set of package prefixes 
     * that will make up the working domain space.
     */
    public void addInside(String packagePrefix) {
        add(packagePrefix, inside);
    }

    /**
     * Determines whether to include package references
     * that lie outside the declared set of interest.
     * <p>
     * If true edges will be processed as well, else
     * they will be ignored. If not set the default
     * will be <code>false</code>.
     * <p>
     * <b>Note:</b> These edge classes must included
     * in the classpath for this utility.
     *
     * @see #addInside
     * @see #setClassPath
     */
    public void setEdges(boolean edges) {
        this.edges = edges;
    }

    /**
     * Add an entry into the set of package prefixes
     * that will bypassed during dependency checking.
     * These entries should be subsets of the contents
     * on the inside set.
     * 
     * @see #addInside
     */
    public void addOutside(String packagePrefix) {
        add(packagePrefix, outside);
    }

    /**
     * Add an entry into the set of package prefixes
     * that will be skipped as part of the dependency
     * generation.
     */
    public void addPrune(String packagePrefix) {
        String arg = packagePrefix;
        if(arg.endsWith("."))
            arg = arg.substring(0, arg.length() - 1);
        /*
         * Convert dots into File.separator for later usage.
         */
        arg = File.separator + arg.replace('.', File.separatorChar);
        prunes.add(arg);
    }

    /**
     * Add an entry into the set of package prefixes
     * that we want to display.
     * This applies only to the final output, so this
     * set should be a subset of the inside set with
     * edges, if that was indicated.
     */
    public void addShow(String packagePrefix) {
        add(packagePrefix, shows);
    }

    /**
     * Add an entry into the set of classes that
     * should be skipped during dependency generation.
     */
    public void addSkip(String packagePrefix) {
        String arg = packagePrefix;
        if(arg.endsWith("."))
            arg = arg.substring(0, arg.length() - 1);
        else
            seen.add(Identifier.lookup(arg));
        /*
         * Convert dots into File.separator for later usage.
         */
        arg = File.separator + arg.replace('.', File.separatorChar);
        skips.add(arg);
    }

    /**
     * Add an entry in to the set of classes whose dependents
     * that lie with the inside set are listed. This in
     * the converse of the rest of the utility and is meant
     * more for debugging purposes.
     *
     * @see inside
     */
    public void addTells(String packagePrefix) {
        tells.add(packagePrefix);
    }

    /**
     * Add an entry into the set of directories to
     * look under for the classes that fall within
     * the working domain space as defined by the
     * intersection of the following sets:
     * inside,outside,prune,show, and hide.
     */
    public void addRoots(String rootName) {
        if(rootName.endsWith(File.separator))
            //remove trailing File.separator
            rootName = rootName.substring(0, rootName.length() - 1);
        //these are directories.
        roots.add(rootName);
        log("addRoots:: adding new root ="+rootName);
    }

    /**
     * Add an entry into the set of classes that
     * dependencies are going to be computed on.
     */
    public void addClasses(String className) {
        classes.add(className);
    }

    /**
     * If true classnames will be separated using
     * File.separator, else it will use dots.
     *
     # @see File.separator
     */
    public boolean getFiles() {
        return files;
    }

    /**
     * Accessor method for the found dependencies.
     */
    public String[] getResults() {
        String[] vals = (String[])results.toArray(new String[results.size()]);
        Arrays.sort(vals);
        return vals;
    }

    /**
     * Convenience method for handing a command line
     * to this utility and have it parsed and fill
     * in the appropriate collections with their
     * respective data.
     */
    public void setupOptions(String[] args) {
        for(int i = 0; i < args.length ; i++) {
            String arg = args[i];
            if(arg.equals("-cp")) {
                i++;
                setClassPath(args[i]);
            } else if(arg.equals("-files")) {
                setFiles(true);
            } else if(arg.equals("-hide")) {
                i++;
                addHides(args[i]);
            } else if(arg.equals("-in")) {
                i++;
                addInside(args[i]);
            } else if(arg.equals("-edges")) {
                setEdges(true);
            } else if(arg.equals("-out")) {
                i++;
                addOutside(args[i]);
            } else if(arg.equals("-outer")) {
                ignoreOuter = false;
            } else if(arg.equals("-prune")) {
                i++;
                addPrune(args[i]);
            } else if(arg.equals("-show")) {
                i++;
                addShow(args[i]);
            } else if(arg.equals("-skip")) {
                i++;
                addSkip(args[i]);
            } else if(arg.equals("-tell")) {
                i++;
                addTells(args[i]);
            } else if(arg.indexOf(File.separator) >= 0) {
                addRoots(arg);
            } else if(arg.startsWith("-")) {
                usage();
            } else {
                addClasses(arg);
            }
        }

    }

    private static ResourceBundle resources;
    private static boolean resinit = false;

    /**
     * Get the strings from our resource localization bundle.
     */
    private static String getString(String key) {
        if(!resinit) {
            try {
                resources = ResourceBundle.getBundle
                            ("com.sun.jini.tool.resources.classdep");
                resinit = true;
            } catch(MissingResourceException e) {
                e.printStackTrace();
            }
        }
        try {
            return resources.getString(key);
        } catch(MissingResourceException e) {
            return null;
        }
    }

    /**
     * Print out string according to resourceBundle format.
     */
    private static void print(String key, Object val) {
        String fmt = getString(key);
        if(fmt == null)
            fmt = "no text found: \"" + key + "\" {0}";
        System.err.println(MessageFormat.format(fmt, new Object[]{val}));
    }

    /**
     * Print out string according to resourceBundle format.
     */
    private static void print(String key, Object val1, Object val2) {
        String fmt = getString(key);
        if(fmt == null)
            fmt = "no text found: \"" + key + "\" {0} {1}";
        System.err.println(MessageFormat.format(fmt,
                                                new Object[]{val1, val2}));
    }

    /**
     * Command line interface for generating the list of classes that
     * a set of classes depends upon.
     * The command line parameters are:
     * <pre>
     * -cp    &lt;classpath&gt; 
     * -edges
     * -files 
     * -hide  &lt;package-prefix&gt;
     * -in    &lt;package-prefix&gt;
     * -out   &lt;package-prefix&gt;
     * -outer 
     * -prune &lt;package-prefix&gt;
     * -show  &lt;package-prefix&gt;
     * -skip  &lt;class&gt;
     * -tell  &lt;class&gt;
     * [javaclass | directory ]
     * </pre>
     *
     * java -cp \files\jini1_0_1\lib\tools.jar;\files\jdk1.2.2\lib\tools.jar
     * com.sun.jini.tool.ClassDep &lt;options&gt;
     *
     *<p>
     * The usual options you need are:
     * -cp &lt;classpath&gt; -in &lt;package-prefix&gt; -files &lt;class&gt;
     * which mean the following:
     *
     * ClassDep then starts from the top-level classes, finds all of the classes
     * that are transitively depended on that are in the namespaces, and prints
     * them out.  You take that output and feed it as command line values
     * to a <code>jar</code> tool command.
     *
     * @param cp    This classpath should include all of your classes, plus
     *              any Jini(TM) classes. It does not need to include any
     *              classes that are defined by J2SE(TM). If you use JAR
     *              files, any Class-Path: manifest entries are ignored, so
     *              for example, if you include jini-ext.jar you also have to 
     *              explicitly include jini-core.jar. 
     *              <p>
     * @param edges This specifies that classes that are referenced
     *              outside the namespace should also be included.
     *              <p>
     * @param files This causes the output to be in filename format instead 
     *              of classname format.  I.e., com\corp\foo\Bar.class 
     *              instead of com.corp.foo.Bar. You generally want this 
     *              option so that you can feed the output into a
     *              <code>jar</code> tool command.
     *              <p>
     * @param hide  Suppresses the the display of any classes in this set.
     *              <p>
     * @param in    This specifies the namespace of classes that should be 
     *              included in your download JAR file.  You can specify this 
     *              option multiple times. For example, if your classes are in 
     *              a com.corp.foo package, and you also use some or our 
     *              com.sun.jini classes in your stub/proxy or attributes 
     *              (e.g., com.sun.jini.admin.DestroyAdmin or 
     *              com.sun.jini.lookup.entry.BasicServiceType), then you would 
     *              specify -in com.corp.foo -in com.sun.jini -in net.jini. 
     *              Note that these are namespaces, so they cover all 
     *              subpackages.
     *              <p>
     * @param out   This specifies the namespace of classes that should not be
     *              included in your download JAR file. You can specify this
     *              option multiple times. For example, if your classes are in 
     *              a com.corp.foo package, and you also use some or our 
     *              com.sun.jini classes in your stub/proxy or attributes 
     *              (e.g., com.sun.jini.admin.DestroyAdmin or 
     *              com.sun.jini.lookup.entry.BasicServiceType), then you would 
     *              specify -out com.corp.foo -out com.sun.jini -out net.jini. 
     *              Note that these are namespaces, so they cover all 
     *              subpackages.
     *              <p>
     * @param outer If used causes the parent class of a static inner class
     *              to be included in the dependency checking as well. This
     *              will cause the parent and the inner class to be listed.
     *              <p>
     * @param prune completely skip over the specified namespace of classes.
     *              <p>
     * @param show  Display only a subset of total dependency set.
     *              <p>
     * @param skip  This specifies what classes to ignore in determining
     *              dependencies.
     *              <p>
     * @param tell  More for debugging. It this switch will tell what
     *              classes in your -cp namespace use the class name
     *              argument handed to the switch.
     *              <p>
     * @param class You can specify this option multiple times, listing 
     *              all of the top-level classes that you know need to be 
     *              included. Typically you need to include your top-level 
     *              proxy or stub class and any entry classes. If your proxy
     *              object has an RMI stub inside of it, then you also need 
     *              to include that stub class. If your proxy object has 
     *              admin methods that return other proxies or stubs, you 
     *              need to include those top-level classes too. If your
     *              proxy/stub returns lease, registration, or event objects,
     *              and their concrete classes are never explicitly mentioned 
     *              exception in your backend server code, you need to 
     *              include those top-level classes too.  All of the classes
     *              you specify need to be in the namespaces covered by -in 
     *              options. In general, you only need to specify concrete 
     *              classes, not interface types.
     *              <p>
     * @param dir  Set of directories to find the actual class definitions.
     */
    public static void main(String[] args) {
        if(args.length == 0) {
            usage();
            return;
        }
        ClassDep dep = new ClassDep(null);
        //boolean files = false;
        dep.setupOptions(args);
        String[] vals = dep.compute();
        for(int i = 0; i < vals.length; i++) {
            if(dep.getFiles())
                System.out.println(vals[i].replace('.', File.separatorChar) +
                                   ".class");
            else
                System.out.println(vals[i]);
        }
    }

    /**
     * Private class to load classes, resolve class names and report errors. 
     *
     * @see sun.tools.java.Environment
     */
    private static class Env extends Environment {
        private final ClassPath noPath = new ClassPath("");
        private final ClassPath path;
        private final HashMap packages = new HashMap();
        private final HashMap classes = new HashMap();

        public Env(String classpath) {
            for(StringTokenizer st =
                new StringTokenizer(System.getProperty("java.ext.dirs"),
                                    File.pathSeparator);
               st.hasMoreTokens();) {
                String dir = st.nextToken();
                String[] files = new File(dir).list();
                if(files != null) {
                    if(!dir.endsWith(File.separator)) {
                        dir += File.separator;
                    }
                    for(int i = files.length; --i >= 0;) {
                        classpath =
                        dir + files[i] + File.pathSeparator + classpath;
                    }
                }
            }
            path = new ClassPath(System.getProperty("sun.boot.class.path") +
                                 File.pathSeparator + classpath);
        }

        /**
         * We don't use flags so we override Environments and
         * simply return 0.
         */
        public int getFlags() {
            return 0;
        }
        /**
         * Take the identifier and see if the class that represents exists.
         * <code>true</code> if the identifier is found, <code>false</code>
         * otherwise.
         */
        public boolean classExists(Identifier id) {
            if(id.isInner())
                id = id.getTopName();
            Type t = Type.tClass(id);
            try {
                ClassDeclaration c = (ClassDeclaration)classes.get(t);
                if(c == null) {
                    Package pkg = getPackage(id.getQualifier());
                    return pkg.getBinaryFile(id.getName()) != null;
                }
                return c.getName().equals(id);
            } catch(IOException e) {
                return false;
            }
        }

        public ClassDeclaration getClassDeclaration(Identifier id) {
            return getClassDeclaration(Type.tClass(id));
        }

        public ClassDeclaration getClassDeclaration(Type t) {
            ClassDeclaration c = (ClassDeclaration)classes.get(t);
            if(c == null) {
                c = new ClassDeclaration(t.getClassName());
                classes.put(t, c);
            }
            return c;
        }

        public Package getPackage(Identifier pkg) throws IOException {
            Package p = (Package)packages.get(pkg);
            if(p == null) {
                p = new Package(noPath, path, pkg);
                packages.put(pkg, p);
            }
            return p;
        }

        BinaryClass loadFile(ClassFile file) throws IOException {
            DataInputStream in =
            new DataInputStream(new BufferedInputStream(
                                                       file.getInputStream()));
            try {
                return BinaryClass.load(new Environment(this, file), in,
                                        ATT_ALLCLASSES);
            } catch(ClassFormatError e) {
                throw new IllegalArgumentException("ClassFormatError: " +
                                                   file.getPath());
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                in.close();
            }
        }

        /**
         * Overridden method from Environment
         */
        public void loadDefinition(ClassDeclaration c) {
            Identifier id = c.getName();
            if(c.getStatus() != CS_UNDEFINED)
                throw new IllegalArgumentException("No file for: " + id);
            Package pkg;
            try {
                pkg = getPackage(id.getQualifier());
            } catch(IOException e) {
                throw new IllegalArgumentException("IOException: " +
                                                   e.getMessage());
            }
            ClassFile file = pkg.getBinaryFile(id.getName());
            if(file == null)
                throw new IllegalArgumentException("No file for: " +
                                                   id.getName());
            BinaryClass bc;
            try {
                bc = loadFile(file);
            } catch(IOException e) {
                throw new IllegalArgumentException("IOException: " +
                                                   e.getMessage());
            }
            if(bc == null)
                throw new IllegalArgumentException("No class in: " +
                                                   file);
            if(!bc.getName().equals(id))
                throw new IllegalArgumentException("Wrong class in: " +
                                                   file);
            c.setDefinition(bc, CS_BINARY);
            bc.loadNested(this, ATT_ALLCLASSES);
        }
    }
}





