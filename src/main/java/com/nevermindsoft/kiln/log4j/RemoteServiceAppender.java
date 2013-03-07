package com.nevermindsoft.kiln.log4j;

import com.nevermindsoft.kiln.internal.log.KilnConsoleInternalLogger;
import com.nevermindsoft.kiln.internal.log.KilnInternalLogger;
import com.nevermindsoft.kiln.internal.workers.PublisherThread;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This Log4j adaptor pushes messages to a pre-configured remote server
 *
 * User: Roger Cracel
 * Date: 12/20/12
 * Time: 3:56 PM
 */
public class RemoteServiceAppender extends AppenderSkeleton {

    private PublisherThread processor;
    private Thread          thread;

    private KilnInternalLogger internalLogger;
    private String             internalLoggerClassName;

    private Config             config;

    /**
     * Constructor
     */
    public RemoteServiceAppender() {
        this.config = new Config();
    }

    /**
     * This method is invoked after all properties are set on this appender. This is where the work actually begins, and
     * invoking this method will cause the remote server pushing thread to begging monitoring and pushing items to the
     * remote server.
     */
    @Override
    public void activateOptions() {
        if ( internalLogger == null && !StringUtils.isBlank( internalLoggerClassName ) ) {
            try {
                Class<?> loggerClass = Class.forName( internalLoggerClassName );
                if ( KilnInternalLogger.class.isAssignableFrom( loggerClass ) ) {
                    internalLogger = (KilnInternalLogger) loggerClass.newInstance();
                } else {
                    //- We don't yet have a logger to report to, so the only option is to use System.out
                    System.out.println("!!!!!!!!! Could not initialize internal logger with property from internalLoggerClassName, it does not implement KilnInternalLogger !!!!!!!!!");
                }
            } catch (Exception e) {
                //- We don't yet have a logger to report to, so the only option is to use System.out
                System.out.println("!!!!!!!!! Could not resolve class from name !!!!!!!!!");
                e.printStackTrace();
            }
        }

        if ( internalLogger == null ) {
            internalLogger = new KilnConsoleInternalLogger();
        }

        config.logger = internalLogger;

        processor = new PublisherThread( config );

        thread = new Thread( processor );

        thread.start();

        if ( thread.isAlive() ) {
            internalLogger.log( Level.INFO, this.getClass().getSimpleName() + " started....");
        } else {
            internalLogger.log( Level.INFO,  this.getClass().getSimpleName());
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
        event.getNDC();
        event.getThreadName();

        // Get a copy of this thread's MDC.
        event.getMDCCopy();
        event.getLocationInformation();

        event.getRenderedMessage();
        event.getThrowableStrRep();

        if ( !processor.queue( event ) ) {
            internalLogger.log( Level.ERROR, "Could not queue event ");
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

    public void setModuleName(String moduleName) {
        config.moduleName = moduleName;
    }

    public void setServerUrl(String serverUrl) {
        config.serverUrl = serverUrl;
    }

    public void setMaxRequestItems(int maxRequestItems) {
        config.maxRequestItems = maxRequestItems;
    }

    public void setSleepTime(int sleepTime) {
        config.sleepTime = sleepTime;
    }

    public void setEnvironmentName( String environmentName ) {
        config.environmentName = environmentName;
    }

    public void setApiKey( String apiKey ) {
        config.apiKey = apiKey;
    }

    public void setInternalLogger( KilnInternalLogger internalLogger ) {
        this.internalLogger = internalLogger;
    }

    public void setInternalLoggerClassName( String internalLoggerClassName ) {
        this.internalLoggerClassName = internalLoggerClassName;
    }

    public void setPlatform( String platform ) {
        config.platform = platform;
    }

    public void setMaxQueueSize( int maxQueueSize ) {
        config.maxQueueSize = maxQueueSize;
    }

    public static class Config {

        private KilnInternalLogger logger;

        private String moduleName      = "Unknown";
        private String environmentName = "Unknown";
        private String apiKey          = "xxx-xxx-xxx";
        private String serverUrl       = "http://localhost:8080/api/events/publish";
        private int    maxRequestItems = 200;
        private int    maxQueueSize    = 1000;
        private int    sleepTime       = 2000;
        private String platform = "Java";


        public String getModuleName() {
            return moduleName;
        }

        public String getEnvironmentName() {
            return environmentName;
        }

        public String getApiKey() {
            return apiKey;
        }

        public String getServerUrl() {
            return serverUrl;
        }

        public int getMaxRequestItems() {
            return maxRequestItems;
        }

        public int getSleepTime() {
            return sleepTime;
        }

        public String getPlatform() {
            return platform;
        }

        public int getMaxQueueSize() {
            return maxQueueSize;
        }

        public KilnInternalLogger getLogger() {
            return logger;
        }
    }
}
