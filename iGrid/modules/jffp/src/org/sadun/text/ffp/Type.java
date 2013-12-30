/*
 * Created on Jan 16, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

/**
 * This class declares the field types recognized by {@link org.sadun.text.ffp.LineFormat}
 * and exposes them via public constant members.
 * <p>
 * This class cannot be instantiated directly.
 *
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
 * @version 1.0
 */
public class Type {
	
	private static final int UNDEFINED_TYPECODE = 0;
	private static final int ALFA_TYPECODE = 1;
	private static final int NUMERIC_TYPECODE = 2;
	private static final int CONSTANT_TYPECODE = 3;
    private static final int CONSTANTSET_TYPECODE = 3;
	
	/**
	 * Declares that no information is known about the field
	 */
	public static final Type UNDEFINED = new Type(UNDEFINED_TYPECODE, "undefined type");
	
	/**
	 * The field is alphanumeric
	 */
	public static final Type ALFA = new Type(ALFA_TYPECODE, "alphanumeric");
	
	/**
	 * The field is numeric
	 */
	public static final Type NUMERIC= new Type(NUMERIC_TYPECODE, "numeric");
	
	/**
	 * The field is a (alphanumeric) constant
	 */
	public static final Type CONSTANT = new Type(CONSTANT_TYPECODE, "constant");
    
    /**
     * The field is a set of (alphanumeric) constants
     */
    public static final Type CONSTANTSET = new Type(CONSTANTSET_TYPECODE, "constantset");
	
	private int code;
	private String description;
	
	protected Type(int code, String description) {
		this.code=code;
		this.description=description;
	}
	
	/**
	 * Return true if the given object is the same type as this
	 * @return true if the given object is the same type as this
	 */
	public final boolean equals(Object obj) {
		if (obj instanceof Type) {
			return ((Type)obj).code==code;
		}
		return false;
	}
	
	/**
	 * Return a description of the type.
	 * @return a description of the type.
	 */
	public String toString() { return description; }

}

