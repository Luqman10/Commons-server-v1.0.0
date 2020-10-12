package com.samaritan.util;

import com.sun.istack.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 *This class has a static method for converting a binary file to its corresponding Base64 representation
 * @author electron
 */
public class Base64Util {

    /**
     * convert file to its corresponding Base64 representation
     * @param file the file to convert
     * @return the base 64 string representation
     */
    public static String convertFileToBase64(@NotNull File file){

        String base64Representation = null ;

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[(int)file.length()] ;
            fileInputStream.read(bytes) ;
            Base64.Encoder encoder = Base64.getEncoder() ;
            base64Representation = encoder.encodeToString(bytes) ;
        }
        catch (IOException ex){

            ex.printStackTrace() ;
        }

        return base64Representation ;
    }
}

