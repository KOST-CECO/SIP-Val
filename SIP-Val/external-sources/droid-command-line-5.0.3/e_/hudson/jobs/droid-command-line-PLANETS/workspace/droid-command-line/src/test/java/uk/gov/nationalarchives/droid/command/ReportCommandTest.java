/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.command;

import java.io.File;
import java.io.PrintWriter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.gov.nationalarchives.droid.RuntimeConfig;
import uk.gov.nationalarchives.droid.command.action.CommandFactory;
import uk.gov.nationalarchives.droid.command.action.CommandFactoryImpl;
import uk.gov.nationalarchives.droid.command.action.CommandLineException;
import uk.gov.nationalarchives.droid.command.context.SpringUiContext;

/**
 * @author Alok Kumar Dash
 */
@Ignore("these tests take too long")
public class ReportCommandTest {

    private CommandFactory commandFactory;
    private PrintWriter printWriter;

    private String filename = System.getProperty("user.home") + "\\.droid\\tmp"
            + "planets.xml";
    private File destination = new File(filename);

    @Before
    public void setup() {
        destination.delete();
        assertFalse(destination.exists());
        printWriter = mock(PrintWriter.class);
        commandFactory = new CommandFactoryImpl(new SpringUiContext(),
                printWriter);
    }

    @After
    public void tearDown() {
        destination.delete();
    }

    @Test
    public void testReportCommand() throws Exception {
        RuntimeConfig.configureRuntimeEnvironment();
        String[] args = new String[] {"-r", filename, "-p",
            "src\\test\\resources\\alok.droid", "-n", "planets", };
        DroidCommandLine droidCommandLine = new DroidCommandLine(args,
                commandFactory);
        droidCommandLine.run();
        assertTrue(destination.exists());

    }

    @Test
    public void testReportCommandWithNonExistentReportName() {
        RuntimeConfig.configureRuntimeEnvironment();
        String[] args = new String[] {"-r", filename, "-p",
            "src\\test\\resources\\alok.droid", "-n", "myPlanets", };
        DroidCommandLine droidCommandLine = new DroidCommandLine(args,
                commandFactory);
        try {
            droidCommandLine.run();
        } catch (CommandLineException parEx) {
            assertTrue(parEx.getMessage(), "myPlanets report is not a valid report type."
                    .equals(parEx.getMessage()));
        }

        assertFalse(destination.exists());

    }

}
