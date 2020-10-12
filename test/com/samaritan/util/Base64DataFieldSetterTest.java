package com.samaritan.util;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Base64DataFieldSetterTest {

    private List<Base64DataFieldPossessor> base64DataFieldPossessors = new ArrayList<>() ;

    @Test
    public void setBase64DataFields(){

        //populate the base64 data field possessors list and use it to create a base64 data field setter
        populateDataFieldPossessors() ;
        Base64DataFieldSetter base64DataFieldSetter = new Base64DataFieldSetter(base64DataFieldPossessors) ;
        //set the base64 data fields of the base64 data field possessors in the list
        base64DataFieldSetter.setBase64DataFields() ;

        //generate base64 string of binary file on filesystem and check if the base64 is equal to that of the base64 data
        //field of the possessor in the list.
        String fileName = "C:\\Users\\electron\\Downloads\\Music\\O_leal.mp3" ;
        File file = new File(fileName) ;
        String base64 = Base64Util.convertFileToBase64(file) ;
        Song song = (Song) base64DataFieldPossessors.get(0) ;
        assertEquals(base64, song.getSong()) ;
    }


    private void populateDataFieldPossessors(){

        Base64DataFieldPossessor base64DataFieldPossessor = new Song() ;
        base64DataFieldPossessors.add(base64DataFieldPossessor) ;
    }

    //inner class that is a base64 data field possessor
    private static class Song implements Base64DataFieldPossessor{

        private String song = "C:\\Users\\electron\\Downloads\\Music\\O_leal.mp3" ;

        @Override
        public void setDataFieldWithBase64(){

            File file = new File(song) ;
            song = Base64Util.convertFileToBase64(file) ;
        }

        String getSong(){

            return song ;
        }
    }
}