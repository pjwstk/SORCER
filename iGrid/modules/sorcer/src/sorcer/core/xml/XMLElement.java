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

import java.util.Hashtable;

public class XMLElement {
	private String elementName = new String();
	private String prefix = new String();
	private String uri = new String();
	private Object data = new Object();
	private Hashtable attributes = new Hashtable();

	public XMLElement() {
		elementName = " ";
	}

	public XMLElement(String elementName) {
		this.elementName = elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getElementName() {
		return elementName;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void setURI(String uri) {
		this.uri = uri;
	}

	public String getURI() {
		return uri;
	}

	public void setAttribute(String key, String value) {
		if ((key != null) && (value != null))
			attributes.put(key, value);
	}

	public String getAttribute(String key) {
		if (key != null)
			return (String) attributes.get(key);

		return null;
	}

}
