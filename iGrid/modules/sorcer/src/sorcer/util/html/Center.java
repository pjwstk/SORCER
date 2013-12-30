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

public class Center extends Area {
	public Center(Component c) {
		super(c);
		tag = "center";
	}

	public Center() {
		tag = "center";
	}

	public Center(String str) {
		text = str;
		tag = "center";
	}

	/**
	 * Print centered component or print centered string. A printed string might
	 * be for example an applet markup.
	 */
	public void print(java.io.PrintWriter pw) {
		if (text == null)
			super.print(pw);
		else {
			pw.println("<" + tag + ">");
			pw.print(text);
			pw.print("</" + tag + ">");
		}
	}

	private String text;
}
