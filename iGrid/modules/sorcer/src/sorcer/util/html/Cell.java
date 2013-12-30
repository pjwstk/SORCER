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

public class Cell extends Container implements HorizontalAligned,
		VerticalAligned {

	public Cell() {
	}

	public Cell(Component c) {
		add(c);
	}

	public Cell(String c) {
		if (c != null)
			add(new Text(c));
	}

	public void print(java.io.PrintWriter pw) {
		if (header == true)
			pw.print("<th");
		else
			pw.print("<td");

		if (backgroundColor != "")
			pw.print(" bgcolor=" + backgroundColor);

		if (rowspan > 0)
			pw.print(" rowspan=" + rowspan);

		if (colspan > 0)
			pw.print(" colspan=" + colspan);

		if (halign != UNALIGNED) {
			pw.print(" align=");
			switch (halign) {
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

		if (valign != UNALIGNED) {
			pw.print(" valign=");
			switch (valign) {
			case TOP:
				pw.print("top");
				break;
			case MIDDLE:
				pw.print("middle");
				break;
			case BOTTOM:
				pw.print("bottom");
				break;
			}
		}

		if (width > 0) {
			pw.print(" width=" + width);
			if (widthInPercent)
				pw.print("%");
		}

		if (height > 0) {
			pw.print(" height=" + height);
			if (heightInPercent)
				pw.print("%");
		}

		pw.println(">");

		super.print(pw);

		if (header == true)
			pw.println("</th>");
		else
			pw.println("</td>");
	}

	public void setHeader(boolean b) {
		header = b;
	}

	public boolean isHeader() {
		return header;
	}

	public void setBackgroundColor(String c) {
		backgroundColor = c;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setRowspan(int n) {
		rowspan = n;
	}

	public void setColumnspan(int n) {
		colspan = n;
	}

	public void setHorizontalAlignment(int n) {
		halign = n;
	}

	public int getHorizontalAlignment() {
		return halign;
	}

	public void setVerticalAlignment(int n) {
		valign = n;
	}

	public int getVerticalAlignment() {
		return valign;
	}

	public void setWidth(int w) {
		width = w;
		widthInPercent = false;
	}

	public int getWidth() {
		return width;
	}

	public boolean isWidthPercent() {
		return widthInPercent;
	}

	public void setWidthPercent(int w) {
		width = w;
		widthInPercent = true;
	}

	public void setHeight(int w) {
		height = w;
		heightInPercent = false;
	}

	public void setHeightPercent(int w) {
		height = w;
		heightInPercent = true;
	}

	public int getHeight() {
		return height;
	}

	public boolean isHeightPercent() {
		return heightInPercent;
	}

	private String backgroundColor = "";
	private int halign = UNALIGNED;
	private int valign = UNALIGNED;
	private int height = 0;
	private boolean heightInPercent = false;
	private int width = 0;
	private boolean widthInPercent = false;
	private boolean header = false;
	private int rowspan = 0;
	private int colspan = 0;
}
