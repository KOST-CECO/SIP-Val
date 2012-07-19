/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.submitter;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.xml.bind.JAXBException;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

import uk.gov.nationalarchives.droid.profile.DirectoryProfileResource;
import uk.gov.nationalarchives.droid.submitter.FileWalker.ProgressEntry;

/**
 * @author rflitcroft
 *
 */
public class FileWalkerPersistenceTest {

    private ProfileWalkerDao profileWalkerDao;
    private File testDir;
    
    @Before
    public void setup() throws JAXBException {
        XMLUnit.setIgnoreWhitespace(true);
        
        profileWalkerDao = new ProfileWalkerDao();
        testDir = new File("tmp/" + getClass().getSimpleName());
        testDir.mkdirs();
        profileWalkerDao.setProfileHomeDir(testDir.getPath());
    }
    
    @Test
    public void testSaveProfileWithSerializedPofileSpecWalker() throws Exception {
        
        final File dirResource1 = new File("root/dir");
        final File dirResource2 = new File("root/dir/subDir");
        final File dirResource3 = new File("root/dir/subdir1/subDir2");
        
        Deque<ProgressEntry> progress = new ArrayDeque<ProgressEntry>();
        
        final File root = new File("root");
        FileWalker filewalker = new FileWalker(root.toURI(), true);
        progress.push(new ProgressEntry(dirResource1.toURI(), 1, null));
        progress.push(new ProgressEntry(dirResource2.toURI(), 2, null));
        progress.push(new ProgressEntry(dirResource3.toURI(), 3, null));
        
        filewalker.setProgress(progress);
        
        ProfileWalkState state = new ProfileWalkState();
        state.setCurrentFileWalker(filewalker);
        state.setCurrentResource(new DirectoryProfileResource(root, true));
        
        profileWalkerDao.save(state);
        
        String control = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" 
            + "<ProfileWalk Status=\"NOT_STARTED\">" 
            + "    <Dir Recursive=\"true\">" 
            + "        <Size>0</Size>" 
            + "        <LastModifiedDate>1970-01-01T01:00:00+01:00</LastModifiedDate>" 
            + "        <Extension></Extension>" 
            + "        <Name>root</Name>" 
            + "        <Uri>" + root.toURI() + "</Uri>" 
            + "    </Dir>" 
            + "    <FileWalker Recursive=\"true\">" 
            + "        <RootUri>" + root.toURI() + "</RootUri>" 
            + "        <Progress>"
            + "            <ProgressEntry Id=\"3\">" 
            + "                <Uri>" + dirResource3.toURI() + "</Uri>" 
            + "            </ProgressEntry>"
            + "            <ProgressEntry Id=\"2\">" 
            + "                <Uri>" + dirResource2.toURI() + "</Uri>" 
            + "            </ProgressEntry>"
            + "            <ProgressEntry Id=\"1\">" 
            + "                <Uri>" + dirResource1.toURI() + "</Uri>" 
            + "            </ProgressEntry>"
            + "        </Progress>"
            + "    </FileWalker>" 
            + "</ProfileWalk>"; 

        XMLAssert.assertXMLEqual(new StringReader(control), 
                new FileReader(new File(testDir, "profile_progress.xml")));

    }

}
