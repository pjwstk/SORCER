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

public class Container extends Component {

	public Container() {
	}

	public Container(Component c) {
		components.addElement(c);
	}

	public void add(Component c) {
		components.addElement(c);
	}

	public void addString(String text) {
		components.addElement(new Text(text, true));
	}

	public void add(String text) {
		components.addElement(new Text(text));
	}

	public boolean isContainer() {
		return true;
	}

	public void print(java.io.PrintWriter pw) {
		int n;
		// Util.debug(this,"The size of the components is :"+components.size());
		for (n = 0; n < components.size(); n++) {
			Component c = (Component) components.elementAt(n);
			c.print(pw);
		}
	}

	public int getComponentCount() {
		return components.size();
	}

	private java.util.Vector components = new java.util.Vector(100);
}
