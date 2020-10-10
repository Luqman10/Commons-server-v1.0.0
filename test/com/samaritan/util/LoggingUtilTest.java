package com.samaritan.util;

import org.junit.Test;

import java.util.logging.Level;

public class LoggingUtilTest{

    private String message = "Hello world" ;
    private String name = "com.samaritan.util.LoggingUtilTest" ;

    @Test
    public void logMessageToConsole(){

        LoggingUtil.logMessageToConsole(name, Level.INFO, message) ;
    }


    @Test
    public void logMessageToFile(){

        String fileName = "C:\\Users\\electron\\Downloads\\Documents\\server_log.txt" ;
        LoggingUtil.logMessageToFile(name, Level.INFO, message, fileName) ;

    }
}