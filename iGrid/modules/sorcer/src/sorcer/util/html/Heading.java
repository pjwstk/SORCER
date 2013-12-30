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

public class Heading extends Component implements HorizontalAligned {
	public Heading(int level, String text) {
		if (level < 1)
			level = 1;
		else if (level > 6)
			level = 6;
		this.level = level;
		this.text = new Text(text);
	}

	public Heading(int level, int alignment, String text) {
		this(level, text);
		setHorizontalAlignment(alignment);
	}

	public void print(java.io.PrintWriter pw) {
		pw.print("<h" + level);
		if (alignment != UNALIGNED) {
			pw.print(" align=");
			switch (alignment) {
			case LEFT:
				pw.print("left");
				break;
			case CENTER:
				pw.print("center");
				break;
			case RIGHT:
				pw.print("right");
				break;
			}
		}

		pw.print(">");

		text.print(pw);

		pw.println("</h" + level + ">");
	}

	public void setHorizontalAlignment(int n) {
		if (n == LEFT || n == CENTER || n == RIGHT)
			alignment = n;
		else
			alignment = UNALIGNED;
	}

	public int getHorizontalAlignment() {
		return alignment;
	}

	private int level;
	private Text text;
	private int alignment = UNALIGNED;
}
