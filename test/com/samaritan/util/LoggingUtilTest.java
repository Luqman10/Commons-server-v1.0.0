package com.samaritan.util;

import org.junit.Test;

import java.util.logging.Level;

public class LoggingUtilTest{

    @Test
    public void logMessageToConsole(){

        String message = "Hello world" ;
        LoggingUtil.logMessageToConsole("com.samaritan.util.LoggingUtilTest", Level.INFO, message) ;
    }
}