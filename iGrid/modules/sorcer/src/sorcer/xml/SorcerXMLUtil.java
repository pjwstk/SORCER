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

import java.io.*;
import java.util.*;
import java.rmi.server.UID;

import sorcer.core.*;
import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.ServiceExertion;
import sorcer.core.signature.NetSignature;
import sorcer.service.*;
import jgapp.persist.ObjectLogger;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.FactoryConfigurationError;  
import javax.xml.parsers.ParserConfigurationException;
 
import org.xml.sax.SAXException;  
import org.xml.sax.SAXParseException;  
import org.w3c.dom.*;

// For write operation
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;  
import javax.xml.transform.stream.StreamResult; 


public class SorcerXMLUtil {

    // **********************************************************************
    //
    // Convert Sorcer data structures to XML 
    //
    // **********************************************************************
    
    public static String toXML(Job job) 
    throws SorcerXMLException {
	try {
	    // build a DOM
	    DocumentBuilderFactory factory =
		DocumentBuilderFactory.initInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document doc = builder.newDocument();
	    buildDOM(doc, doc, job);
	
	    // convert it to XML string
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
            TransformerFactory tFactory =
                TransformerFactory.initInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(os);
	    //transformer.setOutputProperty("encoding", "UTF-16");
	    transformer.setOutputProperty("indent", "yes");
            transformer.transform(source, result);
	    return os.toString();	
	} catch (Exception e) {
	    throw new SorcerXMLException("Error in converting the job to XML",
					e);
	}
    }

    public static String toXML(ServiceExertion task) 
    throws SorcerXMLException {
	try {
	    // build a DOM
	    DocumentBuilderFactory factory =
		DocumentBuilderFactory.initInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document doc = builder.newDocument();
	    buildDOM(doc, doc, task);
	
	    // convert it to XML string
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
            TransformerFactory tFactory =
                TransformerFactory.initInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(os);
	    //transformer.setOutputProperty("encoding", "UTF-16");
	    transformer.setOutputProperty("indent", "yes");
            transformer.transform(source, result);
	    return os.toString();	
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new SorcerXMLException("Error in converting the task to XML",
					e);
	}
    }

