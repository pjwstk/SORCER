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

public class Image extends Component {
	/**
	 * Create an image markup including only the image source
	 * 
	 * @param src
	 *            the image source
	 */
	public Image(String src) {
		imageSrc = src;
	}

	/**
	 * Create an image markup for the image source and an alternate text
	 * 
	 * @param src
	 *            the image source
	 * @param txt
	 *            the alternate text
	 */
	public Image(String src, String txt) {
		imageSrc = src;
		altTxt = txt;
	}

	/**
	 * Set the alternate text
	 * 
	 * @param s
	 *            the alternate text
	 */
	public void setAlternateText(String s) {
		altTxt = s;
	}

	/**
	 * Get the alternate text
	 * 
	 * @return the alternate text
	 */
	public String getAlternateText() {
		return altTxt;
	}

	/**
	 * Set the image width. The image is shrinked or enlarged to this width
	 * 
	 * @param w
	 *            the image width in pixels
	 */
	public void setWidth(int w) {
		if (w >= 0) {
			width = w;
			widthInPercent = false;
		}
	}

	/**
	 * Set the image width in percent
	 * 
	 * @param w
	 *            the image width in percent
	 */
	public void setWidthInPercent(int w) {
		if (w >= 0) {
			width = w;
			widthInPercent = true;
		}
	}

	/**
	 * Get the image width. Note: There is no way to query if the width is in
	 * percent or in pixels.
	 * 
	 * @return the image width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Set the image height
	 * 
	 * @param h
	 *            the image height in pixels
	 */
	public void setHeight(int h) {
		if (h >= 0) {
			height = h;
			heightInPercent = false;
		}
	}

	/**
	 * Set the image height in percent
	 * 
	 * @param h
	 *            the image height in percent
	 */
	public void setHeightInPercent(int h) {
		if (h >= 0) {
			height = h;
			heightInPercent = true;
		}
	}

	/**
	 * Get the image height
	 * 
	 * @return the image height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Set the image border width. A border width of 0 means nor border at all
	 * 
	 * @param b
	 *            the border width in pixels
	 */
	public void setBorder(int b) {
		borderWidth = b;
	}

	/**
	 * Get the border width
	 * 
	 * @return the border width in pixels
	 */
	public int getBorder() {
		return borderWidth;
	}

	/**
	 * Show the image object (as HTML tag...)
	 * 
	 * @param pw
	 *            the PrintWriter object on which to print the object
	 */
	public void print(java.io.PrintWriter pw) {
		pw.print("<img src=\"" + imageSrc + "\"");
		if (width > 0) {
			pw.print(" width=" + width);
			if (widthInPercent == true)
				pw.print("%");
		}

		if (height > 0) {
			pw.print(" height=" + height);
			if (heightInPercent == true)
				pw.print("%");
		}

		if (borderWidth > 0)
			pw.print(" border=" + borderWidth);

		if (altTxt != null)
			pw.print(" alt=\"" + altTxt + "\"");
		pw.println(">");
	}

	private String altTxt = null; // Alternate image text
	private String imageSrc; // Image source (URL)
	private int width = 0; // Image width
	private boolean widthInPercent = false; // If true, image width is in
	// percent
	private int height = 0; // Image height
	private boolean heightInPercent = false; // If true, image height is in
	// percent
	private int borderWidth = 0; // Border width in percent
}
