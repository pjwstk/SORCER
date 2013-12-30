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

public class Area extends Container {

	public Area() {
	}

	public Area(Component c) {
		super(c);
	}

	public void print(java.io.PrintWriter pw) {
		pw.println("<" + tag + ">");
		super.print(pw);
		pw.println("</" + tag + ">");
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = new String(tag);
	}

	protected String tag;
}
