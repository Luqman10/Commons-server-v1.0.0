package com.samaritan.util;

import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.* ;

public class Base64UtilTest {

    @Test
    public void convertFileToBase64ReturnsStringForNonEmptyFile(){

        try{

            URI uri = getClass().getResource("/com/samaritan/asset/O_leal.mp3").toURI() ;
            File file = new File(uri) ;
            String base64 = Base64Util.convertFileToBase64(file) ;
            assertNotNull(base64) ;
        }
        catch(URISyntaxException ex){

            ex.printStackTrace() ;
        }
    }

    @Test
    public void convertFileToBase64ReturnsEmptyStringForEmptyFile(){

        try{

            URI uri = getClass().getResource("/com/samaritan/asset/empty_file.mp3").toURI() ;
            File file = new File(uri) ;
            String base64 = Base64Util.convertFileToBase64(file) ;
            assertEquals("", base64) ;
        }
        catch(URISyntaxException ex){

            ex.printStackTrace() ;
        }
    }
}