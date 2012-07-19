/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.command.action;

import java.io.PrintWriter;

import org.apache.commons.cli.HelpFormatter;

/**
 * @author rflitcroft
 *
 */
public class VersionCommand implements DroidCommand {

    /** Options message. */
    public static final String USAGE = "droid [options]";
    /** Wrap width. */
    public static final int WRAP_WIDTH = 80;

    private PrintWriter writer;
    
    /**
     * @param pw a print writer
     */
    public VersionCommand(PrintWriter pw) {
        this.writer = pw;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws CommandExecutionException {
        HelpFormatter formatter = new HelpFormatter();
        String versionString;
//        try {
        versionString = getClass().getPackage().getImplementationVersion();
            //ManifestUtils.getImplementationVersion();
//        } catch (IOException e) {
//            throw new CommandExecutionException(e);
//        }
        formatter.printWrapped(writer, WRAP_WIDTH, versionString);
    }

}
