package org.sadun.text.ffp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sadun.text.ffp.FlatFileParser.Listener;

/**
 * A dispatcher that allows to have different listeners receiving parsing events from
 * a {@link org.sadun.text.ffp.FlatFileParser} depending on the
 * {@link org.sadun.text.ffp.LineFormat line format} which matched the line,
 * instead of one monolithic one receiving all parsing events.
 * <p>
 * It's useful when the flat file contains more than a few different formats -
 * avoiding to have a big switch statement (or multiple <b>if </b>s) to do
 * something different depending on which
 * {@link org.sadun.text.ffp.LineFormat line format} has actually matched a
 * line.
 * 
 * 
 * @version 1.0
 * @author Cristiano Sadun
 */
public class DispatcherListener implements Listener {

    private static class Association {
        private LineFormat lineFormat;
        private FlatFileParser.Listener Listener;

        private Association(FlatFileParser.Listener associatedListener,
                LineFormat associatedLineFormat) {
            this.Listener = associatedListener;
            this.lineFormat = associatedLineFormat;
        } 

        /**
         * @return Returns the associated LineFormat.
         */
        public LineFormat getLineFormat() {
            return lineFormat;
        } 

        /**
         * @return Returns the associated Listener.
         */
        public FlatFileParser.Listener getListener() {
            return Listener;
        } 
    } 

    private List associatedLineFormats = new ArrayList();
    private boolean allowLostEvents;

    /**
     * Create a dispatcher listener, with no other listeners associated. If
     * <tt>allowLostEvents</tt> is false, the listener will require that every
     * event be associated to a listener. Otherwise, events which aren't
     * associated to any listener will simply be ignored. Use one {@link 
     * #associateListener(LineFormat, FlatFileParser.Listener)}  overload to
     * associate a listener to a specific condition or line format.
     * 
     * @param allowLostEvents
     *            if true, the listener will <i>not </i> require that all events
     *            are associated to a listener.
     */
    public DispatcherListener(boolean allowLostEvents) {
        this.allowLostEvents = allowLostEvents;
    } 

    /**
     * Create a dispatcher listener which does <i>not </i> require that all
     * events are associated to a listener (see
     * {@link #DispatcherListener(boolean)} ).
     *  
     */
    public DispatcherListener() {
        this(true);
    } 

    /**
     * Receive a parsing event and invoke the associated listener, if any.
     * 
     * @see org.sadun.text.ffp.FlatFileParser.Listener#lineParsed(org.sadun.text.ffp.LineFormat,
     *      int, int, java.lang.String[])
     */
    public final void lineParsed(LineFormat format, int logicalLinecount,
            int physicalLineCount, String[] values) throws AbortFFPException {
        for (Iterator i = associatedLineFormats.iterator(); i.hasNext();) {
            Association a = (Association) i.next();
            if (format.equals(a.getLineFormat())) {
                a.getListener().lineParsed(format, logicalLinecount,
                        physicalLineCount, values);
                return;
            } 
        } 
        if (!allowLostEvents)
            throw new NoAssociatedListenerException(format, logicalLinecount,
                    physicalLineCount, values);

    } 

    public void associateListener(LineFormat format,
            FlatFileParser.Listener listener) {
        associatedLineFormats.add(new Association(listener, format));
    } 

    /**
     * If <tt>true</tt>, the dispatcher allows parsing events which are not
     * associated to any listener to be ignored; otherwise, events which are not
     * associated to any listener will raise a
     * {@link NoAssociatedListenerException} .
     * 
     * @return Return whether or not the dispatcher allows parsing events which
     *         are not associated to any listener to be ignored.
     */
    public boolean isAllowLostEvents() {
        return allowLostEvents;
    } 

    /**
     * Set whether or not <tt>true</tt>, the dispatcher allows parsing events
     * which are not associated to any listener to be ignored. If not, events
     * which are not associated to any listener will raise a
     * {@link NoAssociatedListenerException} .
     * 
     * @param allowLostEvents
     *            Set whether or not the dispatcher allows parsing events which
     *            are not associated to any listener to be ignored.
     */
    public void setAllowLostEvents(boolean allowLostEvents) {
        this.allowLostEvents = allowLostEvents;
    } 
} 