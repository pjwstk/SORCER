/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

/**
 * 
 * @author Cristiano Sadun
 */
 abstract class FieldRelatedFFPParseException extends FFPParseException {

	private String expectedFieldName;
	private int expectedFieldStart;
	private int expectedFieldEnd;
	private Type expectedFieldType;
	private String expectedFieldImage;

	FieldRelatedFFPParseException(String msg, LineFormat.FieldInfo info) {
		super(msg);
		this.expectedFieldStart = info.getStart();
		this.expectedFieldEnd = info.getEnd();
		this.expectedFieldName = info.getName();
		this.expectedFieldType = info.getType();
		this.expectedFieldImage = info.getImage();
	}

	/**
	 * @return
	 */
	public int getExpectedFieldEnd() {
		return expectedFieldEnd;
	}

	/**
	 * @return
	 */
	public String getExpectedFieldImage() {
		return expectedFieldImage;
	}

	/**
	 * @return
	 */
	public String getExpectedFieldName() {
		return expectedFieldName;
	}

	/**
	 * @return
	 */
	public int getExpectedFieldStart() {
		return expectedFieldStart;
	}

	/**
	 * @return
	 */
	public Type getExpectedFieldType() {
		return expectedFieldType;
	}


}
