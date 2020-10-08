package com.samaritan.util;

import com.samaritan.entity.Department;
import com.samaritan.entity.Employee;
import javafx.util.Pair;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

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

        List<Employee> allEmployees = databaseOperation.selectFromEntity(Employee.class, 0, 3) ;
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
        List<Department> allDepartments = databaseOperation.selectFromEntity(Department.class, parentEntityAlias, joinClauses,
                0, 1) ;

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
        List<Department> allDepartments = databaseOperation.selectFromEntity(Department.class, parentEntityAlias, joinClauses,
                0, 1) ;

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
        List<Department> allDepartments = databaseOperation.selectFromEntity(Department.class, parentEntityAlias, joinClauses,
                0, 3) ;

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
        List<Employee> matchedEmployees =
                databaseOperation.selectFromEntity(Employee.class, whereCondition, namedParams, 0, 1) ;
        assertEquals(1, matchedEmployees.size()) ;
    }

    /**
     * This test should pass if 3 records are returned.
     */
    @Test
    public void selectAllRecordsFromEntityWithoutOwnedRelationsUsingMultipleWhereConditions(){

        String whereCondition = "id = :id OR firstName = :firstName OR email = :email" ;
        Map<String,Object> namedParams = new HashMap<>() ;
        namedParams.put("id", 6) ;
        namedParams.put("firstName", "Mark") ;
        namedParams.put("email", "makaveli@deathrow.com") ;
        List<Employee> matchedEmployees =
                databaseOperation.selectFromEntity(Employee.class, whereCondition, namedParams, 0, 3) ;
        assertEquals(3, matchedEmployees.size()) ;
    }

    /**
     * This test should pass if 1 record is returned and the first name of the manager of that record is Luqman.
     */
    @Test
    public void selectAllRecordsFromEntityWithOwnedRelationsUsingInnerJoinAndSingleWhereCondition(){

        //create join clauses list
        String parentEntityAlias = "dept" ;
        List<JoinClause> joinClauses = new ArrayList<>() ;
        joinClauses.add(new JoinClause(parentEntityAlias, "manager", JoinClause.INNER_JOIN)) ;

        //where condition and namedParams
        String whereCondition = "dept.manager.id = :id" ;
        Map<String,Object> namedParams = new HashMap<>() ;
        namedParams.put("id", 6) ;

        //query the entity with join clauses and a single where condition
        List<Department> allDepartments =
                databaseOperation.selectFromEntity(Department.class, parentEntityAlias, joinClauses, whereCondition, namedParams, 0, 1) ;

        //assert that only 1 department was returned and the first name of the manager in the returned department is
        // Luqman(it means data from the related employee entity has been fetched).
        assertTrue(allDepartments.size() == 1 &&
                allDepartments.get(0).getManager().getFirstName().equals("Luqman")) ;
    }

    /**
     * This test should pass if 3 records are returned.
     */
    @Test
    public void selectAllRecordsFromEntityWithOwnedRelationsUsingInnerJoinAndMultipleWhereConditions(){

        //create join clauses list
        String parentEntityAlias = "dept" ;
        List<JoinClause> joinClauses = new ArrayList<>() ;
        joinClauses.add(new JoinClause(parentEntityAlias, "manager", JoinClause.INNER_JOIN)) ;

        //where condition and namedParams
        String whereCondition = "dept.manager.id = :id OR " +
                "dept.manager.firstName = :firstName OR " +
                "dept.manager.email = :email" ;
        Map<String,Object> namedParams = new HashMap<>() ;
        namedParams.put("id", 6) ;
        namedParams.put("firstName","Mark") ;
        namedParams.put("email", "makaveli@deathrow.com") ;

        //query the entity with join clauses and a single where condition
        List<Department> allDepartments =
                databaseOperation.selectFromEntity(Department.class, parentEntityAlias, joinClauses, whereCondition, namedParams, 0, 3) ;

        //assert that 3 departments were returned
        assertEquals(3, allDepartments.size()) ;
    }

    /**
     * This test should pass if all the tuples(in this case 3) in an entity(without relations) are returned.
     */
    @Test
    public void selectColumnsFromAllTuplesInEntityWithoutRelations(){

        List tuples =
                databaseOperation.selectColumnsFromEntity("Employee", "emp", 0, 3, "id",
                "firstName","lastName","email") ;
        assertEquals(3, tuples.size()) ;
    }

    /**
     * This test should pass if the expected data (in this case Luqman) is returned.
     */
    @Test
    public void selectColumnsFromAllTuplesInEntityWithoutRelationsReturnsTheRightData(){

        List tuples = databaseOperation.selectColumnsFromEntity("Employee", "emp", 0, 1,  "id",
                "firstName","lastName","email") ;
        String firstNameOfFirstEmployee = (String) ((Object[])tuples.get(0))[1] ;
        assertEquals("Luqman", firstNameOfFirstEmployee) ;
    }

    /**
     * This test should pass if all the tuples(in this case 3) in an entity(with relations) are returned.
     */
    @Test
    public void selectColumnsFromAllTuplesInEntityWithRelations(){

        List<JoinClause> joinClauses = new ArrayList<>() ;
        joinClauses.add(new JoinClause("dept", "manager", JoinClause.INNER_JOIN)) ;

        List<Pair<String,String>> pairs = new ArrayList<>() ;
        pairs.add(new Pair<>("dept", "id")) ;
        pairs.add(new Pair<>("dept", "name")) ;
        pairs.add(new Pair<>("dept", "manager.firstName")) ;
        pairs.add(new Pair<>("dept", "manager.lastName")) ;
        pairs.add(new Pair<>("dept", "manager.email")) ;

        List tuples =
                databaseOperation.selectColumnsFromEntity("Department", "dept", joinClauses, pairs, 0, 3) ;

        assertEquals(3, tuples.size()) ;
    }

    /**
     * This test should pass if the expected data (in this case luqman10@samaritan.com) is returned.
     */
    @Test
    public void selectColumnsFromAllTuplesInEntityWithRelationsReturnsTheRightData(){

        List<JoinClause> joinClauses = new ArrayList<>() ;
        joinClauses.add(new JoinClause("dept", "manager", JoinClause.INNER_JOIN)) ;

        List<Pair<String,String>> pairs = new ArrayList<>() ;
        pairs.add(new Pair<>("dept", "id")) ;
        pairs.add(new Pair<>("dept", "name")) ;
        pairs.add(new Pair<>("dept", "manager.firstName")) ;
        pairs.add(new Pair<>("dept", "manager.lastName")) ;
        pairs.add(new Pair<>("dept", "manager.email")) ;

        List tuples = databaseOperation.selectColumnsFromEntity("Department", "dept", joinClauses, pairs, 0, 1) ;
        String email = "luqman10@samaritan.com" ;

        assertEquals(email, ((Object[])tuples.get(0))[5]) ;
    }

    /**
     * This test should pass if only 1 tuple is returned.
     */
    @Test
    public void selectColumnsFromTuplesThatSatisfyWhereConditionInEntityWithoutRelations(){

        Map<String,Object> namedParams = new HashMap<>() ;
        namedParams.put("id",6) ;

        List tuples = databaseOperation.selectColumnsFromEntity("Employee", "emp", "emp.id = :id", namedParams, 0, 1,  "id",
                "firstName","lastName","email") ;
        assertEquals(1, tuples.size()) ;
    }

    /**
     * This test should pass if the first name col in the returned tuple is Luqman
     */
    @Test
    public void selectColumnsFromTuplesThatSatisfyWhereConditionInEntityWithoutRelationsReturnsExpectedData(){

        Map<String,Object> namedParams = new HashMap<>() ;
        namedParams.put("id",6) ;

        List tuples = databaseOperation.selectColumnsFromEntity("Employee", "emp", "emp.id = :id", namedParams, 0, 1,  "id",
                "firstName","lastName","email") ;
        Object[] columns = (Object[]) tuples.get(0) ;
        assertEquals("Luqman", columns[1]) ;
    }

    /**
     * This test should pass if only 1 tuple is returned
     */
    @Test
    public void selectColumnsFromTuplesThatSatisfyWhereConditionInEntityWithRelations(){

        String parentEntityName = "Department" ;
        String parentEntityAlias = "dept" ;

        List<JoinClause> joinClauses = new ArrayList<>() ;
        joinClauses.add(new JoinClause(parentEntityAlias,"manager", JoinClause.INNER_JOIN)) ;

        List<Pair<String,String>> pairsOfEntityNamesAndCols = new ArrayList<>() ;
        pairsOfEntityNamesAndCols.add(new Pair<>(parentEntityAlias,"id")) ;
        pairsOfEntityNamesAndCols.add(new Pair<>(parentEntityAlias,"name")) ;
        pairsOfEntityNamesAndCols.add(new Pair<>(parentEntityAlias + ".manager","firstName")) ;
        pairsOfEntityNamesAndCols.add(new Pair<>(parentEntityAlias + ".manager","lastName")) ;
        pairsOfEntityNamesAndCols.add(new Pair<>(parentEntityAlias + ".manager","email")) ;

        String whereCondition = parentEntityAlias + ".id = :id" ;

        Map<String,Object> namedParams = new HashMap<>() ;
        namedParams.put("id", 1) ;

        List tuples = databaseOperation.selectColumnsFromEntity(parentEntityName,parentEntityAlias,joinClauses,
                pairsOfEntityNamesAndCols,whereCondition,namedParams, 0, 1) ;

        assertEquals(1, tuples.size()) ;
    }

    /**
     * This test should pass when the email col of the returned tuple is luqman10@samaritan.com
     */
    @Test
    public void selectColumnsFromTuplesThatSatisfyWhereConditionInEntityWithRelationsReturnsExpectedData(){

        String parentEntityName = "Department" ;
        String parentEntityAlias = "dept" ;

        List<JoinClause> joinClauses = new ArrayList<>() ;
        joinClauses.add(new JoinClause(parentEntityAlias,"manager", JoinClause.INNER_JOIN)) ;

        List<Pair<String,String>> pairsOfEntityNamesAndCols = new ArrayList<>() ;
        pairsOfEntityNamesAndCols.add(new Pair<>(parentEntityAlias,"id")) ;
        pairsOfEntityNamesAndCols.add(new Pair<>(parentEntityAlias,"name")) ;
        pairsOfEntityNamesAndCols.add(new Pair<>(parentEntityAlias + ".manager","firstName")) ;
        pairsOfEntityNamesAndCols.add(new Pair<>(parentEntityAlias + ".manager","lastName")) ;
        pairsOfEntityNamesAndCols.add(new Pair<>(parentEntityAlias + ".manager","email")) ;

        String whereCondition = parentEntityAlias + ".id = :id" ;

        Map<String,Object> namedParams = new HashMap<>() ;
        namedParams.put("id", 1) ;

        List tuples = databaseOperation.selectColumnsFromEntity(parentEntityName,parentEntityAlias,joinClauses,
                pairsOfEntityNamesAndCols,whereCondition,namedParams, 0, 1) ;

        Object[] cols = (Object[]) tuples.get(0) ;

        assertEquals("luqman10@samaritan.com", cols[5]) ;
    }

    @Test
    public void updateAllRecordsInEntity(){

        String entityName = "Employee" ;
        String colsAndValues = "firstName =: firstName" ;
        Map<String,Object> namedParams = new HashMap<>() ;
        namedParams.put("firstName","Samaritan") ;

        boolean success = databaseOperation.updateRecordsInEntity(entityName, colsAndValues, namedParams) ;
        assertTrue(success) ;
    }

    @Test
    public void updateRecordsThatSatisfyWhereConditionInEntity(){

        String entityName = "Employee" ;
        String colsAndValues = "firstName =: firstName" ;
        String whereCondition = "email =: email" ;
        Map<String,Object> namedParams = new HashMap<>() ;
        namedParams.put("firstName","Tupac") ;
        namedParams.put("email","makaveli@deathrow.com") ;

        boolean success = databaseOperation.updateRecordsInEntity(entityName, colsAndValues, whereCondition, namedParams) ;
        assertTrue(success) ;
    }
}
