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

public class Document extends Container {

	public Document() {
		// do nothing
	}

	/**
	 * A constructor which allows us to set the documents title
	 * 
	 * @param title
	 *            This documents title
	 */

	public Document(String title) {
		this.title = new Text(title);
	}

	/**
	 * Print into a print writer pw the generated HTML code
	 * 
	 * @param pw
	 *            The PrintWriter to which all output goes
	 */
	public void print(java.io.PrintWriter pw) {
		begin(pw);
		printBody(pw);
		end(pw);
	}

	public void begin(java.io.PrintWriter pw) {
		pw
				.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\""
						+ "\"http://www.w3.org/TR/REC-html40/loose.dtd\">");

		pw.println("<html>");
		if (title != null) {
			pw.print("<head>\n<title>");
			title.print(pw);
			pw.println("</title>\n</head>");
		}

		pw.print("<body");

		if (backgroundColor != null) {
			pw.print(" bgcolor=\"" + backgroundColor + "\"");
		}

		if (background != null) {
			pw.print(" background=\"" + background + "\"");
			if (fixedBackground == true)
				pw.print(" bgproperties=fixed");
		}

		if (textColor != null)
			pw.print(" text=" + textColor);

		if (linkColor != null)
			pw.print(" link=" + linkColor);

		if (visitedLinkColor != null)
			pw.print(" vlink=" + visitedLinkColor);

		if (activeLinkColor != null)
			pw.print(" alink=" + activeLinkColor);

		pw.println(">");
	}

	public void printBody(java.io.PrintWriter pw) {
		super.print(pw);
	}

	public void end(java.io.PrintWriter pw) {
		pw.println("</body>\n</html>");
	}

	/**
	 * Set the title of the document
	 * 
	 * @param title
	 *            This documents title
	 */

	public void setTitle(String title) {
		this.title = new Text(title);
	}

	/**
	 * Query the title of the document
	 * 
	 * @return The title of the document
	 */

	public String getTitle() {
		return title.getText();
	}

	/**
	 * Set the background image (not fixed)
	 * 
	 * @param image
	 *            The URL of the background image
	 */

	public void setBackground(String image) {
		background = new String(image);
		fixedBackground = false;
	}

	/**
	 * Set the background image (fixed, watermark effect)
	 * 
	 * @param image
	 *            The URL of the background image
	 */

	public void setFixedBackground(String image) {
		background = new String(image);
		fixedBackground = true;
	}

	/**
	 * Set fixed/non-fixed background
	 * 
	 * @param fixed
	 *            True if background image is fixed, fals otherwise
	 */

	public void setFixedBackground(boolean fixed) {
		fixedBackground = fixed;
	}

	/**
	 * Set the documents background color
	 * 
	 * @param c
	 *            The background color
	 * @see jgapp.html.Color
	 */

	public void setBackgroundColor(Color c) {
		backgroundColor = c;
	}

	/**
	 * Set the documents background color
	 * 
	 * @param rgbc
	 *            The background RGB triple color
	 */

	public void setBackgroundColor(String rgbc) {
		backgroundColor = rgbc;
	}

	/**
	 * Set the text color
	 * 
	 * @param c
	 *            The text color
	 * @see jgapp.html.Color
	 */

	public void setTextColor(Color c) {
		textColor = c;
	}

	/**
	 * Set the color of a link
	 * 
	 * @param c
	 *            The link color
	 * @see jgapp.html.Color
	 */

	public void setLinkColor(Color c) {
		linkColor = c;
	}

	/**
	 * Set the color of a visited link
	 * 
	 * @param c
	 *            The color of a visited link
	 * @see jgapp.html.Color
	 */

	public void setVisitedLinkColor(Color c) {
		visitedLinkColor = c;
	}

	/**
	 * Set the color of an active linke
	 * 
	 * @param c
	 *            The color of an active link
	 * @see jgapp.html.Color
	 */

	public void setActiveLinkColor(Color c) {
		activeLinkColor = c;
	}

	private Text title = null;
	// can be instance of Color or RGB triple string
	private Object backgroundColor = null;
	private Color textColor = null;
	private Color linkColor = null;
	private Color visitedLinkColor = null;
	private Color activeLinkColor = null;
	private String background = null;
	private boolean fixedBackground = false;
}
