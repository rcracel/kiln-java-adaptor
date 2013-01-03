package com.nevermindsoft.kiln.internal.log;

import org.apache.log4j.Level;

/**
 * An internal logger that just dumps messages to the console
 *
 * User: Roger Cracel
 * Date: 12/27/12
 * Time: 11:42 AM
 */
public interface KilnInternalLogger {

    /**
     * Logs a message and Throwable at a given log level
     *
     * @param level the log level (WARN, INFO, ERROR, ...)
     * @param message the message to log
     * @param t the exception to log along the message
     */
    public void log( Level level, String message, Throwable t );

    /**
     * Log a message at a given log level
     *
     * @param level the log level (WARN, INFO, ERROR, ...)
     * @param message the message to log
     */
    public void log( Level level, String message );

}