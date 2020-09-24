package com.samaritan.util;

import com.samaritan.entity.Employee;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.*;

import java.io.Serializable;

import static org.junit.Assert.* ;

/**
 * Test class for DatabaseOperation
 * @author electron
 */
public class DatabaseOperationTest {

    private DatabaseOperation databaseOperation ;

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

    /**
     * This test should pass if a ConstraintViolationException is thrown because the insertion operation violates a
     * constraint
     */
    @Test(expected = ConstraintViolationException.class)
    public void insertObjectIntoEntityThrowsExceptionOnConstraintViolation(){

        insertObjectIntoEntityReturnsNonNullIdOnSuccess() ;
    }

}
