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

public class Color {
	public static final int black = 0x000000;
	public static final int blue = 0x0000ff;
	public static final int cyan = 0x00ffff;
	public static final int darkGray = 0x404040;
	public static final int gray = 0x808080;
	public static final int green = 0x00ff00;
	public static final int lightGray = 0xc0c0c0;
	public static final int magenta = 0xff00ff;
	public static final int orange = 0xffc800;
	public static final int pink = 0xffafaf;
	public static final int red = 0xff0000;
	public static final int white = 0xffffff;
	public static final int yellow = 0xffff00;

	private int normalize(int n) {
		if (n < 0)
			n = 0;
		else if (n > 255)
			n = 255;
		return n;
	}

	/**
	 * Create a new color using a 24 bit integer value
	 * 
	 * @param rgb
	 *            the 24 bit color value (RRGGBB)
	 */

	public Color(int rgb) {
		this.rgb = (1 << 24) + (rgb & 0xffffff);
	}

	/**
	 * Create a new color using its red, green, and blue parts
	 * 
	 * @param r
	 *            the red part of the color
	 * @param g
	 *            the green part of the color
	 * @param b
	 *            the blue part of the color
	 */

	public Color(int r, int g, int b) {
		rgb = (1 << 24) + (normalize(r) << 16) + (normalize(g) << 8)
				+ normalize(b);
	}

	/**
	 * Convert the color to its string representation. This method returns the
	 * color in the form "#rrggbb" so that it can be used in HTML documents
	 * directly
	 * 
	 * @return the color as string
	 */

	public String toString() {
		return "#" + Integer.toString(rgb, 16).substring(1);
	}

	private int rgb; // Stores the current color
}
