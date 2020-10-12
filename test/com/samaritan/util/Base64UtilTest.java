package com.samaritan.util;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class Base64UtilTest {

    @Test
    public void convertFileToBase64(){

        String fileName = "C:\\Users\\electron\\Downloads\\Music\\O_leal.mp3" ;
        File file = new File(fileName) ;
        String base64 = Base64Util.convertFileToBase64(file) ;
        assertNotNull(base64) ;
    }
}