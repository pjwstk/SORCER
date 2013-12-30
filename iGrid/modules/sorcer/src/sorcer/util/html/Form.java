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

public class Form extends Container {

	public Form(String action, String httpMethod) {
		this(action, httpMethod, null, null);
	}

	public Form(String action, String httpMethod, String name) {
		this(action, httpMethod, name, null);
	}

	public Form(String action, String httpMethod, String name, String onsubmit) {
		this.action = action;
		this.method = httpMethod;
		this.name = name;
		this.onsubmit = onsubmit;
	}

	public void print(java.io.PrintWriter pw) {
		pw.println("<form action=\"" + action + "\" method=" + method);

		if (name != null)
			pw.print(" name=\"" + name + "\"");

		if (onsubmit != null)
			pw.print(" onsubmit=\"" + onsubmit + "\"");

		pw.print(">");

		super.print(pw);
		pw.println("</form>");
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setMethod(String httpMethod) {
		method = httpMethod;
	}

	public void setOnsubmit(String onsubmit) {
		this.onsubmit = onsubmit;
	}

	private String action;
	// defaulf for forms is POST
	private String method = "POST";;
	private String name;
	private String onsubmit;
}
