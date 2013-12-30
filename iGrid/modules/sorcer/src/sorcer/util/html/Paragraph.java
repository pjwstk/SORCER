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

public class Paragraph extends Component implements HorizontalAligned {

	public Paragraph() {
	}

	/**
	 * Create a new aligned paragraph
	 * 
	 * @param alignment
	 *            the horizontal alignment
	 * @see HorizontalAligned
	 */
	public Paragraph(int alignment) {
		setHorizontalAlignment(alignment);
	}

	public void print(java.io.PrintWriter pw) {
		pw.print("<p");
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
		pw.println(">");
	}

	public void setHorizontalAlignment(int alignment) {
		if (alignment == LEFT || alignment == CENTER || alignment == RIGHT)
			this.alignment = alignment;
		else
			this.alignment = UNALIGNED;
	}

	public int getHorizontalAlignment() {
		return alignment;
	}

	private int alignment = UNALIGNED;
}
