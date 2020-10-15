package com.samaritan.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class can be used to convert from JSON format to a Java object and vice-versa.
 */
public class JSONParser{

    private static Gson gson ;

    static {

        gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .create() ;
    }

    /**
     * convert data in a Java object to JSON format. only fields with the @Expose annotation will be serialized.
     * @param object the object to convert
     * @return JSON representation of the data
     */
    public static String convertObjectToJSON(Object object){

        return gson.toJson(object) ;
    }

    /**
     * convert data in JSON format to a Java object.
     * @param json the data in JSON format
     * @param objectClass the class of the returned object
     * @param <T> the type of the object to return
     * @return the object encapsulating the data that was in JSON format
     */
    public static <T> T convertJSONToObject(String json, Class<T> objectClass){

        return gson.fromJson(json, objectClass) ;
    }
}
