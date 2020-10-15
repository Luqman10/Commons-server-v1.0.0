package com.samaritan.util;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * This class helps client code to build complex HTTP responses
 */
public class HttpResponseBuilder{

    /**
     * create an HTTP response with the given response headers, status code and response entity.
     * @param status the response status
     * @param entity the response entity
     * @param headers the response headers
     * @return the built HTTP response
     */
    public static Response response(Response.Status status, Object entity, MultivaluedMap<String,Object> headers){

        Response.ResponseBuilder builder = Response.status(status) ;
        builder.entity(entity) ;
        Response response = builder.build() ;
        response.getHeaders().putAll(headers) ;
        return response ;
    }
}
