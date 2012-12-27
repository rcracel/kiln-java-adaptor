package com.nevermindsoft.kiln;

import org.apache.log4j.Level;

/**
 * Created with IntelliJ IDEA.
 * User: rcracel
 * Date: 12/27/12
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class KilnLogger {

    public static void log( Level level, String message, Throwable t ) {
        System.out.format(" -- %s -- %s", level.toString(), message );

        if ( t != null ) {
            t.printStackTrace();
        }
    }

    public static void log( Level level, String message ) {
        KilnLogger.log( level, message, null );
    }


}
