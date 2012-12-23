package com.nevermindsoft.kiln;

import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
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

    /**
     * Constructor for the publisher thread.
     *
     * @param moduleName
     * @param apiKey
     * @param environmentName
     * @param serverUrl
     * @param maxItems
     * @param sleepTime
     */
    public PublisherThread(String moduleName, String apiKey, String environmentName, String serverUrl, int maxItems, int sleepTime) {
        this.moduleName = moduleName;
        this.environmentName = environmentName;
        this.serverUrl = serverUrl;
        this.maxItems = maxItems;
        this.sleepTime = sleepTime;
        this.apiKey = apiKey;
    }

    public boolean queue( LoggingEvent event ) {
        return eventQueue.offer( event );
    }

    /**
     *
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
                    //System.out.println( " -- Queue is empty" );
                }

                if ( localQueue.isEmpty() )
                    Thread.sleep( sleepTime );
            }
        } catch ( InterruptedException e ) {
            System.out.println(" -- The log published has be halted due to a InterruptedException");
        } catch ( Exception e ) {
            System.out.println(" -- Unexpected error, halting thread: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println(" -- Log Published Thread has stopped");
    }

    /**
     * Pushes a collection of events to the server
     *
     * @param events the collection of LoggingEvent to serialize to the log server
     */
    private void pushItems( List<LoggingEvent> events ) {
        new KilnPublisher( serverUrl, apiKey, moduleName, environmentName ).pushItems( events );
    }

    /**
     * Forces all pending messages to be sent to the server
     */
    public void flush() {
        List<LoggingEvent> localQueue = new ArrayList<LoggingEvent>();
        KilnPublisher publisher = new KilnPublisher( serverUrl, apiKey, moduleName, environmentName );

        while ( !eventQueue.isEmpty() ) {
            LoggingEvent event = eventQueue.poll();
            if ( event != null ) {
                localQueue.add( event );
            }

            if ( localQueue.size() >= maxItems ) {
                publisher.pushItems( localQueue );
                localQueue.clear();
            }
        }


    }

    public void stop() {
        shouldRun = false;
    }
}
