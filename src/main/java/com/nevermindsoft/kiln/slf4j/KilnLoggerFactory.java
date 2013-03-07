package com.nevermindsoft.kiln.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * User: rcracel
 * Date: 2/5/13
 * Time: 11:57 PM
 */
public class KilnLoggerFactory implements ILoggerFactory {

    private Map<String, Logger> loggerMap;

    public KilnLoggerFactory() {
        loggerMap = new HashMap<String, Logger>();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slf4j.ILoggerFactory#getLogger(java.lang.String)
     */
    public Logger getLogger(String name) {
        Logger slf4jLogger = null;

        // protect against concurrent access of loggerMap
        synchronized (this) {
            slf4jLogger = loggerMap.get(name);
            if (slf4jLogger == null) {
                slf4jLogger = new RemoteServiceAppender();

                //- Somehow configure the remote appender here

                loggerMap.put(name, slf4jLogger);
            }
        }

        return slf4jLogger;
    }

}
