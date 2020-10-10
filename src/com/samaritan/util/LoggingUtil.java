package com.samaritan.util;

import java.io.IOException;
import java.util.logging.FileHandler;
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

    /**
     * write log messages to the given file
     * @param name the logger's name
     * @param level severity level of the message
     * @param message the log message
     * @param fileName the name of the destination file
     */
    public static void logMessageToFile(String name, Level level, String message, String fileName){

        Logger logger = Logger.getLogger(name) ;

        try{
            logger.addHandler(new FileHandler(fileName,true)) ;
        }
        catch(IOException ex){

            ex.printStackTrace() ;
        }

        logger.log(level, message) ;
    }
}
