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

package sorcer.core.xml;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import java.io.FileReader;
import java.util.Vector;
import java.io.FileNotFoundException;

import java.lang.System;
import java.util.Hashtable;

public class StaxParser {
    /**** hashtable of elements
     **** key    = XMLElement i.e object containing elemntName and hashtable of attributes
     **** value  = text within the element
     **** Example:-  
     ****      if the element is  <name type="firstName">Bob</name>
     ****      key = XMLElement(name, type) and value = Bob
     ****/
    private Hashtable elements   = new Hashtable();

    private XMLStreamReader xmlr;
    private XMLElement element;
    private String elementName;
    private int previousEvent = 10;

    //Constructers
    public StaxParser() {
	// do nothing
    }

    public StaxParser(String filename) {
	System.setProperty("javax.xml.stream.XMLInputFactory",
			   "com.bea.xml.stream.MXParserFactory");

	try {
	    // Create an input factory
	    XMLInputFactory xmlif = XMLInputFactory.newInstance();
	    
	    // Create an XML stream reader
	    xmlr =
		xmlif.createXMLStreamReader(new FileReader(filename));
	} catch(FileNotFoundException fnfe) {
	    fnfe.printStackTrace();
	} catch(XMLStreamException xse) {
	    xse.printStackTrace();
	} 
    }
    
    public Hashtable parse() {
	try {
	    // Loop over XML input stream and process events
	    if( xmlr != null ) {
		while (xmlr.hasNext()) {
		    processEvent(xmlr);
		    xmlr.next();
		}

		return elements;
	    }
	} catch(XMLStreamException xse) {
	    xse.printStackTrace();
	    System.out.println(">>>>>Location = " + xse.getLocation());
	} 

	return null;
    }

    /**
     * Process a single event
     */
    private void processEvent(XMLStreamReader xmlr) {
	switch (xmlr.getEventType()) {
	case XMLStreamConstants.START_ELEMENT :
	    previousEvent = XMLStreamConstants.START_ELEMENT;
	    processName(xmlr);
            processAttributes(xmlr);
            break;
	case XMLStreamConstants.END_ELEMENT :
	    previousEvent = XMLStreamConstants.END_ELEMENT;
	    elements.put(elementName, element);
            break;
	case XMLStreamConstants.CHARACTERS :
            int start = xmlr.getTextStart();
            int length = xmlr.getTextLength();

	    if( previousEvent == XMLStreamConstants.START_ELEMENT ) {
		String text = new String(xmlr.getTextCharacters(), start, length);
		System.out.println(">>>ElementName = " + elementName + " text = " + text);
		element.setData(text);
	    }
            break;
	case XMLStreamConstants.COMMENT :
	case XMLStreamConstants.SPACE :
	case XMLStreamConstants.PROCESSING_INSTRUCTION :
            break;
	}
    }

    private void processName(XMLStreamReader xmlr) {
	if( xmlr == null )
	    return;
	
	if (xmlr.hasName()) {
	    elementName = xmlr.getLocalName();
	    element = new XMLElement(elementName);
	    element.setPrefix(xmlr.getPrefix());
	    element.setURI(xmlr.getNamespaceURI());
	    element.setElementName(elementName);
	}
    }

    private void processAttributes(XMLStreamReader xmlr) {
	if( xmlr == null )
	    return;

	for (int i = 0; i < xmlr.getAttributeCount(); i++)
         processAttribute(xmlr, i);
    }
    
    private void processAttribute(XMLStreamReader xmlr, int index) {
	if( xmlr == null )
	    return;
	
	element.setAttribute(xmlr.getAttributeName(index).toString(),xmlr.getAttributeValue(index));
    }
}
