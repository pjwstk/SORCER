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
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.util.Properties;
import java.util.StringTokenizer;

import sorcer.service.*;

public class ServiceJobXMLSAXParser extends HandlerBase {
    

    //===========================================================
    // SAX DocumentHandler methods
    //===========================================================

    public void startDocument() {
	//System.err.println(">>> Beginning of the document ...");
    }

    public void endDocument() {
	//System.err.println(">>> The end of the document ...");
    }
   
    public void startElement (String name, AttributeList attrs)
	throws SAXException
    {
	// echo the element information
        System.out.println("ELEMENT: " + name);
	Properties props = new Properties();
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
		props.setProperty(attrs.getName(i), attrs.getValue(i));
		System.out.println("  ATTR: " + 
				   attrs.getName(i) + "=" +
				   attrs.getValue(i));
	    }
	}

    }

    public void characters(char buf[], int offset, int len) {
	String s = new String(buf, offset, len);
	/*
	System.out.println("  ==>" + buf + "<==");
	System.out.println("  ==> offset = " + offset);
	System.out.println("  ==> len = " + len);
	*/
	System.out.println("  ==>" + s + "<==");
    }

    public void endElement(String name) {
	System.err.println(">>> The end of the element: " + name + " ...");
    }

    //===========================================================
    // SAX ErrorHandler methods
    //===========================================================

    // treat validation errors as fatal
    public void error (SAXParseException e)
	throws SAXParseException
    {
        System.out.println("***** Error:");
	System.out.println("at " + e.getSystemId());
	System.out.println("   line " + e.getLineNumber());
	System.out.println("   column " + e.getColumnNumber());
        System.out.println("   ==> " + e.getMessage ());
    }

    // dump warnings too
    public void warning (SAXParseException e)
	throws SAXParseException
    {
        System.out.println("***** Warning:");
	System.out.println("at " + e.getSystemId());
	System.out.println("   line " + e.getLineNumber());
	System.out.println("   column " + e.getColumnNumber());
        System.out.println("   ==> " + e.getMessage ());
    }


    //===========================================================
    // main
    //===========================================================

    public static void main(String args[]) {
	if (args.length != 1) {
	    System.err.println("Usage: java ServiceJobXMLSAXParser <xmlfile|URL>");
	    System.exit(1);
	}

	// get parser factory
	SAXParserFactory parserFactory = SAXParserFactory.initInstance();
	parserFactory.setValidating(true);
	//parserFactory.setValidating(false);


	// get the parser and parse the file
	try {
	    // get new parser with validation on
	    SAXParser parser = parserFactory.newSAXParser();
	    
	    // parse the URL or file
	    if (args[0].startsWith("http://")) {	// URL
		//parser.parse(args[0], new HandlerBase());
		parser.parse(args[0], new ServiceJobXMLSAXParser());
	    } else {
		//parser.parse(new File(args[0]), new HandlerBase());
		parser.parse(new File(args[0]), new ServiceJobXMLSAXParser());
	    }
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	    e.printStackTrace();
	}
    }
}
