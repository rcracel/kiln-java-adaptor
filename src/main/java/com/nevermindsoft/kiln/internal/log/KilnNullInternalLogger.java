package com.nevermindsoft.kiln.internal.log;

import org.apache.log4j.Level;

/**
 * Created with IntelliJ IDEA.
 * User: rcracel
 * Date: 1/3/13
 * Time: 9:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class KilnNullInternalLogger implements KilnInternalLogger {

    /**
     * Logs a message and Throwable at a given log level
     *
     * @param level   the log level (WARN, INFO, ERROR, ...)
     * @param message the message to log
     * @param t       the exception to log along the message
     */
    @Override
    public void log(Level level, String message, Throwable t) {
        //- Do nothing
    }

    /**
     * Log a message at a given log level
     *
     * @param level   the log level (WARN, INFO, ERROR, ...)
     * @param message the message to log
     */
    @Override
    public void log(Level level, String message) {
        //- Do nothing
    }
}
