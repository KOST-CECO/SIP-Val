/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.gov.nationalarchives.droid.results.handlers.ProgressObserver;

/**
 * @author rflitcroft
 * 
 */
public class ProfileDiskAction {

    private static final int UNITY_PERCENT = 100;
    private static final int BUFFER_SIZE = 1024;

    private final Log log = LogFactory.getLog(getClass());
    
    /**
     * Saves the profile to disk by zipping it up.
     * 
     * @param baseDir
     *            the base direcytory of the zip operation
     * @param destination
     *            the ntarget zip file
     * @param callback
     *            a progress observer, notified on progress
     * @throws IOException
     *             if a file IO operation failed
     */
    public void saveProfile(String baseDir, File destination,
            ProgressObserver callback) throws IOException {

        log.info(String.format("Saving profile [%s] to [%s]", baseDir, destination));
        
        File output = new File(destination.getAbsolutePath() + ".tmp~");
        output.delete();

        if (!output.createNewFile()) {
            throw new IOException(String.format("Error creating tmp file [%s]", output));
        }

        ProfileWalker profileWalker = new ProfileWalker(new File(baseDir), output, callback);

        try {
            profileWalker.save();
            callback.onProgress(UNITY_PERCENT);
            if (destination.exists()) {
                if (!destination.delete()) {
                    throw new IOException(String.format("Error removing old file [%s]", destination));
                }
            }
            if (!output.renameTo(destination)) {
                throw new IOException(String.format("Error creating saved file [%s]", destination));
            }
        } finally {
            profileWalker.close();
        }
    }

    /**
     * Walks directories and files in a profile.
     * 
     * @author rflitcroft
     * 
     */
    private final class ProfileWalker extends DirectoryWalker {

        private String sourcePath;
        private File destination;
        private ZipArchiveOutputStream out;
        private File source;
        private ProgressObserver callback;
        private final long bytesToProcess;
        private long bytesProcessed;

        /**
         *
         */
        public ProfileWalker(File source, File destination,
                ProgressObserver callback) {
            sourcePath = source.getAbsolutePath() + File.separator;
            this.source = source;
            this.destination = destination;
            this.callback = callback;

            bytesToProcess = FileUtils.sizeOfDirectory(source);
        }

        /**
         * @throws IOException
         * 
         */
        public void close() throws IOException {
            out.close();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void handleStart(File startDirectory, Collection results)
            throws IOException {

            out = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(destination)));
            out.setMethod(ZipOutputStream.DEFLATED);
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void handleEnd(Collection results) throws IOException {
            out.close();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected boolean handleDirectory(File directory, int depth,
                Collection results) {
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void handleDirectoryStart(File directory, int depth, Collection results) {
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void handleFile(File file, int depth, Collection results)
            throws IOException {

            ZipArchiveEntry entry = (ZipArchiveEntry) out.createArchiveEntry(file, 
                    StringUtils.substringAfter(file
                    .getAbsolutePath(), sourcePath));
            out.putArchiveEntry(entry);
            final FileInputStream in = new FileInputStream(file);
            try {
                IOUtils.copy(in, out);
            } finally {
                bytesProcessed += file.length();
                callback.onProgress((int) ((UNITY_PERCENT * bytesProcessed) / bytesToProcess));
                out.closeArchiveEntry();
                in.close();
            }

        }

        protected void save() throws IOException {
            walk(source, Collections.EMPTY_LIST);
        }
    }

    /**
     * Loads a droid file from disk.
     * 
     * @param source
     *            the droif file to load
     * @param destination
     *            the target directory where the source should be unpacked
     * @param observer
     *            a progress observer which is notified on progress
     * @throws IOException
     *             if the file operations failed
     */
    public void load(File source, File destination, ProgressObserver observer)
        throws IOException {

        // Delete any remnants of this expanded profile
        if (destination.exists()) {
            FileUtils.deleteDirectory(destination);
        }

        ZipFile zip = new ZipFile(source);

        try {
            // count the zip entries so we can do progress bar
            int maxCount = 0;
            for (Enumeration<? extends ZipEntry> it = zip.entries(); it
                    .hasMoreElements(); it.nextElement()) {
                maxCount++;
            }

            int count = 0;
            for (Enumeration<? extends ZipEntry> it = zip.entries(); it
                    .hasMoreElements();) {
                ZipEntry entry = it.nextElement();
                File expandedFile = new File(destination + File.separator
                        + entry.getName());

                BufferedInputStream in = new BufferedInputStream(zip
                        .getInputStream(entry));

                FileUtils.forceMkdir(expandedFile.getParentFile());
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(expandedFile));

                readFile(in, out, observer);
                observer.onProgress((UNITY_PERCENT * count++) / maxCount);
            }
            observer.onProgress(UNITY_PERCENT);
        } finally {
            zip.close();
        }

    }
    
    private void readFile(BufferedInputStream in, BufferedOutputStream out, ProgressObserver observer) 
        throws IOException {
        try {
            int bytesIn = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesIn = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesIn);
            }
        } finally {
            try {
                in.close();
            } finally {
                out.close();
            }
        }
    }
}
