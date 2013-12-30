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

public class Table extends Container {

	public Table() {
		bordered = false;
	}

	public Table(String title) {
		caption = new Text(title);
		bordered = false;
	}

	public void print(java.io.PrintWriter pw) {
		pw.print("<table");
		if (bordered == true) {
			pw.print(" border=" + borderWidth);
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
		// if(styleClass)
		// pw.print(" class=" + styleClass);

		if (cellspacing > -1)
			pw.print(" cellspacing=" + cellspacing);

		if (cellpadding > -1)
			pw.print(" cellpadding=" + cellpadding);

		if (backgroundColor != null)
			pw.print(" bgcolor=" + backgroundColor);

		if (borderColor != null)
			pw.print(" bordercolor=" + borderColor);

		if (borderColorDark != null)
			pw.print(" bordercolordark=" + borderColorDark);

		if (borderColorLight != null)
			pw.print(" bordercolorlight=" + borderColorLight);

		pw.println(">");

		if (caption != null) {
			pw.print(" <caption>");
			caption.print(pw);
			pw.print("</caption>");
		}

		super.print(pw);

		pw.println("</table>");
	}

	public void setBordered(boolean b) {
		bordered = b;
	}

	public boolean isBordered() {
		return bordered;
	}

	public void setBorder(int pels) {
		borderWidth = pels;
		bordered = borderWidth != 0;
	}

	public int getBorder() {
		return borderWidth;
	}

	public void setWidth(int w) {
		width = w;
		widthInPercent = false;
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

	public void setCellSpacing(int n) {
		cellspacing = n;
	}

	public void setCellPadding(int n) {
		cellpadding = n;
	}

	public void setBackgroundColor(Color c) {
		backgroundColor = c;
	}

	public void setBorderColor(Color c) {
		borderColor = c;
	}

	public void setBorderColorDark(Color c) {
		borderColorDark = c;
	}

	public void setBorderColorLight(Color c) {
		borderColorLight = c;
	}

	public Table addEmptyRow() {
		add(new TableRow().addEmptyCell());
		return this;
	}

	private Text caption = null;
	private boolean bordered;
	private int borderWidth = 0;
	private int width = 0;
	private boolean widthInPercent = false;
	private int height = 0;
	private boolean heightInPercent = false;
	private int cellspacing = -1;
	private int cellpadding = -1;
	private Color backgroundColor = null;
	private Color borderColor = null;
	private Color borderColorDark = null;
	private Color borderColorLight = null;
}
