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
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.com.bytecode.opencsv.CSVWriter;

import uk.gov.nationalarchives.droid.export.interfaces.ItemWriter;
import uk.gov.nationalarchives.droid.profile.FormatIdentification;
import uk.gov.nationalarchives.droid.profile.NodeMetaData;
import uk.gov.nationalarchives.droid.profile.ProfileResourceNode;
import uk.gov.nationalarchives.droid.profile.referencedata.Format;

/**
 * @author rflitcroft
 *
 */
public class CsvItemWriter implements ItemWriter<ProfileResourceNode> {

    /**
     * 
     */
    private static final String FILE_URI_SCHEME = "file";

    private static final String[] HEADERS = {
        "URI",
        "FILE_PATH",
        "NAME",
        "METHOD",
        "STATUS",
        "SIZE",
        "TYPE",
        "EXT",
        "LAST_MODIFIED",
        "PUID",
        "MIME_TYPE",
        "FORMAT_NAME",
        "FORMAT_VERSION",
    };
    
    private final Log log = LogFactory.getLog(getClass());

    private CSVWriter csvWriter;
    private FastDateFormat dateFormat = DateFormatUtils.ISO_DATETIME_FORMAT;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(List<? extends ProfileResourceNode> nodes) {
        try {
            for (ProfileResourceNode node : nodes) {
                NodeMetaData metaData = node.getMetaData();
                
                for (FormatIdentification formatId : node.getFormatIdentifications()) {
                    Format format = formatId.getFormat();    
                    String[] nodeEntries = new String[] {
                        node.getUri().toString(),
                        toFilePath(node.getUri()),
                        toFileName(metaData.getName()),
                        nullSafeName(metaData.getIdentificationMethod()),
                        metaData.getNodeStatus().name(),
                        nullSafeNumber(metaData.getSize()),
                        metaData.getResourceType().name(),
                        metaData.getExtension(),
                        nullSafeDate(metaData.getLastModifiedDate(), dateFormat),
                        format.getPuid(),
                        format.getMimeType(),
                        format.getName(),
                        format.getVersion(),
                    };
                    csvWriter.writeNext(nodeEntries);
                    
                }
                csvWriter.flush();
            }
            
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e.getMessage(), e);
        }
        
    }

    /**
     * @param csvWriter the csvWriter to write to.
     */
    void setCsvWriter(CSVWriter csvWriter) {
        this.csvWriter = csvWriter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open(Writer writer) {
        csvWriter = new CSVWriter(writer);
        csvWriter.writeNext(HEADERS);
    }

    /**
     * Closes the CSV writer.
     */
    @Override
    public void close() {
        try {
            csvWriter.close();
        } catch (IOException e) {
            log.warn("Error closing CSV output file.", e);
        }
    }
    
    private static String nullSafeName(Enum<?> value) {
        return value == null ? null : value.name();
    }
    
    private static String nullSafeNumber(Number number) {
        return number == null ? null : number.toString();
    }
    
    private static String nullSafeDate(Date date, FastDateFormat format) {
        return date == null ? null : format.format(date);
    }
    
    private static String toFilePath(URI uri) {
        if (FILE_URI_SCHEME.equals(uri.getScheme())) {
            return new File(uri).getAbsolutePath();
        }
        
        return null;
    }

    private static String toFileName(String name) {
        return FilenameUtils.getName(name);
    }

}
