package com.samaritan.util;

import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class HttpResponseBuilderTest {

    @Test
    public void response(){

        String entity = "Hello World" ;
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>() ;
        List<Object> listOfValuesForHello = new ArrayList<>() ;
        listOfValuesForHello.add("world") ;
        headers.put("hello", listOfValuesForHello) ;

        Response response = HttpResponseBuilder.response(Response.Status.OK, entity, headers) ;
        assertNotNull(response) ;
    }
}