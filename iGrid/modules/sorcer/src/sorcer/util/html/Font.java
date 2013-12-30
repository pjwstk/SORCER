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

public class Font extends Container {

	public Font() {
		// do nothing
	}

	public Font(Object c) {
		if (c instanceof String)
			add(new Text((String) c));
		else
			add((Component) c);
	}

	public Font(Object c, String face) {
		this(c, face, null, "");
	}

	public Font(Object c, String face, String size) {
		this(c, face, size, "");
	}

	public Font(Object c, String face, String size, Color color) {
		this(c, face, size, color, null);
	}

	public Font(Object c, String face, String size, String rgbTriplet) {
		this(c, face, size, rgbTriplet, null);
	}

	public Font(Object c, String face, String size, Color color,
			String highlighting) {
		this(c);
		this.face = face;
		this.size = size;
		this.color = color;
		this.highlighting = highlighting;
	}

	public Font(Object c, String face, String size, String rgbTriplet,
			String highlighting) {
		this(c);
		this.face = face;
		this.size = size;
		this.rgbTriplet = rgbTriplet;
		this.highlighting = highlighting;
	}

	public Font(String c, String face, String size, String rgbTriplet,
			String highlighting) {
		add(new Text(c));
		this.face = face;
		this.size = size;
		this.rgbTriplet = rgbTriplet;
		this.highlighting = highlighting;
	}

	public void print(java.io.PrintWriter pw) {
		pw.print("<font");

		if (face != null)
			pw.print(" face=\"" + face + '"');

		if (size != null)
			pw.print(" size=\"" + size + '"');

		if (color != null)
			pw.print(" color=" + color);

		if (rgbTriplet != null && rgbTriplet.length() != 0)
			pw.print(" color=\"" + rgbTriplet + '"');

		pw.print(">");

		if (highlighting != null)
			pw.print("<" + highlighting + ">");

		super.print(pw);

		if (highlighting != null)
			pw.print("</" + highlighting + ">");

		pw.println("</font>");
	}

	// migh be realtive to base, i.g. +2, -2
	public void setSize(String s) {
		size = s;
	}

	public void setColor(Color c) {
		color = c;
	}

	public void setFace(String f) {
		face = f;
	}

	/**
	 * Set a highlighting elemnt such as EM, STRONG, CODE, KBD, VAR, DFN CITE or
	 * physical highlighting elements, such as B for boldface, I for italics, TT
	 * for typewriter, U for underlined font.
	 */
	public void setHighlighting(String h) {
		highlighting = h;
	}

	private String size = null;
	private String face = null;
	private Color color = null;
	private String rgbTriplet = null;
	private String highlighting = null;
}
