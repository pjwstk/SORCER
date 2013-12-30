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

public class ImageAction extends Container {

	public ImageAction(String action, String src, String txt) {
		this.action = action;
		this.imageSrc = src;
		this.altTxt = txt;
	}

	public void print(java.io.PrintWriter pw) {
		pw.println("<a href=\"" + action + "\" ><img src=\"" + imageSrc + "\"");

		if (altTxt != null)
			pw.print(" alt=\"" + altTxt + "\"");
		pw.print(" border=\"0\" align=\"center\"");
		pw.println(">");

		super.print(pw);
		pw.println("</a>");
	}

	private String action;
	private String imageSrc;
	private String altTxt;
}
