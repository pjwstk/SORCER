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

public class XMLFile {
	private StringBuffer virtualFile = null;
	private static int index = 0;

	public XMLFile() {
		virtualFile = new StringBuffer();
		virtualFile
				.append("<?xml version='1.0' encoding='ISO-8859-1' standalone='yes'?>");
	}

	public void setExertion(String type) {
		if (virtualFile == null)
			return;

		virtualFile.append("\n<Exertion type=\"" + type + "\">\n");
	}

	public void setInValue(String value) {
		if (virtualFile == null)
			return;

		virtualFile.append("<in-value" + index + ">");
		virtualFile.append(value);
		virtualFile.append("</in-value" + index + ">\n");
		index++;
	}

	public void setMethod(String method) {
		if (virtualFile == null)
			return;

		virtualFile.append("<method>");
		virtualFile.append(method);
		virtualFile.append("</method>\n");
	}

	public void setProviderName(String providerName) {
		if (virtualFile == null)
			return;

		virtualFile.append("<providerName>");
		virtualFile.append(providerName);
		virtualFile.append("</providerName>\n");
	}

	public void setServiceType(String serviceType) {
		if (virtualFile == null)
			return;

		virtualFile.append("<serviceType>");
		virtualFile.append(serviceType);
		virtualFile.append("</serviceType>\n");
	}

	public void startContext() {
		if (virtualFile == null)
			return;

		virtualFile.append("<Context>");
	}

	public void endContext() {
		if (virtualFile == null)
			return;

		virtualFile.append("</Context>\n");
	}

	public void endExertion() {
		if (virtualFile == null)
			return;

		virtualFile.append("</Exertion>");
	}

	public StringBuffer getFile() {
		return virtualFile;
	}
}
