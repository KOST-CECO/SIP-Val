/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.submitter;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.gov.nationalarchives.droid.core.interfaces.AsynchDroid;
import uk.gov.nationalarchives.droid.core.interfaces.DroidCore;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationErrorType;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationException;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.ResultHandler;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ArchiveFormat;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ArchiveFormatResolver;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ArchiveHandler;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ArchiveHandlerFactory;
import uk.gov.nationalarchives.droid.core.interfaces.control.PauseBefore;

/**
 * Acts as a DroidCore proxy by keeping track of in-flight identification
 * requests. Requests are removed from the queue when the droid ID task finishes
 * All requests should come through this pipeline.
 * @author rflitcroft
 * 
 *
 */
public class SubmissionGateway implements AsynchDroid {
    
    private final Log log = LogFactory.getLog(getClass());

    private DroidCore droidCore;
    private ResultHandler resultHandler;
    private ExecutorService executorService;
    private boolean processArchives;
    private ArchiveFormatResolver archiveFormatResolver;
    private ArchiveHandlerFactory archiveHandlerFactory;
    
    private SubmissionQueue submissionQueue;
    private final JobCounter jobCounter = new JobCounter();
    private ReplaySubmitter replaySubmitter;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @PauseBefore
    public Future<IdentificationResultCollection> submit(final IdentificationRequest request) {
        jobCounter.increment();
        
        Callable<IdentificationResultCollection> callable = new Callable<IdentificationResultCollection>() {
            @Override
            public IdentificationResultCollection call() {
                IdentificationResultCollection results = droidCore.submit(request);
                return results;
            }
        };
        
        FutureTask<IdentificationResultCollection> task = new SubmissionFutureTask(callable, request);
        executorService.submit(task);
        return task;
    }
    
    private void closeRequest(IdentificationRequest request) {
        try {
            request.close();
        } catch (IOException e) {
            log.warn(String.format("Error closing request [%s]", request.getIdentifier().getUri()), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void replay() {
        replaySubmitter.replay();
    }
    
    private final class SubmissionFutureTask extends FutureTask<IdentificationResultCollection> {

        private IdentificationRequest request;
        
        SubmissionFutureTask(Callable<IdentificationResultCollection> callable, IdentificationRequest request) {
            super(callable);
            this.request = request;
        }

        @Override
        protected void done() {
            boolean jobCountDecremented = false;
            try {
                IdentificationResultCollection results = get();
                if (processArchives && archiveFormatResolver != null) {
                    ArchiveFormat archiveFormat = getArchiveFormat(results);
                    if (archiveFormat != null) {
                        results.setArchive(true);
                        long nodeId = resultHandler.handle(results);
                        jobCounter.incrementPostProcess();
                        request.getIdentifier().setNodeId(nodeId);
                        if (request.getIdentifier().getAncestorId() == null) {
                            request.getIdentifier().setAncestorId(nodeId);
                        }
                        submissionQueue.add(request.getIdentifier());
                        jobCounter.decrement();
                        jobCountDecremented = true;
                        try {
                            handleArchive(archiveFormat, request);
                            // CHECKSTYLE:OFF
                        } catch (Exception e) {
                            // CHECKSTYLE:ON
                            log.error(e.getMessage(), e);
                            resultHandler.handleError(new IdentificationException(
                                    request, IdentificationErrorType.OTHER, e));
                        } finally {
                            submissionQueue.remove(request.getIdentifier());
                            jobCounter.decrementPostProcess();
                        }
                    } else {
                        request.getIdentifier().setNodeId(resultHandler.handle(results));
                    }
                } else {
                    request.getIdentifier().setNodeId(resultHandler.handle(results));
                }
            } catch (ExecutionException e) {
                final Throwable cause = e.getCause();
                log.error(cause.getStackTrace(), cause);
                resultHandler.handleError(new IdentificationException(
                        request, IdentificationErrorType.OTHER, cause));
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            } finally {
                closeRequest(request);
                if (!jobCountDecremented) {
                    jobCounter.decrement();
                }
            }
        }
    }
    
    /**
     * @param nodeId
     * @param format
     * @throws IOException 
     */
    private void handleArchive(final ArchiveFormat format, IdentificationRequest request) throws IOException {
        ArchiveHandler handler = archiveHandlerFactory.getHandler(format);
        handler.handle(request);
    }
    
    /**
     * @param results
     * @param nodeId
     */
    private ArchiveFormat getArchiveFormat(IdentificationResultCollection results) {
        for (IdentificationResult result : results.getResults()) {
            final ArchiveFormat format = archiveFormatResolver.forPuid(result.getPuid());
            if (format != null) {
                return format;
            }
        }
        
        return null;
    }
    
    /**
     * Waits until all in-process jobs have completed.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    @Override
    public void awaitIdle() throws InterruptedException {
        jobCounter.awaitIdle();
    }
    
    /**
     * Waits until the job queue is empty AND all sub-tasks (archives etc.) have finished.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    @Override
    public void awaitFinished() throws InterruptedException {
        jobCounter.awaitFinished();
    }

    /**
     * @param archiveFormatResolver the archiveFormatResolver to set
     */
    public void setArchiveFormatResolver(ArchiveFormatResolver archiveFormatResolver) {
        this.archiveFormatResolver = archiveFormatResolver;
    }
    
    /**
     * @param archiveHandlerFactory the archiveHandlerFactory to set
     */
    public void setArchiveHandlerFactory(ArchiveHandlerFactory archiveHandlerFactory) {
        this.archiveHandlerFactory = archiveHandlerFactory;
    }
    
    /**
     * @param droidCore the droidCore to set
     */
    public void setDroidCore(DroidCore droidCore) {
        this.droidCore = droidCore;
    }
    
    /**
     * @param executorService the executorService to set
     */
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
    
    /**
     * @param processArchives the processArchives to set
     */
    public void setProcessArchives(boolean processArchives) {
        this.processArchives = processArchives;
    }
    
    /**
     * @param resultHandler the resultHandler to set
     */
    public void setResultHandler(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }
    
    /**
     * @param submissionQueue the submissionQueue to set
     */
    public void setSubmissionQueue(SubmissionQueue submissionQueue) {
        this.submissionQueue = submissionQueue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void save() {
        submissionQueue.save();
    }
    
    /**
     * @param replaySubmitter the replaySubmitter to set
     */
    public void setReplaySubmitter(ReplaySubmitter replaySubmitter) {
        this.replaySubmitter = replaySubmitter;
    }
    
    /**
     * Shuts down the executor service.
     */
    public void close() {
        executorService.shutdownNow();
    }
    
}