    public static String toXML(Context context) 
    throws SorcerXMLException {
	try {
	    // build a DOM
	    DocumentBuilderFactory factory =
		DocumentBuilderFactory.initInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document doc = builder.newDocument();
	    buildDOM(doc, doc, context);
	
	    // convert it to XML string
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
            TransformerFactory tFactory =
                TransformerFactory.initInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(os);
	    //transformer.setOutputProperty("encoding", "UTF-16");
	    transformer.setOutputProperty("indent", "yes");
            transformer.transform(source, result);
	    return os.toString();	
	} catch (Exception e) {
	    throw new 
		SorcerXMLException("Error in converting the context to XML",
				  e);
	}
    }

    
    protected static void buildDOM(Document doc, Node rootNode, Job job)
    throws SorcerXMLException {
	// job
	Element jobNode = doc.createElement("ServiceJob");
	rootNode.appendChild(jobNode);
	if (job == null) {
	    throw new SorcerXMLException("The input job is null!");
	}
	try {
	    jobNode.setAttribute("name",
				 (job.getName()==null)?"null":job.getName());
	    jobNode.setAttribute("description",
				 (job.getDescription()==null)?"null":job.getDescription());
	    jobNode.setAttribute("domainSeqId",
				 (job.getDomainId()==null)?"null":job.getDomainId());
	    jobNode.setAttribute("subDomainSeqId",
				 (job.getSubdomainId()==null)?"null":
				 job.getSubdomainId());
	    jobNode.setAttribute("mode",
				 Integer.toString(job.getMode()));
	    jobNode.setAttribute("scopeCode",
				 Integer.toString(job.getScopeCode()));
	    jobNode.setAttribute("status",
				 String.valueOf(job.getStatus()));
	    jobNode.setAttribute("jobID",
				 (job.getId()==null)?"null":job.getId());
	    jobNode.setAttribute("strategy",
				 (job.getContext().getFlowType()==null)?"":
				 job.getContext().getFlowType());
	    jobNode.setAttribute("access",
				 (job.getContext().getAccessType()==null)?"":
				 job.getContext().getAccessType());
	    jobNode.setAttribute("wait",
				 String.valueOf(!job.getContext().isMonitorEnabled()));
	    jobNode.setAttribute("notify",
				 (job.getContext().getNotifyList()==null)?"":
				 job.getContext().getNotifyList());
	    
	    // tasks
	    for (int i = 0; i < job.size(); i++) {
		buildDOM(doc, jobNode, (ServiceExertion)job.exertionAt(i));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new SorcerXMLException(e.getMessage());
	}
    }


    protected static void buildDOM(Document doc, Node rootNode, ServiceExertion task)
    throws SorcerXMLException {
	// task
	Element taskNode = doc.createElement("ServiceTask");
	rootNode.appendChild(taskNode);
	NetSignature method = (NetSignature)task.getProcessSignature();
	if (//task.getName() == null ||
	    //task.getDescription() == null ||
	    method == null
	    //task.method.getName() == null ||
	    //task.method.serviceType == null ||
	    //task.method.providerName == null ||
	    //task.taskID == null
	    ) {
	    throw new 
		SorcerXMLException("Could not build XML from the given "
				  + "ServiceTask. At least one of the "
				  + "fields is not defined!\n"
				  + "Task is :" + task);
	}
	taskNode.setAttribute("name", 
			      (task.getName()==null)?"null":
			      task.getName());
	taskNode.setAttribute("description",
			      (task.getDescription()==null)?"null":
			      task.getDescription());
	taskNode.setAttribute("method", 
			      (method.name==null)?"null":
			      method.getName());
	taskNode.setAttribute("serviceType",
			      (method.serviceType==null)?"null":
			      method.serviceType);
	taskNode.setAttribute("provider", 
			      (method.providerName==null)?"null":
			      method.providerName);
	taskNode.setAttribute("taskID",
			      (task.getId()==null)?"null":
			      task.getId());
	taskNode.setAttribute("domainSeqId",
			      (task.getDomainId()==null)?"null":
			      task.getDomainId());
	taskNode.setAttribute("subDomainSeqId",
			      (task.getSubdomainId()==null)?"null":
			      task.getSubdomainId());
	taskNode.setAttribute("status",
			      Integer.toString(task.getStatus()));
	// this field "condition" is no longer  there
	/*
	taskNode.setAttribute("condition",
			      (task.condition==null)?"null":
			      task.condition.toString());
	*/
	taskNode.setAttribute("priority",
			      Integer.toString(task.getPriority()));
	taskNode.setAttribute("mode",
			      Integer.toString(task.getMode()));
	taskNode.setAttribute("scopeCode",
			      Integer.toString(task.getScopeCode()));
	taskNode.setAttribute("ownerID", 
			      (task.getOwnerId()==null)?"null":
			      task.getOwnerId());
	taskNode.setAttribute("parentID", 
			      (task.getParentId()==null)?"null":
			      task.getParentId());
	taskNode.setAttribute("portalURL",
			      (method.getPortalURL()==null)?"null":
			      method.getPortalURL());
	/*
	taskNode.setAttribute("runtimeSID",
			      (task.runtimeSessionID==null)?"-1":
			      task.runtimeSessionID);
	*/
	// ignore it for now
	taskNode.setAttribute("runtimeSID", "1");
	
	// contexts
	if (task.getContext() != null)
	    buildDOM(doc, taskNode, task.getContext());
    }
    
    
    protected static void buildDOM(Document doc, Node rootNode,
				   Context context) 
    throws SorcerXMLException {
	/*
	  // context
	  Element contextNode = doc.createElement("ServiceContext");
	  rootNode.appendChild(contextNode);
	  contextNode.setAttribute("name", context.getName());
	  // data nodes
	  Enumeration enum = ((Hashtable)context).keys();
	  while (enum.hasMoreElements()) {
	  Object keyObj = enum.nextElement();
	  Object valueObj = context.getValue((String)keyObj);
	  if (keyObj instanceof String) {
	  String path = (String)keyObj;
	  String valueType = valueObj.getClass().getName();
	  String valueTypeAbbreviation = // class name w/o package names
	  valueType.substring(valueType.lastIndexOf('.')+1); 
	  
	  // get all meta attributes
	  //!!!!!!!!!!!!!Redo with current relevance to Linked context. 
	  Enumeration attrEnum = context.getMetaAssociations(path);
	  StringBuffer metaAttrs = new StringBuffer();
	  while (attrEnum.hasMoreElements()) {
	  metaAttrs.append((String)attrEnum.nextElement());
	  if (attrEnum.hasMoreElements()) {
	  metaAttrs.append(";"); // separator for the meta attrs
	  }
	  }
	  
	  // construct XML node for this path
	  Element node = doc.createElement("TextNode");
	  contextNode.appendChild(node);
	  node.setAttribute("path", path);
	  node.setAttribute("valueType", valueTypeAbbreviation);
	  if (metaAttrs.length() >  0)
	  node.setAttribute("meta-attrs", metaAttrs.toString());
	  
	  // the value should not be parsed!!
	  node.appendChild(doc.createCDATASection(valueObj.toString()));
	  //node.appendChild(doc.createTextNode((String)valueObj));
	  }
	  }*/
    }



    // **********************************************************************
    //
    // Convert XML to Sorcer data structures
    //
    // **********************************************************************

    public static Job xmlToServiceJob(String xml)
	throws SorcerXMLException {

	// create DOM Document
	DocumentBuilderFactory factory = DocumentBuilderFactory.initInstance();
	DocumentBuilder builder = null;
	try {
	    builder = factory.newDocumentBuilder();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	    throw new SorcerXMLException(e.getMessage());
	}
	Document doc = null;
	try {
	    doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
	} catch (SAXException se) {
	    se.printStackTrace();
	    throw new SorcerXMLException(se.getMessage());
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    throw new SorcerXMLException(ioe.getMessage());
	}

	// validate it
	if (!doc.hasChildNodes() ||
	    doc.getChildNodes().getLength() != 1 ||
	    doc.getChildNodes().item(0).getNodeName() != "ServiceJob") {
	    throw new SorcerXMLException("Could not create a ServiceJob object "+
					"from the passed xml string. \n" +
					"The root node of the xml string " +
					"is not \"ServiceJob\"");
	}

	// construct a ServiceJob
	Element jobNode = (Element)doc.getChildNodes().item(0);
	return domToServiceJob(jobNode);
    }


    protected static Job domToServiceJob(Element jobNode) 
	throws SorcerXMLException {

	// define job 
	String name = jobNode.getAttribute("name");
	String desc = jobNode.getAttribute("description");
	String jobID = jobNode.getAttribute("jobID");
	String strategy = jobNode.getAttribute("strategy").toLowerCase();
	String access = jobNode.getAttribute("access").toLowerCase();
	String wait = jobNode.getAttribute("wait");
	String notify = jobNode.getAttribute("notify");
	String domainSeqId = jobNode.getAttribute("domainSeqId");
	String subDomainSeqId = jobNode.getAttribute("subDomainSeqId");
	String status = jobNode.getAttribute("status");
	
	Job job = new Job();
	job.setName(name);
	try {
	    job.setDescription(desc);
	    job.getContext().setFlowType((strategy.length()==0)?"sequential":strategy);
	    job.getContext().setAccessType((access.length()==0)?"catalog":access);
	    job.getContext().isMonitorEnabled((wait.equals("true"))?false:true);
	    job.getContext().setNotifyList(notify);
	    if (domainSeqId != null || domainSeqId.length() > 0)
		job.setDomainId(domainSeqId);
	    if (subDomainSeqId != null || subDomainSeqId.length() > 0)
		job.setSubdomainId(subDomainSeqId);
	    if (status != null || status.length() > 0)
		job.setStatus (Integer.parseInt(status));
	    if (jobID == null || jobID.length() == 0 || jobID.equals("null")) {
		job.setId(new UID().toString());
	    } else {	    
		job.setId(jobID);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new SorcerXMLException(e.getMessage());
	}
	    
	// define tasks in the job
	NodeList list = jobNode.getChildNodes();
	Element[] elements = new Element[list.getLength()];
	int count = 0;
	for (int i = 0; i < list.getLength(); i++) {
	    // get type:Element and ignore all the other types
	    if (list.item(i) instanceof Element) {
		//System.err.println("=> Got an element type");
		elements[count] = (Element)list.item(i);
		count++;
	    }
	}
	ServiceExertion[] tasks = new ServiceExertion[count];
	for (int i = 0; i < count; i++)
	    job.addExertion(domToServiceTask(elements[i]));
	return job;
    }




    public static ServiceExertion xmlToServiceTask(String xml)
	throws SorcerXMLException {

	// create DOM Document
	DocumentBuilderFactory factory = DocumentBuilderFactory.initInstance();
	DocumentBuilder builder = null;
	try {
	    builder = factory.newDocumentBuilder();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	    throw new SorcerXMLException(e.getMessage());
	}
	Document doc = null;
	try {
	    doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
	} catch (SAXException se) {
	    se.printStackTrace();
	    throw new SorcerXMLException(se.getMessage());
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    throw new SorcerXMLException(ioe.getMessage());
	}

	// validate it
	if (!doc.hasChildNodes() ||
	    doc.getChildNodes().getLength() != 1 ||
	    doc.getChildNodes().item(0).getNodeName() != "ServiceTask") {
	    throw new SorcerXMLException("Could not create a ServiceTask object "+
					"from the passed xml string. \n" +
					"The root node of the xml string " +
					"is not \"ServiceTask\"");
	}

	// construct a ServiceTask
	Element taskNode = (Element)doc.getChildNodes().item(0);
	return domToServiceTask(taskNode);
    }


    protected static ServiceExertion domToServiceTask(Element taskNode) 
	throws SorcerXMLException {

	// define task 
	String name = taskNode.getAttribute("name");
	String desc = taskNode.getAttribute("description");
	String methodName = taskNode.getAttribute("method");
	String serviceType = taskNode.getAttribute("serviceType");
	String provider = taskNode.getAttribute("provider");
	String taskID = taskNode.getAttribute("taskID");
	String portalURL = taskNode.getAttribute("portalURL");
	String runtimeSessionID = taskNode.getAttribute("runtimeSID");

	NetSignature method = new NetSignature(methodName, serviceType, provider);
	if (portalURL != null)
	    method.setPortalURL(portalURL);

	ServiceExertion task = new ServiceExertion(name, desc, 
				       new NetSignature[]{method});
	task.setConditionalContext(new ServiceContext()); // no contexts yet
	task.setId(taskID);
	task.setProviderName(provider);
	task.setServiceType(serviceType);
	if (runtimeSessionID != null) 
	    task.setSessionId(runtimeSessionID);
	else
	    task.setSessionId("-1");
	
	
	// define contexts in the task
	NodeList list = taskNode.getChildNodes();
	Element[] elements = new Element[list.getLength()];
	int count = 0;
	for (int i = 0; i < list.getLength(); i++) {
	    //System.err.println("task: Got a " + list.item(i).getClass().getName());
	    //System.err.println("      type: " + list.item(i).getNodeType());
	    //System.err.println("      name: " + list.item(i).getNodeName());
	    //System.err.println("      value:" + list.item(i).getNodeValue());

	    // get type:Element and ignore all the other types
	    if (list.item(i) instanceof Element) {
		//System.err.println("=> Got an element type");
		elements[count] = (Element)list.item(i);
		count++;
	    }
	}
	Context[] contexts = new Context[count];
	for (int i = 0; i < count; i++)
	    contexts[i] = domToServiceContext(elements[i]);
	task.setConditionalContext(contexts[0]);
	return task;
    }

    protected static Context domToServiceContext(Element contextNode)
	throws SorcerXMLException {

	// define context
	String name = contextNode.getAttribute("name");
	String root = contextNode.getAttribute("root");
	Context context = new ServiceContext(name);
	context.setRootName(root);
	
	// define path/values
	NodeList list = contextNode.getChildNodes();
	int size = list.getLength();
	for (int i = 0; i < size; i++) {
	    Node node = list.item(i);
	    /*
	    System.err.println("node (" + node.getNodeType() + ") = " +
			       node.getNodeName() + " ==> " +
			       node.getNodeValue());
	    */
	    // check if it's a "TextNode" element
	    if (node.getNodeType() == Node.ELEMENT_NODE &&
		node.getNodeName() == "TextNode") { // a "TextNode" element
		String path = ((Element)node).getAttribute("path");
		String valueType = ((Element)node).getAttribute("valueType");
		String metaAttrs = ((Element)node).getAttribute("meta-attrs");
		String cdataValue = null;
		String textValue = null;
		NodeList list2 = node.getChildNodes();
		for (int j = 0; j < list2.getLength(); j++) {
		    Node childNode = list2.item(j);
		    switch (childNode.getNodeType()) {
		    case Node.TEXT_NODE: // a text field
			//System.err.println("got a text!");
			textValue = list2.item(j).getNodeValue().trim();
			break;
		    case Node.CDATA_SECTION_NODE: // a CDATA 
			//System.err.println("got a cdata!");
			cdataValue = list2.item(j).getNodeValue().trim();
			break;
		    }
		}
		// put path/value to context
		try {
		    if (cdataValue != null)
			context.putValue(path, cdataValue);
		    else if (textValue != null)
			context.putValue(path, textValue);
		    else
			context.putValue(path, ""); // empty string

		}catch(ContextException ce) {
		    ce.printStackTrace();
		    throw new SorcerXMLException("ContextException caught:"+ce.getMessage());
		}

		// assign meta attributes
		try {
		    if (metaAttrs != null && metaAttrs.length() > 0) {
			StringTokenizer token = new StringTokenizer(metaAttrs, ";");
			System.err.println("======================");
			System.err.println("Before adding attr: " + 
					   (context).toString());
			while (token.hasMoreTokens()) {
			    String attr = token.nextToken();
			    context.addAttributeValue(path, attr);
			    System.err.println("After adding attr: (" + 
					       attr + ")" +
					       (context).toString());
			}
		    } 
		} catch (ContextException ce) {
		    ce.printStackTrace();
		    throw new SorcerXMLException(ce.getMessage());
		}
	    }
	}


	// define inpath(s)
	for (int i = 0; i < size; i++) {
	    Node node = list.item(i);
	    // check if it's a "InNode" element
	    if (node.getNodeType() == Node.ELEMENT_NODE &&
		node.getNodeName() == "InNode") {
		String path = ((Element)node).getAttribute("path");
		String from = ((Element)node).getAttribute("from");
		try {
		    context.putValue(path,
				     Context.EMPTY_LEAF, 
				     from);
		    System.err.println("*** putting inpath=" + path +
				       " from outpath=" + from);
		} catch (ContextException e) {
		    e.printStackTrace();
		    throw new SorcerXMLException(e.getMessage());
		}
	    }
	}

	//System.err.println("context = " + ((HashtableContext)context).toString());
	return context;
    }
	


    public static void main(String[] args) {
	if (args.length != 2) {
	    System.err.println("Usage: java sorcer.service.xml.SorcerXMLUtil" +
			       " <options>\n" +
			       "<options>:  -tobj <xml_file> " +
			       "(converting xml string to ServiceTask)\n" +
			       "<optiosn>:  -txml <serialzed_task> " +
			       "(writing out xml string for the input task)\n" +
			       "<options>:  -jobj <xml_file>" +
			       "(converting xml string to ServiceJob)\n" +
			       "<optiosn>:  -jxml <serialzed_job> " +
			       "(writing out xml string for the input job)\n");
	    return;
	}

	if (args[0].equals("-tobj")) {
	    try {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(args[1]));
		String temp;
		while ((temp = reader.readLine()) != null)
		    buffer.append(temp);
		ServiceExertion task = SorcerXMLUtil.xmlToServiceTask(buffer.toString());
		System.out.println("task = \n" + task);
		ObjectLogger.persist(args[1]+".task", task);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	} else if (args[0].equals("-txml")) {
	    try {
		ServiceExertion task = (ServiceExertion)ObjectLogger.restore(args[1]);
		String xml = SorcerXMLUtil.toXML(task);
		System.err.println("task = \n" + xml);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	} else if (args[0].equals("-jobj")) {
	    try {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(args[1]));
		String temp;
		while ((temp = reader.readLine()) != null)
		    buffer.append(temp);
		Job job = SorcerXMLUtil.xmlToServiceJob(buffer.toString());
		System.out.println("job = \n" + job);
		ObjectLogger.persist(args[1]+".job", job);
		int count = 0;
		for (int i=0; i<job.size(); i++) {
		    ServiceExertion task = (ServiceExertion)job.exertionAt(i);
		    System.out.println("=============== task #" + count + "=======");
		    System.out.println(task);
		    count++;
		}

	    } catch (Exception e) {
		e.printStackTrace();
	    }
	} else if (args[0].equals("-jxml")) {
	    try {
		Job job = (Job)ObjectLogger.restore(args[1]);
		String xml = SorcerXMLUtil.toXML(job);
		System.err.println("job = \n" + xml);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

}
