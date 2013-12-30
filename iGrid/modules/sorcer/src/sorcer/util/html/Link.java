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

package sorcer.util.html;

public class Link extends Component {

	public Link(String url, String text) {
		this.url = url;
		content = new Text(text);
	}

	public Link(String url, Component c) {
		this.url = url;
		content = c;
	}

	public Link(Anchor a) {
		anchor = a;
		content = a.getContent();
	}

	public Link(Anchor a, String text) {
		anchor = a;
		content = new Text(text);
	}

	public Link(Anchor a, Component c) {
		anchor = a;
		content = c;
	}

	public void print(java.io.PrintWriter pw) {
		pw.print("<a href=\"");
		if (anchor == null)
			pw.print(url);
		else
			pw.print("#" + anchor.getName());
		pw.print("\">");
		if (content != null)
			content.print(pw);
		pw.println("</a>");
	}

	private Anchor anchor = null;
	private String url = null; // The URL
	private Component content = null; // The link content, normally text
}
