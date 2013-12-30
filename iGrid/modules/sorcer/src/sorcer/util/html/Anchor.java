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

public class Anchor extends Component {

	public Anchor(String text) {
		name = String.valueOf(anchorNumber++);
		content = new Text(text);
	}

	public Anchor(Component c) {
		name = String.valueOf(anchorNumber++);
		content = c;
	}

	public Anchor(String name, Component c) {
		this.name = new String(name);
		content = c;
	}

	public Anchor(String name, String text) {
		this.name = new String(name);
		content = new Text(text);
	}

	public void print(java.io.PrintWriter pw) {
		pw.print("<a name=\"" + name + "\">");
		if (content != null)
			content.print(pw);
		pw.println("</a>");
	}

	public String getName() {
		return name;
	}

	public Component getContent() {
		return content;
	}

	private String name = null;
	private Component content = null;
	private int thisAnchorNumber = 0;
	private static int anchorNumber = 1;

}
