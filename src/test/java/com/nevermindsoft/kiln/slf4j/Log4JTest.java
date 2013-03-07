package com.nevermindsoft.kiln.slf4j;

import com.nevermindsoft.kiln.internal.json.Event;
import com.nevermindsoft.kiln.server.Server;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * User: rcracel
 * Date: 3/6/13
 * Time: 1:02 PM
 */
@RunWith(JUnit4.class)
public class Log4JTest {

    private static final Logger LOG = Logger.getLogger( Log4JTest.class );

    @BeforeClass
    public static void startUp() {
        System.out.println("Start Up");
    }

    @Before
    public void before() {
        Server.start();
    }

    @Test
    public void testLog() throws Exception {
        LOG.info("test....");

        Thread.sleep( 6000 );

        List<Event> events = Server.getEvents();
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
//        Assert.assertNotNull( event.getStackTrace() );
        Assert.assertNotNull( event.getSource() );
        Assert.assertEquals( event.getMetadata().size(), 2 );
    }

    @After
    public void after() {
        Server.stop();

        List<Event> events = Server.getEvents();

        System.out.println( events );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        System.out.println("Tear Down");

        Thread.sleep( 20000 );
    }

}
