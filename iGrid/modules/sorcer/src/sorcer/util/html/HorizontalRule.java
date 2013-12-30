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

public class HorizontalRule extends Component {

	public String toString() {
		return new String("<hr>\n");
	}

	public void print(java.io.PrintWriter pw) {
		pw.println("<hr");

		if (size != 0) {
			pw.print(" size=" + size);
		}

		if (width != 0) {
			pw.print(" width=" + width);
		}

		if (source != null)
			pw.print(" src=\"" + source + "\"");

		pw.print(">");
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setSource(String source) {
		this.source = source;
	}

	private String source = null;
	private int width = 0;
	private int size = 0;
}
