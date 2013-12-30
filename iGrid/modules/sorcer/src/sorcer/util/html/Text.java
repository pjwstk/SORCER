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

public class Text extends Component {
	boolean isPlain = false;

	public Text(String text, boolean isPlain) {
		if (text != null)
			this.text = new String(text);
		this.isPlain = isPlain;
	}

	public Text(String text) {
		this(text, false);
	}

	public void print(java.io.PrintWriter pw) {
		if (text != null && !isPlain) {
			int length = text.length();
			for (int n = 0; n < length; n++) {
				char c;
				switch ((c = text.charAt(n))) {
				/*
				 * case '\u00E4': pw.print("&auml;"); break; case '\u00C4':
				 * pw.print("&Auml;"); break; case '\u00F6': pw.print("&ouml;");
				 * break; case '\u00D6': pw.print("&Ouml;"); break; case
				 * '\u00FC': pw.print("&uuml;"); break; case '\u00DC':
				 * pw.print("&Uuml;"); break;
				 */
				case '<':
					pw.print("&lt;");
					break;
				case '>':
					pw.print("&gt;");
					break;
				case '&':
					pw.print("&amp;");
					break;
				case '"':
					pw.print("&quot;");
					break;
				default:
					pw.print(c);
				}
			}
		} else {
			pw.print(text);
		}
	}

	public String getText() {
		if (text != null)
			return new String(text);
		else
			return null;
	}

	public void setText(String text) {
		if (text != null)
			this.text = new String(text);
	}

	private String text = null;
}
