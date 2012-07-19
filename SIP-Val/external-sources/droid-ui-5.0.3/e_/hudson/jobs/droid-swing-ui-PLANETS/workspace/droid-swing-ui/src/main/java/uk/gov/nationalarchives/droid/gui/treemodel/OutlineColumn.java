/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.treemodel;

import java.util.List;

import org.apache.commons.io.FilenameUtils;

import uk.gov.nationalarchives.droid.core.interfaces.IdentificationMethod;
import uk.gov.nationalarchives.droid.core.interfaces.NodeStatus;
import uk.gov.nationalarchives.droid.profile.FormatIdentification;
import uk.gov.nationalarchives.droid.profile.ProfileResourceNode;
import uk.gov.nationalarchives.droid.profile.referencedata.Format;

/**
 * @author rflitcroft
 * 
 */
public enum OutlineColumn {

    /** Date column. */
    DATE {
        /** {@inheritDoc} */
        @Override
        public Class<DirectoryComparableDate> getColumnClass() {
            return DirectoryComparableDate.class;
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "Last modified";
        }

        /** {@inheritDoc} */
        @Override
        public Object getValue(ProfileResourceNode node) {
            return new DirectoryComparableDate(node.getMetaData().getLastModifiedDate(), node.allowsChildren());
        }
    },

    /** Date column. */
    SIZE {
        /** {@inheritDoc} */
        @Override
        public Class<DirectoryComparableLong> getColumnClass() {
            return DirectoryComparableLong.class;
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "Size (bytes)";
        }

        /** {@inheritDoc} */
        @Override
        public Object getValue(ProfileResourceNode node) {
            return new DirectoryComparableLong(node.getMetaData().getSize(), node.allowsChildren());
        }
    },

    /** Date column. */
    EXTENSION {
        /** {@inheritDoc} */
        @Override
        public Class<DirectoryComparableString> getColumnClass() {
            return DirectoryComparableString.class;
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "Extension";
        }

        /** {@inheritDoc} */
        @Override
        public Object getValue(ProfileResourceNode node) {
            return new DirectoryComparableString(FilenameUtils.getExtension(node.getMetaData().getName()), node
                    .allowsChildren());
        }
    },

    /** Date column. */
    FORMAT {
        /** {@inheritDoc} */
        @Override
        public Class<DirectoryComparableString> getColumnClass() {
            return DirectoryComparableString.class;
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "Format";
        }

        /** {@inheritDoc} */
        @Override
        public Object getValue(ProfileResourceNode node) {
            String formatValue = null;

            List<FormatIdentification> formatIdentifications = node.getFormatIdentifications();
            if (!formatIdentifications.isEmpty()) {
                formatValue = formatIdentifications.get(0).getFormat().getName();
            }
            return new DirectoryComparableString(formatValue, node.allowsChildren());
        }
    },

    /** The identificatiuon method. */
    IDENTIFICATION_METHOD {
        /** {@inheritDoc} */
        @Override
        public Class<DirectoryComparableString> getColumnClass() {
            return DirectoryComparableString.class;
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "Method";
        }

        /** {@inheritDoc} */
        @Override
        public Object getValue(ProfileResourceNode node) {
            IdentificationMethod method = node.getMetaData().getIdentificationMethod();
            return new DirectoryComparableString(method == null ? "" : method.getMethod(), node.allowsChildren());
        }
    },

    /** The PUID. */
    PUID {
        /** {@inheritDoc} */
        @Override
        public Class<DirectoryComparableString> getColumnClass() {
            return DirectoryComparableString.class;
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "PUID";
        }

        /** {@inheritDoc} */
        @Override
        public Object getValue(ProfileResourceNode node) {
            Format format = null;
            List<FormatIdentification> formatIdentifications = node.getFormatIdentifications();
            if (!formatIdentifications.isEmpty()) {
                format = formatIdentifications.get(0).getFormat();
            }
            String puid;
            if (format == null) {
                puid = "";
            } else {
                puid = format.equals(Format.NULL) ? "" : format.getPuid();
            }
            return new DirectoryComparableString("NONE".equals(puid) ? "" : "<html><a href=\"\">"
                    + puid + "</a></html>", node.allowsChildren());
        }
    },

    /** The PUID. */
    MIME_TYPE {
        /** {@inheritDoc} */
        @Override
        public Class<DirectoryComparableString> getColumnClass() {
            return DirectoryComparableString.class;
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "Mime type";
        }

        /** {@inheritDoc} */
        @Override
        public Object getValue(ProfileResourceNode node) {

            String mimeType = null;

            List<FormatIdentification> formatIdentifications = node.getFormatIdentifications();
            if (!formatIdentifications.isEmpty()) {
                mimeType = formatIdentifications.get(0).getFormat().getMimeType();
            }
            return new DirectoryComparableString(mimeType, node.allowsChildren());

        }
    },

    /** The PUID. */
    VERSION {
        /** {@inheritDoc} */
        @Override
        public Class<DirectoryComparableString> getColumnClass() {
            return DirectoryComparableString.class;
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "Version";
        }

        /** {@inheritDoc} */
        @Override
        public Object getValue(ProfileResourceNode node) {

            String version = null;

            List<FormatIdentification> formatIdentifications = node.getFormatIdentifications();
            if (!formatIdentifications.isEmpty()) {
                version = formatIdentifications.get(0).getFormat().getVersion();
            }
            return new DirectoryComparableString(version, node.allowsChildren());

        }
    },

    /** The job status. */
    JOB_STATUS {
        /** {@inheritDoc} */
        @Override
        public Class<DirectoryComparableString> getColumnClass() {
            return DirectoryComparableString.class;
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "Job status";
        }

        /** {@inheritDoc} */
        @Override
        public Object getValue(ProfileResourceNode node) {
            NodeStatus status = node.getMetaData().getNodeStatus();
            return new DirectoryComparableString(status == null ? "" : status.getStatus(), node.allowsChildren());
        }
    };

    /**
     * @return the Class that the column will represent.
     */
    public abstract Class<?> getColumnClass();

    /**
     * @return the column heading.
     */
    abstract String getName();

    /**
     * @param node
     *            the node whose value we want.
     * @return the value for the column.
     */
    abstract Object getValue(ProfileResourceNode node);

}
