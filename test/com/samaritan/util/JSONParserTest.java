package com.samaritan.util;

import com.samaritan.entity.Employee;
import org.junit.Test;

import static org.junit.Assert.*;

public class JSONParserTest {

    @Test
    public void convertObjectToJSON(){

        Employee employee = new Employee() ;
        employee.setId(1) ;
        employee.setFirstName("Luqman") ;
        employee.setLastName("Abdul Qadir") ;
        employee.setEmail("luqman@samaritan.com") ;

        String expectedJson = "{\"id\":1,\"firstName\":\"Luqman\",\"lastName\":\"Abdul Qadir\",\"email\":\"luqman@samaritan.com\"}" ;
        String actualJson = JSONParser.convertObjectToJSON(employee) ;
        assertEquals(expectedJson, actualJson) ;
    }


    @Test
    public void convertJSONToObject(){

        String dataInJson = "{\"id\":1,\"firstName\":\"Luqman\",\"lastName\":\"Abdul Qadir\",\"email\":\"luqman@samaritan.com\"}" ;

        Employee manuallyCreatedEmployee = new Employee() ;
        manuallyCreatedEmployee.setId(1) ;
        manuallyCreatedEmployee.setFirstName("Luqman") ;
        manuallyCreatedEmployee.setLastName("Abdul Qadir") ;
        manuallyCreatedEmployee.setEmail("luqman@samaritan.com") ;

        Employee employeeFromJsonData = JSONParser.convertJSONToObject(dataInJson, Employee.class) ;
        assertEquals(manuallyCreatedEmployee, employeeFromJsonData) ;
    }
}