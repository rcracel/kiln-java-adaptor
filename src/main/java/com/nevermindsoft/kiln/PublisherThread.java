package com.nevermindsoft.kiln;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public class PublisherThread implements Runnable {

    private Queue<LoggingEvent> eventQueue = new LinkedBlockingQueue<LoggingEvent>();

    private boolean shouldRun = true;

    private String moduleName;

    private String apiKey;

    private String environmentName;

    private String serverUrl;

    private int maxItems;

    private int sleepTime;

    private KilnPublisher publisher;

    /**
     * Constructor for the publisher thread.
     *
     * @param moduleName the name of the module to be reported to the remote server
     * @param apiKey the api key to be reported to the remote server
     * @param environmentName the environment name to be reported to the remote server
     * @param serverUrl the server url of the remote server
     * @param maxItems the maximum number of items to send on a single request
     * @param sleepTime the time to wait between pushes
     */
    public PublisherThread(String moduleName, String apiKey, String environmentName, String serverUrl, int maxItems, int sleepTime) {
        this.moduleName = moduleName;
        this.environmentName = environmentName;
        this.serverUrl = serverUrl;
        this.maxItems = maxItems;
        this.sleepTime = sleepTime;
        this.apiKey = apiKey;
    }

    /**
     * Queues a LoggingEvent to be published to the remote server
     *
     * @param event the event to queue
     *
     * @return [true|false] depending on the whether the item was successfully queued
     */
    public boolean queue( LoggingEvent event ) {
        return eventQueue.offer( event );
    }

    /**
     * @see Thread#run()
     */
    @Override
    public void run() {

        try {
            while ( shouldRun ) {
                List<LoggingEvent> localQueue = new ArrayList<LoggingEvent>();

                for ( int index = 0 ; index < maxItems ; index++ ) {
                    LoggingEvent event = eventQueue.poll();
                    if ( event != null ) {
                        localQueue.add( event );
                    }
                }

                if ( !localQueue.isEmpty() ) {
                    pushItems( localQueue );
                } else {
                    //KilnLogger.log( Level.WARN, "Queue is empty" );
                }

                if ( localQueue.isEmpty() )
                    Thread.sleep( sleepTime );
            }
        } catch ( InterruptedException e ) {
            KilnLogger.log( Level.ERROR, "The log published has be halted due to a InterruptedException");
        } catch ( Exception e ) {
            KilnLogger.log( Level.ERROR, "Unexpected error, halting thread: " + e.getMessage(), e );
        }

        KilnLogger.log( Level.WARN, "Log Published Thread has stopped" );
    }

    /**
     * Pushes a collection of events to the server
     *
     * @param events the collection of LoggingEvent to serialize to the log server
     */
    private void pushItems( List<LoggingEvent> events ) {
        getPublisher().pushItems(events);
    }

    /**
     * Forces all pending messages to be sent to the server. This should be called before stopping or shutting down to avoid
     * dropping uncommitted log messages.
     */
    public void flush() {
        List<LoggingEvent> localQueue = new ArrayList<LoggingEvent>();

        while ( !eventQueue.isEmpty() ) {
            LoggingEvent event = eventQueue.poll();
            if ( event != null ) {
                localQueue.add( event );
            }

            if ( localQueue.size() >= maxItems ) {
                getPublisher().pushItems(localQueue);
                localQueue.clear();
            }
        }


    }

    public void stop() {
        shouldRun = false;
    }

    /**
     * Returns a published instance to be used to push events to a remote server
     *
     * @return a publisher that extends KilnPublisher
     */
    protected KilnPublisher getPublisher() {
        if ( publisher == null ) {
            synchronized (this) {
                if ( publisher == null ) {
                    publisher = new KilnPublisher( getServerUrl(), getApiKey(), getModuleName(), getEnvironmentName() );
                }
            }
        }
        return publisher;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public String getServerUrl() {
        return serverUrl;
    }
}
