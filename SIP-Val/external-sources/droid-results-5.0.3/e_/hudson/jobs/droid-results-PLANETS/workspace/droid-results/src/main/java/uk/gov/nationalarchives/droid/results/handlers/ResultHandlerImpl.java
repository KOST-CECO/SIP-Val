/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.results.handlers;

import java.net.URI;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.nationalarchives.droid.core.interfaces.IdentificationException;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationMethod;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.NodeStatus;
import uk.gov.nationalarchives.droid.core.interfaces.ResourceType;
import uk.gov.nationalarchives.droid.core.interfaces.ResultHandler;
import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;
import uk.gov.nationalarchives.droid.profile.FormatIdentification;
import uk.gov.nationalarchives.droid.profile.IdentificationJob;
import uk.gov.nationalarchives.droid.profile.JobStatus;
import uk.gov.nationalarchives.droid.profile.NodeMetaData;
import uk.gov.nationalarchives.droid.profile.ProfileResourceNode;
import uk.gov.nationalarchives.droid.profile.referencedata.Format;

/**
 * @author rflitcroft
 * 
 */
public class ResultHandlerImpl implements ResultHandler {
 
    private Log log = LogFactory.getLog(getClass());

    private ResultHandlerDao resultHandlerDao;
    private ProgressMonitor progressMonitor;

    /**
     * Saves the incoming result to the database.
     * 
     * @param results
     *            the results to be handled.
     * @return long
     *            node id.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public long handle(IdentificationResultCollection results) {
        
        log.info(String.format("handling result for job [%s]", results.getUri()));
        
        ProfileResourceNode node = new ProfileResourceNode(results.getUri());
        
        RequestMetaData requestMetaData = results.getRequestMetaData();
        
        NodeMetaData metaData = new NodeMetaData();
        metaData.setLastModified(requestMetaData.getTime());
        metaData.setSize(results.getFileLength());
        metaData.setName(requestMetaData.getName());
        metaData.setExtension(FilenameUtils.getExtension(requestMetaData.getName()));
        metaData.setResourceType(results.isArchive() ? ResourceType.CONTAINER : ResourceType.FILE);
        
        metaData.setNodeStatus(NodeStatus.forResultSize(results.getResults().size()));

        node.setMetaData(metaData);
        resultHandlerDao.save(node, results.getCorrelationId());

        IdentificationJob job = new IdentificationJob();
        job.setFinished(new Date());
        job.setStatus(JobStatus.COMPLETE);
        node.setJob(job);

        if (results.getResults().isEmpty()) {
            FormatIdentification formatIdentification = new FormatIdentification();
            formatIdentification.setFormat(Format.NULL);
            node.addFormatIdentification(formatIdentification);
        } else {
            for (IdentificationResult result : results.getResults()) {
                node.getMetaData().setIdentificationMethod(result.getMethod());

                FormatIdentification formatIdentification = new FormatIdentification();
                Format format = resultHandlerDao.loadFormat(result.getPuid());
                formatIdentification.setFormat(format);

                log.debug(String.format("Handling ID puid[%s]; uri[%s]", result.getPuid(), results.getUri()));

                node.addFormatIdentification(formatIdentification);
            }
        }

        progressMonitor.stopJob(node);

        return node.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleError(IdentificationException e) {
        final IdentificationRequest request = e.getRequest();
        URI uri = request.getIdentifier().getUri();
        log.warn(String.format("handling error for job [%s]", uri));
        
        final Long nodeId = request.getIdentifier().getNodeId();
        ProfileResourceNode node;
        if (nodeId != null) {
            node = resultHandlerDao.loadNode(nodeId);
            node.getMetaData().setNodeStatus(NodeStatus.Error);
            // Need to initialise the collection eagerly...
            node.getFormatIdentifications().size();
        } else {
            node = new ProfileResourceNode(uri);
    
            IdentificationJob job = new IdentificationJob();
            job.setFinished(new Date());
            job.setStatus(JobStatus.ERROR);
            job.setErrorMessage(e.getMessage());
            node.setJob(job);
            final NodeMetaData metaData = node.getMetaData();
            metaData.setNodeStatus(NodeStatus.Error);
            metaData.setResourceType(ResourceType.FILE);
            
            RequestMetaData requestMetaData = request.getRequestMetaData();
            
            metaData.setName(requestMetaData.getName());
            metaData.setSize(requestMetaData.getSize());
            metaData.setExtension(request.getExtension());
            metaData.setLastModified(request.getRequestMetaData().getTime());
            
            FormatIdentification formatIdentification = new FormatIdentification();
            formatIdentification.setFormat(Format.NULL);
            node.addFormatIdentification(formatIdentification);
    
            resultHandlerDao.save(node, request.getIdentifier().getParentId());
        }
        
        progressMonitor.stopJob(node);
    }
    
    

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public long handleDirectory(IdentificationResult result, Long parentId, boolean restricted) {
        final URI uri = result.getIdentifier().getUri();
        log.debug(String.format("handling directory [%s]", uri));
        ProfileResourceNode node = new ProfileResourceNode(uri);

        RequestMetaData requestMetaData = result.getMetaData();
        
        NodeMetaData metaData = new NodeMetaData();
        metaData.setName(requestMetaData.getName());
        metaData.setSize(null);
        metaData.setLastModified(requestMetaData.getTime());
        metaData.setIdentificationMethod(IdentificationMethod.OPERATING_SYSTEM);
        metaData.setNodeStatus(restricted ? NodeStatus.Error : NodeStatus.Identified);
        metaData.setResourceType(ResourceType.FOLDER);
        node.setMetaData(metaData);

        resultHandlerDao.save(node, parentId);
        IdentificationJob job = new IdentificationJob();
        job.setFinished(new Date());
        job.setStatus(JobStatus.COMPLETE);
        node.setJob(job);

        FormatIdentification formatIdentification = new FormatIdentification();
        formatIdentification.setFormat(Format.NULL);
        node.addFormatIdentification(formatIdentification);

        progressMonitor.stopJob(node);
        return node.getId();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void addProblem(Long nodeId) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param resultHandlerDao
     *            the resultHandlerDao to set
     */
    public void setResultHandlerDao(ResultHandlerDao resultHandlerDao) {
        this.resultHandlerDao = resultHandlerDao;
    }

    /**
     * @param progressMonitor
     *            the progressMonitor to set
     */
    public void setProgressMonitor(ProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCascade(Long nodeId) {
        resultHandlerDao.deleteNode(nodeId);
    }
}
