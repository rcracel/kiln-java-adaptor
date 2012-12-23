package com.nevermindsoft.kiln;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 */
public class RemoteServiceAppender extends AppenderSkeleton {

    private PublisherThread processor;
    private Thread             thread;

    private String applicationName = "Unknown";
    private String environmentName = "Unknown";
    private String serverUrl       = "http://localhost:8080/polar/api/submit";
    private int    maxRequestItems = 200;
    private int    sleepTime       = 2000;

    public RemoteServiceAppender() {

    }

    @Override
    public void activateOptions() {
        processor = new PublisherThread( applicationName, environmentName, serverUrl, maxRequestItems, sleepTime );

        thread = new Thread( processor );

        thread.start();

        if ( thread.isAlive() ) {
            System.out.println(" -- " + this.getClass().getSimpleName() + " started....");
        } else {
            System.out.println(" -- Could not start " + this.getClass().getSimpleName());
        }
    }

    /**
     * Subclasses of <code>AppenderSkeleton</code> should implement this
     * method to perform actual logging. See also {@link #doAppend
     * AppenderSkeleton.doAppend} method.
     *
     * @since 0.9.0
     */
    @Override
    protected void append( LoggingEvent event ) {
        if ( !processor.queue( event ) ) {
            System.out.println(" -- Could not queue event");
        }
    }

    /**
     * Release any resources allocated within the appender such as file
     * handles, network connections, etc.
     * <p/>
     * <p>It is a programming error to append to a closed appender.
     *
     * @since 0.8.4
     */
    @Override
    public void close() {
        processor.flush();
        processor.stop();
    }

    /**
     * Configurators call this method to determine if the appender
     * requires a layout. If this method returns <code>true</code>,
     * meaning that layout is required, then the configurator will
     * configure an layout using the configuration information at its
     * disposal.  If this method returns <code>false</code>, meaning that
     * a layout is not required, then layout configuration will be
     * skipped even if there is available layout configuration
     * information at the disposal of the configurator..
     * <p/>
     * <p>In the rather exceptional case, where the appender
     * implementation admits a layout but can also work without it, then
     * the appender should return <code>true</code>.
     *
     * @since 0.8.4
     */
    @Override
    public boolean requiresLayout() {
        return false;
    }


    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setMaxRequestItems(int maxRequestItems) {
        this.maxRequestItems = maxRequestItems;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void setEnvironmentName( String environmentName ) {
        this.environmentName = environmentName;
    }
}
