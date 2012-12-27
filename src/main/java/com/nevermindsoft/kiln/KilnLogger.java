package com.nevermindsoft.kiln;

import org.apache.log4j.Level;

/**
 * An internal logger that just dumps messages to the console
 *
 * User: Roger Cracel
 * Date: 12/27/12
 * Time: 11:42 AM
 */
public class KilnLogger {

    /**
     * Internal method for logging messages to the console.
     *
     * @param level the log level (WARN, INFO, ERROR, ...)
     * @param message the message to log
     * @param t the exception to log along the message
     */
    public static void log( Level level, String message, Throwable t ) {
        System.out.print( String.format( " -- %s -- %s", level.toString(), message ) );

        if ( t != null ) {
            t.printStackTrace();
        }
    }

    /**
     * Internal method for logging messages to the console.
     *
     * @param level the log level (WARN, INFO, ERROR, ...)
     * @param message the message to log
     */
    public static void log( Level level, String message ) {
        KilnLogger.log( level, message, null );
    }

}