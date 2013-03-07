package com.nevermindsoft.kiln.internal.workers;

import com.nevermindsoft.kiln.internal.log.KilnInternalLogger;
import com.nevermindsoft.kiln.internal.publishers.KilnPublisher;
import com.nevermindsoft.kiln.log4j.RemoteServiceAppender.Config;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The publisher thread pushes log events to the remote server on a predefined interval
 *
 * User: Roger Cracel
 * Date: 12/27/12
 * Time: 11:42 AM
 */
public class PublisherThread implements Runnable {

    private Queue<LoggingEvent> eventQueue;

    private boolean shouldRun = true;

    private KilnPublisher publisher;

    private Config config;

    /**
     * Constructor for the published thread.
     *
     * @param config configuration for the appender
     */
    public PublisherThread( Config config ) {
        this.config     = config;
        this.eventQueue = new LinkedBlockingQueue<LoggingEvent>( config.getMaxQueueSize() );
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

        config.getLogger().log( Level.WARN, "Log Publishing Thread has started..." );

        try {
            while ( shouldRun ) {
                List<LoggingEvent> localQueue = new ArrayList<LoggingEvent>();

                for ( int index = 0 ; index < config.getMaxRequestItems() ; index++ ) {
                    LoggingEvent event = eventQueue.poll();
                    if ( event != null ) {
                        localQueue.add( event );
                    }
                }

                if ( !localQueue.isEmpty() ) {
                    //config.getLogger().log(Level.WARN, "Processing " + localQueue.size() + " events");
                    pushItems( localQueue );
                } else {
                    //config.getLogger().log(Level.WARN, "Queue is empty");
                }

                if ( localQueue.isEmpty() )
                    Thread.sleep( config.getSleepTime() );
            }
        } catch ( InterruptedException e ) {
            config.getLogger().log(Level.ERROR, "The log published has be halted due to a InterruptedException");
        } catch ( Exception e ) {
            config.getLogger().log( Level.ERROR, "Unexpected error, halting thread: " + e.getMessage(), e );
        }

        config.getLogger().log( Level.WARN, "Log Publishing Thread has stopped" );
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

            if ( localQueue.size() >= config.getMaxRequestItems() ) {
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
                    publisher = new KilnPublisher( config );
                }
            }
        }
        return publisher;
    }

}
