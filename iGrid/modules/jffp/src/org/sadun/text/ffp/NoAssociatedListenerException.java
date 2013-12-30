package org.sadun.text.ffp;

/**
 * An exception raised when a {@link org.sadun.text.ffp.DispatcherListener}is
 * used to dispatch parsing events from a
 * {@link org.sadun.text.ffp.FlatFileParser flat file parser}, its {@link  
 * 
 * @author Cristiano Sadun
 */
public class NoAssociatedListenerException extends AbortFFPException {

    private LineFormat format;
    private int logicalLinecount;
    private int physicalLineCount;
    private String[] values;

    /**
     * @param format
     * @param logicalLinecount
     * @param physicalLineCount
     * @param values
     */
    public NoAssociatedListenerException(LineFormat format,
            int logicalLinecount, int physicalLineCount, String[] values) {
        super(
                "Programming error (logical line "
                        + logicalLinecount
                        + ", physical line "
                        + physicalLineCount
                        + "): no listener has been associated to parsing of lines with format "
                        + format+". Please use associateListener() in DispatcherListener to associate one, " +
                        		"or construct the DispatcherListner with (true).");
        this.format = format;
        this.logicalLinecount = logicalLinecount;
        this.physicalLineCount = physicalLineCount;
        this.values = values;
    }

    /**
     * The format that has successfully matched the line.
     * 
     * @return Return the format that has successfully matched the line.
     */
    public LineFormat getFormat() {
        return format;
    }

    /**
     * The logical line number of the matched line.
     * 
     * @return Return the logical line number of the matched line.
     */
    public int getLogicalLinecount() {
        return logicalLinecount;
    }

    /**
     * The physical line number of the matched line.
     * 
     * @return Return the physical line number of the matched line.
     */
    public int getPhysicalLineCount() {
        return physicalLineCount;
    }

    /**
     * @return Returns the values.
     */
    public String[] getValues() {
        return values;
    }
}