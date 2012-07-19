/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.nationalarchives.droid.RuntimeConfig;
import uk.gov.nationalarchives.droid.results.handlers.ProgressObserver;
import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;
/**
 * @author rflitcroft
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring-profile.xml" })
public class ProfileManagerIntegrationTest {

    private SignatureFileInfo signatureFileInfo;
    
    @Autowired
    private ProfileManager profileManager;
    
    @BeforeClass
    public static void setupFiles() throws IOException {
        RuntimeConfig.configureRuntimeEnvironment();
        new File("integration-test-files").mkdir();
        new File("integration-test-files/file1").createNewFile();
    }
    
    @AfterClass
    public static void tearDown() throws IOException {
        FileUtils.forceDelete(new File("integration-test-files"));
    }
    
    @Before
    public void setup() {
        signatureFileInfo = new SignatureFileInfo(26, false);
        signatureFileInfo.setFile(new File("test_sig_files/DROID_SignatureFile_V26.xml"));
    }
    
    @Test
    public void testStartProfileSpecPersistsAJobForEachFileResourceNode() throws Exception {
        try {
            FileUtils.forceDelete(new File("profiles/integration-test"));
        } catch (IOException e) { }
        
        File testFile = new File("test_sig_files/sample.pdf");
        FileProfileResource fileResource = new FileProfileResource(testFile);
        assertNotNull(profileManager);
        ProfileSpec profileSpec = new ProfileSpec();
        profileSpec.addResource(fileResource);
        
        ProfileResultObserver myObserver = mock(ProfileResultObserver.class);
        ProfileInstance profile = profileManager.createProfile(signatureFileInfo);
        profileManager.updateProfileSpec(profile.getUuid(), profileSpec);
        profileManager.setResultsObserver(profile.getUuid(), myObserver);
        
        Collection<ProfileResourceNode> rootNodes = profileManager.findRootNodes(profile.getUuid());
        assertEquals(1, rootNodes.size());
        assertEquals(testFile.toURI(), rootNodes.iterator().next().getUri());
        
        ProgressObserver progressObserver = mock(ProgressObserver.class);
        profileManager.setProgressObserver(profile.getUuid(), progressObserver);
        profile.changeState(ProfileState.VIRGIN);

        Future<Void> submission = profileManager.start(profile.getUuid());
        submission.get();
        
        // Assert we got our result
        ArgumentCaptor<ProfileResourceNode> nodeCaptor = ArgumentCaptor.forClass(ProfileResourceNode.class);
        verify(myObserver).onResult(nodeCaptor.capture());
        URI capturedUri = nodeCaptor.getValue().getUri();
        
        assertEquals(testFile.toURI(), capturedUri);
        
        // Now assert that file/1 is in the database
        List<ProfileResourceNode> nodes = profileManager.findProfileResourceNodeAndImmediateChildren(
                profile.getUuid(), null);
        
        assertEquals(1, nodes.size());
        ProfileResourceNode node = nodes.get(0);
        assertEquals(testFile.toURI(), node.getUri());
        assertEquals(JobStatus.COMPLETE, node.getJob().getStatus());
        assertEquals(testFile.length(), node.getMetaData().getSize().longValue());
        assertEquals(new Date(testFile.lastModified()), node.getMetaData().getLastModifiedDate());

        // check the progress listener was invoked properly.
        verify(progressObserver, times(2)).onProgress(anyInt());
        verify(progressObserver).onProgress(100);
        
    }

    @Test
    public void testStartProfileSpecPersistsJobsForEachFileInANonRecursiveDirResourceNode() throws Exception {
        try {
            FileUtils.forceDelete(new File("profiles/integration-test2"));
        } catch (IOException e) { }
        
        File testFile = new File("test_sig_files");
        FileProfileResource fileResource = new DirectoryProfileResource(testFile, false);
        assertNotNull(profileManager);
        ProfileSpec profileSpec = new ProfileSpec();
        profileSpec.addResource(fileResource);
        
        ProfileResultObserver myObserver = mock(ProfileResultObserver.class);
        ProfileInstance profile = profileManager.createProfile(signatureFileInfo);
        profile.changeState(ProfileState.VIRGIN);
        
        String profileId = profile.getUuid();
        profileManager.updateProfileSpec(profileId, profileSpec);
        profileManager.setResultsObserver(profileId, myObserver);
        
        ProgressObserver progressObserver = mock(ProgressObserver.class);
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) {
//                System.err.println("Progress = " + invocation.getArguments()[0]);
//                return null;
//            }
//        }).when(progressObserver).onProgress(anyInt());
        profileManager.setProgressObserver(profileId, progressObserver);

        Future<Void> submission = profileManager.start(profileId);
        submission.get();
        
        // Assert we got our result notifications
        ArgumentCaptor<ProfileResourceNode> nodeCaptor = ArgumentCaptor.forClass(ProfileResourceNode.class);
        List<ProfileResourceNode> capturedUris = nodeCaptor.getAllValues();
        verify(myObserver, atLeast(8)).onResult(nodeCaptor.capture());
        
        ProfileResourceNode testFileNode = capturedUris.get(0);
        
        Collections.sort(capturedUris, new Comparator<ProfileResourceNode>() {
            @Override
            public int compare(ProfileResourceNode o1, ProfileResourceNode o2) {
                return o1.getUri().compareTo(o2.getUri());
            }
        });
        
        capturedUris.contains(new File("test_sig_files").toURI());
        capturedUris.contains(new File("test_sig_files/DROID 5  Architecture.doc").toURI());
        capturedUris.contains(new File("test_sig_files/DROID_SignatureFile_V26.xml").toURI());
        capturedUris.contains(new File("test_sig_files/DROID_SignatureFile_V16.xml").toURI());
        capturedUris.contains(new File("test_sig_files/malformed.xml").toURI());
        capturedUris.contains(new File("test_sig_files/not_valid_sig_file.xml").toURI());
        capturedUris.contains(new File("test_sig_files/sample.pdf").toURI());
        capturedUris.contains(new File("test_sig_files/persistence.jar").toURI());
        
        // Now assert that file/1 is in the database
        List<ProfileResourceNode> nodes = profileManager.findProfileResourceNodeAndImmediateChildren(
                profile.getUuid(), testFileNode.getId());
        
        assertEquals(8, nodes.size());
        
        ProfileResourceNode samplePdf = null;
        File samplePdfFile = new File("test_sig_files/sample.pdf");
        for (ProfileResourceNode childNode : nodes) {
            if (childNode.getUri().equals(samplePdfFile.toURI())) {
                samplePdf = childNode;
                break;
            }
        }
        
        assertEquals(samplePdfFile.toURI(), samplePdf.getUri());
        assertEquals(JobStatus.COMPLETE, samplePdf.getJob().getStatus());
        assertEquals(samplePdfFile.length(), samplePdf.getMetaData().getSize().longValue());
        assertEquals(new Date(samplePdfFile.lastModified()), samplePdf.getMetaData().getLastModifiedDate());
        
        // check the progress listener was invoked properly.
        verify(progressObserver, atLeast(8)).onProgress(anyInt());
        verify(progressObserver).onProgress(100);
        

    }

}
