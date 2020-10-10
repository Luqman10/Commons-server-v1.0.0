package com.samaritan.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class allows client code to write log messages to different destinations(console,files etc)
 */
public class LoggingUtil{

    /**
     * write log messages to the console
     * @param name the logger's name
     * @param level severity level of the message
     * @param message the log message
     */
    public static void logMessageToConsole(String name, Level level, String message){

        Logger.getLogger(name).log(level, message) ;
    }
}
