package org.sadun.text.ffp;

import java.io.IOException;

import org.sadun.text.ffp.FlatFileParser.Condition;
import org.sadun.text.ffp.FlatFileParser.LineReader;

/**
 * A condition that never holds.
 * <p>
 * This condition cannot be constructed, but its single instance {@link #INSTANCE} used instead.
 *
 * @author Cristiano Sadun
 */
public class NullCondition implements Condition {
    
    public static final NullCondition INSTANCE = new NullCondition();

    private NullCondition() {
        
    }

    /**
     * Return <b>false</b>.
     * 
     * @return <b>false</b>.
     * @see org.sadun.text.ffp.FlatFileParser.Condition#holds(int, int, org.sadun.text.ffp.FlatFileParser.LineReader)
     */
    public boolean holds(int logicalLineCount, int physicalLineCount,
            LineReader reader) throws IOException {
        return false;
    }

}
