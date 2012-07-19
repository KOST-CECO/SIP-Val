/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile.throttle;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang.time.StopWatch;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.nationalarchives.droid.RuntimeConfig;

/**
 * @author rflitcroft
 *
 */
public class SimpleSubmissionThrottleTest {

    private SimpleSubmissionThrottle throttle;
    
    @BeforeClass
    public static void setupEnv() {
        RuntimeConfig.configureRuntimeEnvironment();
    }
    
    @AfterClass
    public static void tearDownEnv() {
        System.clearProperty(RuntimeConfig.DROID_WORK);
    }

    @Before
    public void setup() {
        throttle = new SimpleSubmissionThrottle();
    }
    
    @Test
    public void testThrottlingWith100msWait() throws Exception {
        throttle.setWaitMilliseconds(100);
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        throttle.apply();
        stopWatch.stop();
        assertThat((double) stopWatch.getTime(), closeTo(100L, 12L));
        
    }

    @Test
    public void testThrottlingWithNoWait() throws Exception {
        throttle.setWaitMilliseconds(0);
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        throttle.apply();
        stopWatch.stop();
        assertThat((double) stopWatch.getTime(), closeTo(0L, 5L));
    }

    @Test
    public void testThrottlingWhenCallingThreadIsInterrupted() throws InterruptedException {
        throttle.setWaitMilliseconds(100);
        
        final StopWatch stopWatch = new StopWatch();
        
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    throttle.apply();
                } catch (InterruptedException e) {
                }
            }
        };
        stopWatch.start();
        t.start();
        
        Thread.sleep(50);
        t.interrupt();
        stopWatch.stop();
        assertThat(stopWatch.getTime(), lessThan(100L));
        
    }
}
