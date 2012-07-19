package ch.bedag.a6z.sipvalidator.logging;

import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Erzeugt ein vollkommen "nacktes" Layout, welches nichts als die eigentliche Message enthält.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */
public class MessageOnlyLayout extends SimpleLayout {

    StringBuffer sbuf = new StringBuffer(128);

    @Override
    public String format(LoggingEvent event) {
        sbuf.setLength(0);
        sbuf.append(event.getRenderedMessage());
        sbuf.append(LINE_SEP);
        return sbuf.toString();
    }

}
