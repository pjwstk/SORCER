/*
 * Copyright 2010 the original author or authors.
 * Copyright 2010 SorcerSoft.org.
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

package sorcer.xml;

// JAXP APIs
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;

// exceptions
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

// file handling
import java.io.File;
import java.io.IOException;

// DOM related
import org.w3c.dom.*;

// xml document
import com.sun.xml.tree.XmlDocument;

// sorcer
import java.io.*;
import java.util.*;
import sorcer.core.*;
import sorcer.core.exertion.ServiceTask;
import sorcer.core.method.ServiceMethod;
import jgapp.util.Util;
import sorcer.service.ServiceJob;
import sorcer.util.*;


public class ServiceJobXMLDomParser {
    
    public static org.w3c.dom.Document document;
    
    public static final short NODE_TYPE_NONE		= 0;
    public static final short NODE_TYPE_ELEMENT		= 1;
    public static final short NODE_TYPE_ATTR		= 2;
    public static final short NODE_TYPE_TEXT		= 3;
    public static final short NODE_TYPE_CDATA		= 4;
    public static final short NODE_TYPE_ENTITYREF	= 5;
    public static final short NODE_TYPE_ENTITY		= 6;
    public static final short NODE_TYPE_PROCINSTR	= 7;
    public static final short NODE_TYPE_COMMENT		= 8;
    public static final short NODE_TYPE_DOCUMENT	= 9;
    public static final short NODE_TYPE_DOCTYPE		= 10;
    public static final short NODE_TYPE_DOCFRAGMENT	= 11;
    public static final short NODE_TYPE_NOTATION	= 12;

    private short childLevel = 0;



    private ServiceJob job = null;

    // temporary holders
    private ServiceTask task = null;
    private HashtableContext context = null;
    private DataNode dataNode = null;
    private ServiceContext taskContext = null;

    private Vector allTasks = new Vector();
    private Hashtable allContexts = new Hashtable();// name, ServiceContext
    private Vector allTaskContexts = new Vector();
    private Hashtable allDataNodes = new Hashtable();	// path, DataNode
    private Hashtable allTextNodes = new Hashtable();	// path, Strings

    private String text;


    
    /** Recursively traverse the document node and print the info */
    public void traverse(org.w3c.dom.Node node) {

	childLevel++;

	// termination condition
	if (node == null) {
	    childLevel--;
	    return;
	}

	// query itself
	short type = node.getNodeType();
	String name = node.getNodeName();
	String value = node.getNodeValue();
	// print value: remove white space at beginning and end
	/*
	System.out.println(childLevel + 
			   " type:" + type +
			   " name:" + name + 
			   " value:" + ((value==null)?"null":value.trim()));
	*/

	// query attributes
	if (type == NODE_TYPE_ELEMENT) {
	    NamedNodeMap attrs = node.getAttributes();
	    int attrLength = attrs.getLength();
	    org.w3c.dom.Node[] attrNodes = new org.w3c.dom.Node[attrLength];
	    for (int j = 0; j < attrLength; j++) {
		attrNodes[j] = attrs.item(j);
		traverse(attrNodes[j]);
	    }
	}
	
	// query children
	if (!node.hasChildNodes()) {
	    childLevel--;
	    return;
	}
	NodeList childNodes = node.getChildNodes();
	int length = childNodes.getLength();
	for (int i = 0; i < length; i++) {
	    org.w3c.dom.Node childNode = childNodes.item(i);
	    traverse(childNode);
	}

	childLevel--;
    }


    /*
    public ServiceContext constructContext(org.w3c.dom.Node node) {
    }

    public ServiceMethod constructMethod(org.w3c.dom.Node node) {
    }
    
    public ServiceTask constructTask(org.w3c.dom.Node node) {
    }
    */

    public ServiceJob constructServiceJob(org.w3c.dom.Node node) {
	
	job = null;
	
	if (node == null || !node.hasChildNodes()) return null;

	// query children -- doing recursion
	NodeList childNodes = node.getChildNodes();
	int length = childNodes.getLength();
	for (int i = 0; i < length; i++) {
	    org.w3c.dom.Node childNode = childNodes.item(i);
	    if (childNode.getNodeType() == NODE_TYPE_ELEMENT &&
		childNode.getNodeName().equals("ServiceJob")) {
		constructServiceObject(childNode);
		break;
	    }
	}	

	return job;
    }



    private void constructServiceObject(org.w3c.dom.Node node) {

	// termination condition
	if (node == null) return;

	short type = node.getNodeType();
	if (type != NODE_TYPE_ELEMENT &&
	    type != NODE_TYPE_TEXT)
	    return;	// ignore the rest node types
	
	// query itself
	String name = node.getNodeName();
	String value = node.getNodeValue();
	
	if (type == NODE_TYPE_TEXT) {
	    text = value.trim();
	    return;	// done
	}

	// query attributes
	Properties attrsTable = new Properties();
	NamedNodeMap attrs = node.getAttributes();
	int attrLength = attrs.getLength();
	for (int j = 0; j < attrLength; j++) {
	    org.w3c.dom.Node attrNode = attrs.item(j);
	    attrsTable.setProperty(attrNode.getNodeName(),
				   attrNode.getNodeValue().trim());
	}

	// pre-process (initilize all data types)
	if (name.equals("ServiceJob")) {
	    job = new ServiceJob(attrsTable.getProperty("name"));
	} else if (name.equals("ServiceTask")) {
	    // method, provider
	    ServiceMethod method = new 
		ServiceMethod(attrsTable.getProperty("method"),
			    attrsTable.getProperty("provider"));
	    // service type
	    if (attrsTable.getProperty("serviceType") != null)
		method.serviceType = attrsTable.getProperty("serviceType");
	    // task name, description, method
	    task = new ServiceTask(attrsTable.getProperty("name"),
				 attrsTable.getProperty("description"),
				 null,	// no contexts yet
				 method);
	    // task ID
	    if (attrsTable.getProperty("taskID") != null)
		task.taskID = attrsTable.getProperty("taskID");
	} else if (name.equals("ServiceContext")) {
	    String contextName = attrsTable.getProperty("name");
	    context = new HashtableContext(contextName);
	} else if (name.equals("DataNode")) {
	    String data = attrsTable.getProperty("data");
	    dataNode = 
		new DataNode("",
			     data,
			     (attrsTable.getProperty("ioType").equals("TYPE_IN")?
			      DataNode.TYPE_IN : DataNode.TYPE_OUT),
			     attrsTable.getProperty("NodeType"),
			     (attrsTable.getProperty("isTransient").equals("true")?
			      true : false));
	} else if (name.equals("TextNode")) {
	    // don't need to do anything yet
	} else if (name.equals("TaskContext")) {
	    taskContext = (HashtableContext)allContexts.get(attrsTable.getProperty("root"));
	}
				    

	// query children -- doing recursion
	NodeList childNodes = node.getChildNodes();
	int length = childNodes.getLength();
	for (int i = 0; i < length; i++) {
	    org.w3c.dom.Node childNode = childNodes.item(i);
	    constructServiceObject(childNode);
	}	

	// post-processing (populates data types)
	if (name.equals("ServiceJob")) {
	    // add all the tasks
	    for (Enumeration enum = allTasks.elements(); enum.hasMoreElements() ;) {
		job.addTask((ServiceTask)enum.nextElement());
	    }
	    allTasks.clear();
	} else if (name.equals("ServiceTask")) {
	    // add all the task contexts
	    for (Enumeration enum = allTaskContexts.elements(); enum.hasMoreElements() ;) {
		task.addContext((ServiceContext)enum.nextElement());
	    }
	    allTasks.add(task);
	    allTaskContexts.clear();
	    task = null;
	} else if (name.equals("ServiceContext")) {
	    // add all data nodes
	    String contextName = attrsTable.getProperty("name");
	    Enumeration enumKeys = allDataNodes.keys();
	    Enumeration enumValues = allDataNodes.elements();
	    while (enumKeys.hasMoreElements()) {
		context.putValue((String)enumKeys.nextElement(),
				 (DataNode)enumValues.nextElement());
	    }
	    allDataNodes.clear();
	    // add all text nodes
	    enumKeys = allTextNodes.keys();
	    enumValues = allTextNodes.elements();
	    while (enumKeys.hasMoreElements()) {
		String nodeKey = (String)enumKeys.nextElement();
		String nodeValue = (String)enumValues.nextElement();
		// only SORCER.NO_WAIT needs a Boolean value
		if (nodeKey.equals(SORCERg.NO_WAIT)) {
		    context.put(nodeKey, new Boolean(nodeValue));
		} else 
		    context.put(nodeKey, nodeValue);
		/*
		System.err.println("kk = " + kk);
		System.err.println("vv = " + vv);
		*/
	    }
	    allTextNodes.clear();
	    // put this context
	    allContexts.put(contextName, context);
	    context = null;
	    // debug
	    Enumeration enumContextKeys = allContexts.keys();
	    System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	    while (enumContextKeys.hasMoreElements()) {
		System.err.println((ServiceContext)allContexts.get((String)enumContextKeys.nextElement()));
	    }

	} else if (name.equals("DataNode")) {
	    String path = attrsTable.getProperty("path");
	    dataNode.setName(text);
	    allDataNodes.put(path, dataNode);
	    dataNode = null;
	    text = null;
	} else if (name.equals("TextNode")) {
	    String path = attrsTable.getProperty("path");
	    allTextNodes.put(path, text);
	    text = null;
	} else if (name.equals("TaskContext")) {
	    if (text.length() == 0) {
		allTaskContexts.add(taskContext);
	    } else {
		allTaskContexts.add(taskContext.getSubcontext(text));
	    }
	    taskContext = null;
	}
	    
    }



    
    public static void main(String args[]) {
	if (args.length != 2) {
	    String program = "java ServiceJobXMLDomParser";
	    String arguments1 = " -f <xml_file>";
	    String arguments2 = " -s <xml_string>";
	    System.err.println("Usage: " + program + arguments1);
	    System.err.println("or");
	    System.err.println("Usage: " + program + arguments2);
	    return;
	}

	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	
	try {
	    // parse xml
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    if (args[0].equals("-f"))
		document = builder.parse(new File(args[1]));
	    else if (args[0].equals("-s"))
		document = builder.parse(new ByteArrayInputStream(args[1].getBytes()));
	    else
		throw new IOException("Invalid flag: " + args[0] + " for the parser!!");

	    // print
	    XmlDocument xdoc = (XmlDocument)document;
	    xdoc.write(System.out);

	    // traverse and print
	    ServiceJobXMLDomParser p = new ServiceJobXMLDomParser();
	    p.traverse(document);

	    // construct a job
	    ServiceJob job = p.constructServiceJob(document);
	    System.out.println("Job: \n" + job);

	    // print job
	    Enumeration e = job.elements();
	    while (e.hasMoreElements()) {
		ServiceTask task = (ServiceTask)e.nextElement();
		System.out.println("task: " + task);
		System.out.println("  taskID: " + task.taskID);
		System.out.println("  name: " + task.name);
		System.out.println("  description: " + task.description);
		System.out.println("  status: " + task.status);
		System.out.println("  priority: " + task.priority);
		System.out.println("  method: " + task.method);
		System.out.println("  contexts: " + jgapp.util.Util.arrayToString(task.contexts));
	    }
		

        } catch (SAXParseException spe) {
           // Error generated by the parser
           System.out.println ("\n** Parsing error" 
              + ", line " + spe.getLineNumber ()
              + ", uri " + spe.getSystemId ());
           System.out.println("   " + spe.getMessage() );

           // Use the contained exception, if any
           Exception  x = spe;
           if (spe.getException() != null)
               x = spe.getException();
           x.printStackTrace();

        } catch (SAXException sxe) {
           // Error generated by this application
           // (or a parser-initialization error)
           Exception  x = sxe;
           if (sxe.getException() != null)
               x = sxe.getException();
           x.printStackTrace();

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();

        } catch (IOException ioe) {
           // I/O error
           ioe.printStackTrace();
        }

    }
}
