/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.export;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVWriter;

import uk.gov.nationalarchives.droid.core.interfaces.IdentificationMethod;
import uk.gov.nationalarchives.droid.core.interfaces.NodeStatus;
import uk.gov.nationalarchives.droid.core.interfaces.ResourceType;
import uk.gov.nationalarchives.droid.export.interfaces.JobOptions;
import uk.gov.nationalarchives.droid.profile.FormatIdentification;
import uk.gov.nationalarchives.droid.profile.NodeMetaData;
import uk.gov.nationalarchives.droid.profile.ProfileResourceNode;
import uk.gov.nationalarchives.droid.profile.referencedata.Format;

/**
 * @author rflitcroft
 *
 */
public class CsvItemWriterTest {

    private CsvItemWriter itemWriter;
    private File destination;
    private CSVWriter csvWriter;
    
    @Before
    public void setup() {
        File dir = new File("exports");
        dir.mkdir();
        destination = new File(dir, "test1.csv");
        destination.delete();
        itemWriter = new CsvItemWriter();
        csvWriter = mock(CSVWriter.class);
        itemWriter.setCsvWriter(csvWriter);
    }
    
    @Test
    public void testWriteHeadersOnOpen() throws IOException {
        Writer writer = mock(Writer.class);
        
        JobOptions jobOptions = mock(JobOptions.class);
        when(jobOptions.getParameter("location")).thenReturn("test.csv");
        
        itemWriter.open(writer);
        String expectedString =
              "\"URI\","
            + "\"FILE_PATH\","
            + "\"NAME\","
            + "\"METHOD\","
            + "\"STATUS\","
            + "\"SIZE\","
            + "\"TYPE\","
            + "\"EXT\","
            + "\"LAST_MODIFIED\","
            + "\"PUID\","
            + "\"MIME_TYPE\","
            + "\"FORMAT_NAME\","
            + "\"FORMAT_VERSION\"\n";
        verify(writer).write(expectedString, 0, expectedString.length());
    }
    
    @Test
    public void testWriteNoNodes() {
        List<ProfileResourceNode> nodes = new ArrayList<ProfileResourceNode>();
        itemWriter.write(nodes);
        verify(csvWriter, never()).writeNext(any(String[].class));
    }

    @Test
    public void testWriteOneNode() {
        List<ProfileResourceNode> nodes = new ArrayList<ProfileResourceNode>();
        nodes.add(buildProfileResourceNode(1, 1001L));
        itemWriter.write(nodes);
        
        String[] expectedEntry = new String[] {
            "file:/C:/my/file1.txt", 
            "C:\\my\\file1.txt",
            "file1.txt", 
            "BINARY_SIGNATURE", 
            "Identified", 
            "1001", 
            "FILE", 
            "foo", 
            "1970-01-01T04:25:45",
            "fmt/1",
            "text/plain",
            "Plain Text",
            "1.0",
        };
        
        verify(csvWriter).writeNext(expectedEntry);
    }

    @Test
    public void testWriteNodeWithNullFormat() {
        List<ProfileResourceNode> nodes = new ArrayList<ProfileResourceNode>();
        ProfileResourceNode node = buildProfileResourceNode(1, 1001L);
        for (FormatIdentification id : node.getFormatIdentifications()) {
            id.setFormat(Format.NULL);
        }
        nodes.add(node);

        itemWriter.write(nodes);
        
        String[] expectedEntry = new String[] {
            "file:/C:/my/file1.txt", 
            "C:\\my\\file1.txt",
            "file1.txt", 
            "BINARY_SIGNATURE", 
            "Identified", 
            "1001", 
            "FILE", 
            "foo", 
            "1970-01-01T04:25:45",
            null,
            null,
            null,
            null,
        };
        
        verify(csvWriter).writeNext(expectedEntry);
    }

    @Test
    public void testWriteOneNodeWithNullSize() {
        List<ProfileResourceNode> nodes = new ArrayList<ProfileResourceNode>();
        nodes.add(buildProfileResourceNode(1, null));
        itemWriter.write(nodes);
        
        String[] expectedEntry = new String[] {
            "file:/C:/my/file1.txt", 
            "C:\\my\\file1.txt",
            "file1.txt", 
            "BINARY_SIGNATURE", 
            "Identified", 
            null, 
            "FILE", 
            "foo", 
            "1970-01-01T04:25:45",
            "fmt/1",
            "text/plain",
            "Plain Text",
            "1.0",
        };
        
        verify(csvWriter).writeNext(expectedEntry);
    }

    @Test
    public void testWriteOneNodeWithTwoFormats() {
        List<ProfileResourceNode> nodes = new ArrayList<ProfileResourceNode>();
        nodes.add(buildProfileResourceNode(1, 1000L + 1));
        nodes.add(buildProfileResourceNode(2, 1000L + 2));
        itemWriter.write(nodes);
        
        String[] expectedEntry1 = new String[] {
            "file:/C:/my/file1.txt", 
            "C:\\my\\file1.txt",
            "file1.txt", 
            "BINARY_SIGNATURE", 
            "Identified", 
            "1001", 
            "FILE", 
            "foo", 
            "1970-01-01T04:25:45",
            "fmt/1",
            "text/plain",
            "Plain Text",
            "1.0",
        };
        
        String[] expectedEntry2 = new String[] {
            "file:/C:/my/file2.txt", 
            "C:\\my\\file2.txt",
            "file2.txt", 
            "BINARY_SIGNATURE", 
            "Identified", 
            "1002", 
            "FILE", 
            "foo", 
            "1970-01-01T04:25:45",
            "fmt/2",
            "text/plain",
            "Plain Text",
            "1.0",
        };

        verify(csvWriter).writeNext(expectedEntry1);
        verify(csvWriter).writeNext(expectedEntry2);
    }

    @Test
    public void testWriteTenNodes() {
        List<ProfileResourceNode> nodes = new ArrayList<ProfileResourceNode>();
        
        for (int i = 1; i <= 10; i++) {
            nodes.add(buildProfileResourceNode(i, (long) i));
        }
        
        itemWriter.write(nodes);
        verify(csvWriter, times(10)).writeNext(any(String[].class));
    }
    
    private static ProfileResourceNode buildProfileResourceNode(int i, Long size) {
        File f = new File("C:/my/file" + i + ".txt");
        ProfileResourceNode node = new ProfileResourceNode(f.toURI());
        
        NodeMetaData metaData = new NodeMetaData();
        metaData.setExtension("foo");
        metaData.setIdentificationMethod(IdentificationMethod.BINARY_SIGNATURE);
        metaData.setLastModified(12345678L);
        metaData.setName("file" + i + ".txt");
        metaData.setNodeStatus(NodeStatus.Identified);
        metaData.setResourceType(ResourceType.FILE);
        metaData.setSize(size);
        node.setMetaData(metaData);
        
        Format format = new Format();
        format.setPuid("fmt/" + i);
        format.setMimeType("text/plain");
        format.setName("Plain Text");
        format.setVersion("1.0");
        
        final FormatIdentification formatIdentification = new FormatIdentification();
        formatIdentification.setFormat(format);
        node.addFormatIdentification(formatIdentification);
        
        return node;
    }
}
