package com.nevermindsoft.kiln.internal.log;

import org.apache.log4j.Level;

/**
 * Interface for internal loggers
 *
 * User: rcracel
 * Date: 1/3/13
 * Time: 9:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class KilnConsoleInternalLogger implements KilnInternalLogger {

    private Level minLevel = Level.ALL;

    /**
     * Default constructor, will log all events
     */
    public KilnConsoleInternalLogger() {
    }

    /**
     * Default constructor, will log all level of the given level or higher
     * @param minLevel the minimum level to log
     */
    public KilnConsoleInternalLogger(Level minLevel) {
        this.minLevel = minLevel;
    }

    /**
     * Internal method for logging messages to the console.
     *
     * @param level the log level (WARN, INFO, ERROR, ...)
     * @param message the message to log
     * @param t the exception to log along the message
     */
    public void log( Level level, String message, Throwable t ) {
        if ( level.isGreaterOrEqual(minLevel) ) {
            System.out.print( String.format( " -- %s -- %s", level.toString(), message ) );

            if ( t != null ) {
                t.printStackTrace();
            }
        }
    }

    /**
     * Internal method for logging messages to the console.
     *
     * @param level the log level (WARN, INFO, ERROR, ...)
     * @param message the message to log
     */
    public void log( Level level, String message ) {
        if ( level.isGreaterOrEqual(minLevel) ) {
            log(level, message, null);
        }
    }

}
