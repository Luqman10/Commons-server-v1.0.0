package com.samaritan.util;

import com.samaritan.entity.Department;
import com.samaritan.entity.Employee;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    /**
     * This test should pass if all the records in an entity are returned.
     */
    @Test
    public void selectAllRecordsFromEntityWithoutOwnedRelations(){

        List<Employee> allEmployees = databaseOperation.selectFromEntity(Employee.class) ;
        assertEquals(3, allEmployees.size()) ;
    }

    /**
     * This test should pass if the first name of the manager of the first department is Luqman.
     */
    @Test
    public void selectAllRecordsFromEntityWithOwnedRelationsUsingInnerJoin(){

        //create join clauses list
        String parentEntityAlias = "dept" ;
        List<JoinClause> joinClauses = new ArrayList<>() ;
        joinClauses.add(new JoinClause(parentEntityAlias, "manager", JoinClause.INNER_JOIN)) ;

        //query the entity with join clauses
        List<Department> allDepartments = databaseOperation.selectFromEntity(Department.class, parentEntityAlias, joinClauses) ;

        //assert that the first name of the manager in the first department is Luqman(it means data from the related employee entity
        // has been fetched).
        Employee manager = allDepartments.get(0).getManager() ;
        assertEquals("Luqman", manager.getFirstName()) ;
    }

    /**
     * This test should pass if there's only 1 returned department since a left outer join was used to join
     * Department and Employee (Department has only 1 record).
     */
    @Test
    public void selectAllRecordsFromEntityWithOwnedRelationsUsingLeftOuterJoin(){

        //create join clauses list
        String parentEntityAlias = "dept" ;
        List<JoinClause> joinClauses = new ArrayList<>() ;
        joinClauses.add(new JoinClause(parentEntityAlias, "manager", JoinClause.LEFT_OUTER_JOIN)) ;

        //query the entity with join clauses
        List<Department> allDepartments = databaseOperation.selectFromEntity(Department.class, parentEntityAlias, joinClauses) ;

        //assert that only one department was returned.
        assertEquals(1, allDepartments.size()) ;
    }

    /**
     * This test should pass if there are 3 returned departments since a right outer join was used to join
     * Department and Employee (Employee has 3 records).
     */
    @Test
    public void selectAllRecordsFromEntityWithOwnedRelationsUsingRightOuterJoin(){

        //create join clauses list
        String parentEntityAlias = "dept" ;
        List<JoinClause> joinClauses = new ArrayList<>() ;
        joinClauses.add(new JoinClause(parentEntityAlias, "manager", JoinClause.RIGHT_OUTER_JOIN)) ;

        //query the entity with join clauses
        List<Department> allDepartments = databaseOperation.selectFromEntity(Department.class, parentEntityAlias, joinClauses) ;

        //assert that 3 departments were returned.
        assertEquals(3, allDepartments.size()) ;
    }

    /**
     * This test should pass if 1 record is returned.
     */
    @Test
    public void selectAllRecordsFromEntityWithoutOwnedRelationsUsingSingleWhereCondition(){

        String whereCondition = "id = :id" ;
        Map<String,Object> namedParams = new HashMap<>() ;
        namedParams.put("id", 6) ;
        List<Employee> matchedEmployees = databaseOperation.selectFromEntity(Employee.class, whereCondition, namedParams) ;
        assertEquals(1, matchedEmployees.size()) ;
    }

    /**
     * This test should pass if 3 records are returned.
     */
    @Test
    public void selectAllRecordsFromEntityWithoutOwnedRelationsUsingMultipleWhereCondition(){

        String whereCondition = "id = :id OR firstName = :firstName OR email = :email" ;
        Map<String,Object> namedParams = new HashMap<>() ;
        namedParams.put("id", 6) ;
        namedParams.put("firstName", "Mark") ;
        namedParams.put("email", "makaveli@deathrow.com") ;
        List<Employee> matchedEmployees = databaseOperation.selectFromEntity(Employee.class, whereCondition, namedParams) ;
        assertEquals(3, matchedEmployees.size()) ;
    }
}
