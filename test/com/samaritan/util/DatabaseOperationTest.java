/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samaritan.util;

import com.samaritan.entity.Employee;
import java.io.Serializable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.* ;

/**
 * Test class for DatabaseOperation
 * @author electron
 */
public class DatabaseOperationTest {
    
   DatabaseOperation databaseOperation ;
    
    @Before
    public void setUp() {
        
        databaseOperation = new DatabaseOperation(HibernateUtil.getSessionFactory()) ;
    }
    
    @After
    public void tearDown() {
        
        databaseOperation = null ;
    }

    /**
     * This test should pass when the insert operation succeeds
     */
    @Test
    public void insertObjectIntoEntityReturnsNonNullIdOnSuccess(){
        
        Employee employee = new Employee() ;
        employee.setFirstName("Luqman") ;
        employee.setLastName("Abdul Qadir") ;
        employee.setEmail("luqman10@gmail.com") ;
        Serializable id = databaseOperation.insertObjectIntoEntity(employee) ;
        assertNotNull(id) ;
    }
    
}
