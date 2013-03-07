package com.nevermindsoft.kiln.slf4j;

import com.nevermindsoft.kiln.internal.json.Event;
import com.nevermindsoft.kiln.server.Server;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

/**
 * User: rcracel
 * Date: 3/6/13
 * Time: 1:02 PM
 */
@RunWith(JUnit4.class)
public class Log4JTest {

    private static final Logger LOG = Logger.getLogger( Log4JTest.class );

    private static Server server;

    private static int port = 4444;
    private static long sleepTime = 2500;

    @BeforeClass
    public static void startUp() {
        System.out.println("Starting server...");

        server = new Server( port );
        server.start();
    }

    @Before
    public void before() {
        server.clear();
    }

    @Test
    public void testLog() throws Exception {
        LOG.info("test....");

        Thread.sleep( sleepTime );

        List<Event> events = server.getEvents();
        if ( events.size() != 1 ) {
            Assert.fail( "Expected only one event, but got " + events.size() );
        }

        Event event = events.get( 0 );
        Assert.assertNotNull( event.getModuleName() );
        Assert.assertNotNull( event.getLogLevel() );
        Assert.assertNotNull( event.getMessage() );
        Assert.assertNotNull( event.getTimestamp() );
        Assert.assertNotNull( event.getThreadName() );
        Assert.assertNotNull( event.getEnvironmentName() );
        Assert.assertNotNull( event.getPlatform() );
        Assert.assertNull( event.getStackTrace() );
        Assert.assertNotNull( event.getSource() );
        Assert.assertEquals( event.getMetadata().size(), 2 );
    }

    @Test
    public void testQueueOverflow() throws Exception {
        for ( int index = 0 ; index < 20 ; index++ ) {
            LOG.error( "Entry #" + index );
        }

        Thread.sleep( sleepTime );

        List<Event> events = server.getEvents();
        if ( events.size() != 10 ) {
            Assert.fail( "Expected 10 events, but got " + events.size() );
        }

    }

    @Test
    public void testBursts() throws Exception {
        for ( int burst = 0 ; burst < 3 ; burst++ ) {
            for ( int index = 0 ; index < 20 ; index++ ) {
                LOG.error( "Entry #" + index );
            }
            Thread.sleep( sleepTime );
        }

        List<Event> events = server.getEvents();
        if ( events.size() != 30 ) {
            Assert.fail( "Expected 30 events, but got " + events.size() );
        }
    }

    @Test
    public void testMaxStackTraceSize() throws Exception {
        LOG.error("This is an error", new RuntimeException("12345678901234567890"));
        Thread.sleep( sleepTime );

        List<Event> events = server.getEvents();
        if ( events.size() != 1 ) {
            Assert.fail( "Expected 1 event, but got " + events.size() );
        }

        Event event = events.get( 0 );
        Assert.assertNotNull( event.getModuleName() );
        Assert.assertNotNull( event.getLogLevel() );
        Assert.assertNotNull( event.getMessage() );
        Assert.assertNotNull( event.getTimestamp() );
        Assert.assertNotNull( event.getThreadName() );
        Assert.assertNotNull( event.getEnvironmentName() );
        Assert.assertNotNull( event.getPlatform() );
        Assert.assertEquals(  event.getStackTrace(), "java.lang.RuntimeException" );
        Assert.assertNotNull( event.getSource() );
        Assert.assertEquals(  event.getMetadata().size(), 2 );
    }

    @After
    public void after() throws Exception {
    }

    @AfterClass
    public static void tearDown() throws Exception {
        System.out.println("Stopping server...");

        server.stop();
        Thread.sleep( sleepTime );
    }

}
