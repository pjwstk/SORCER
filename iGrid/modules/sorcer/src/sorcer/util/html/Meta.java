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

public class Meta extends Component {

	/**
	 * Create new meta information
	 * 
	 * @param n
	 *            The meta information name
	 * @param c
	 *            The meta information content
	 */
	public Meta(String n, String c) {
		name = n;
		content = c;
	}

	/**
	 * Show the tag on an output stream
	 * 
	 * @param ps
	 *            The PrintWriter on which to print the tag
	 */
	public void print(java.io.PrintWriter pw) {
		pw.println("<meta name=\"" + name + "\" content=\"" + content + "\">");
	}

	private String name;
	private String content;
}
