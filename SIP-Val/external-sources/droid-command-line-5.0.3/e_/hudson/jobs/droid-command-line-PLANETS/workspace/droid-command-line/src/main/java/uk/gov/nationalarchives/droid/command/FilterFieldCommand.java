/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.command;

import java.io.PrintWriter;

import org.apache.commons.cli.HelpFormatter;

import uk.gov.nationalarchives.droid.command.action.DroidCommand;
import uk.gov.nationalarchives.droid.command.filter.DqlCriterionMapper;
import uk.gov.nationalarchives.droid.command.i18n.I18N;
import uk.gov.nationalarchives.droid.core.interfaces.filter.CriterionFieldEnum;

/**
 * @author rflitcroft
 *
 */
public class FilterFieldCommand implements DroidCommand {

    private static final int LINE_INDENT = 4;
    private static final int LINE_WIDTH = 80;

    private static final String BASE_PROPERTY = "dql.help.";
    
    private PrintWriter printWriter;
    private HelpFormatter helpFormatter = new HelpFormatter();
    
    /**
     * @param printWriter a print writer for user output
     */
    public FilterFieldCommand(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        for (String fieldName : DqlCriterionMapper.allDqlFields()) {
            final CriterionFieldEnum field = DqlCriterionMapper.forField(fieldName);
            String description = I18N.getResource(BASE_PROPERTY + field.name());
            String text = String.format("%s\t%s", fieldName, description);
            helpFormatter.printWrapped(printWriter, LINE_WIDTH, LINE_INDENT, text);
        }
        
        printWriter.flush();
        
    }

}
