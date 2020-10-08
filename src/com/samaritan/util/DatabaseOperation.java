package com.samaritan.util;

import javafx.util.Pair;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This class is used to make database operations(insert,select,update and delete). It uses Hibernate to make 
 * these operations.
 * @author electron
 */
public class DatabaseOperation{
    
    private final SessionFactory sessionFactory ;
    
    /**
     * construct an instance of this class with the given session factory
     * @param sessionFactory session factory
     * @throws IllegalStateException when session factory is null
     */
    public DatabaseOperation(SessionFactory sessionFactory) throws IllegalStateException{

        if(sessionFactory == null)
            throw new IllegalStateException("Session factory cannot be null") ;

        this.sessionFactory = sessionFactory ;
    }
    
    /**
     * insert an object(instance of a persistent class) into the database table that the entity is mapped to.
     * @param object the object to save
     * @throws ConstraintViolationException if the insertion violates an integrity constraint placed on the table
     * @throws IllegalStateException if the passed object to insert is null
     * @return the id of the saved entity
     */
    public Serializable insertObjectIntoEntity(Object object) throws ConstraintViolationException, IllegalStateException{

        if(object == null)
            throw new IllegalStateException("A null object cannot be inserted") ;
        
        Session session = sessionFactory.openSession() ;
        Transaction transaction = session.beginTransaction() ;
        Serializable id ;
        
        try{
            
            id = session.save(object) ;
            transaction.commit() ;
        }
        catch(ConstraintViolationException ex){
            
            if(transaction != null) transaction.rollback() ;
            throw ex ;
        }
        finally{session.close() ;}
        
        return id ;
    }

    /**
     * select all the records in an entity (without the entity owning any relations with other entities)
     * @param entityClass the class of the entity to select from
     * @param <T> the entity type
     * @param start the index of the first result to return
     * @param size the number of records to return
     * @throws IllegalStateException when entityClass is null
     * @return a list of objects of the entity type selected from the entity.
     */
    public <T> List<T> selectFromEntity(Class<T> entityClass, int start, int size) throws IllegalStateException{

        if(entityClass == null)
            throw new IllegalStateException("The class of the entity to select from cannot be null") ;

        Session session = sessionFactory.openSession() ;
        Query<T> query = session.createQuery(createFromClause(entityClass),entityClass) ;
        query.setFirstResult(start) ;
        query.setMaxResults(size) ;
        List<T> allRecordsInEntity = query.getResultList() ;
        session.close() ;
        return allRecordsInEntity ;
    }

    /**
     * select all the records in an entity (without the entity owning any relations with other entities) that satisfy
     * the where condition in a WHERE clause
     * @param entityClass the class of the entity to select from
     * @param whereCondition the condition to use in the WHERE clause (excluding the WHERE keyword)
     * @param namedParameters a map of named parameters used in the where condition and the values to replace them
     * with
     * @param start the index of first record to return
     * @param size the number of records to return
     * @param <T> the entity type
     * @throws IllegalStateException when any of the parameters is null. if the where condition is empty.
     * if namedParameters is empty.
     * @return a list of objects of the entity type that satisfy the where condition
     */
    public <T> List<T> selectFromEntity(Class<T> entityClass, String whereCondition, Map<String,Object> namedParameters,
                                        int start, int size)
    throws IllegalStateException{

        if(entityClass == null || whereCondition == null || whereCondition.trim().equals("") || namedParameters == null
        || namedParameters.size() == 0)
            throw new IllegalStateException("None of the params can be null. Also, whereCondition and namedParameters " +
                    "cannot be empty.") ;

        Session session = sessionFactory.openSession() ;
        String queryString = createFromClause(entityClass) + " " + createWhereClause(whereCondition) ;
        Query<T> query = session.createQuery(queryString, entityClass) ;
        setNamedParametersInQuery(query, namedParameters) ;
        query.setFirstResult(start) ;
        query.setMaxResults(size) ;
        List<T> recordsInEntityThatSatisfyWhereCondition = query.getResultList() ;
        session.close() ;
        return recordsInEntityThatSatisfyWhereCondition ;
    }

    /**
     * calls .setParameter() on the query object for every entry in the namedParameters map. it is used to set named
     * parameters in the query.
     * @param query the query instance
     * @param namedParameters the map holding named parameters and their values
     */
    private void setNamedParametersInQuery(Query query, Map<String, Object> namedParameters){

        for(String key : namedParameters.keySet())
            query.setParameter(key, namedParameters.get(key)) ;
    }

    /**
     * create a where clause in the format of WHERE [whereCondition]
     * @param whereCondition the where condition that appears after the WHERE keyword
     * @return the where clause in the format of WHERE [whereCondition]
     */
    private String createWhereClause(String whereCondition){

        return "WHERE " + whereCondition ;
    }

    /**
     * select all the records in an entity (that has relations with other entities) using the given join clauses
     * @param entityClass the class of the entity to select from
     * @param entityAlias the alias to use for the entity in the query
     * @param joinClauses the list of join clauses to use in querying the entity
     * @param start the index of the first result to return
     * @param size the number of records to return
     * @param <T> the entity type
     * @throws IllegalStateException when any of the params is null. if entityAlias is empty. if joinClauses is empty.
     * @return a list of objects of the entity type selected from the entity.
     */
    public <T> List<T> selectFromEntity(Class<T> entityClass, String entityAlias, List<JoinClause> joinClauses, int start,
                                        int size)
            throws IllegalStateException{

        if(entityClass == null || entityAlias == null || entityAlias.trim().equals("") || joinClauses == null ||
        joinClauses.isEmpty())
            throw new IllegalStateException("None of the params may be null. Also entityAlias or joinClauses cannot be " +
                    "empty") ;

        Session session = sessionFactory.openSession() ;
        String queryString = createFromClause(entityClass) + " " + entityAlias + combineJoinClauses(joinClauses) ;
        Query<T> query = session.createQuery(queryString, entityClass) ;
        query.setFirstResult(start) ;
        query.setMaxResults(size) ;
        List<T> allRecordsInEntity = query.getResultList() ;
        session.close() ;
        return allRecordsInEntity ;
    }

    /**
     * select records in an entity (that has relations with other entities) using the given join clauses and a where
     * condition
     * @param entityClass the class of the entity to select from
     * @param entityAlias the alias to use for the entity in the query
     * @param joinClauses the list of join clauses to use in querying the entity
     * @param whereCondition the condition to use in the WHERE clause (excluding the WHERE keyword)
     * @param namedParameters a map of named parameters used in the where condition and the values to replace them
     * with.
     * @param start the index of the first record to return
     * @param size the max number of records to return
     * @param <T> the entity type
     * @throws IllegalStateException when any of the params is null. if entityAlias is empty. if joinClauses is empty.
     * @return a list of objects of the entity type selected from the entity.
     */
    public <T> List<T> selectFromEntity(Class<T> entityClass, String entityAlias, List<JoinClause> joinClauses,
                                        String whereCondition, Map<String,Object> namedParameters, int start, int size)
            throws IllegalStateException{

        if(entityClass == null || entityAlias == null || entityAlias.trim().equals("") || joinClauses == null ||
                joinClauses.isEmpty() || whereCondition == null || whereCondition.trim().equals("") ||
        namedParameters == null || namedParameters.isEmpty())
            throw new IllegalStateException("None of the params may be null. Also entityAlias, joinClauses, " +
                    "whereCondition and namedParameters cannot be empty") ;

        Session session = sessionFactory.openSession() ;
        String queryString = createFromClause(entityClass) + " " + entityAlias + combineJoinClauses(joinClauses) ;
        queryString += (" " + createWhereClause(whereCondition)) ;
        Query<T> query = session.createQuery(queryString, entityClass) ;
        setNamedParametersInQuery(query, namedParameters) ;
        query.setFirstResult(start) ;
        query.setMaxResults(size) ;
        List<T> recordsInEntityThatSatisfyWhereCondition = query.getResultList() ;
        session.close() ;
        return recordsInEntityThatSatisfyWhereCondition ;
    }

    /**
     * select column(s) from all rows in the given entity (without relations to other entities).
     * @param entityName the name of the entity to select from
     * @param entityAlias the alias to use for the entity in the select query
     * @param start the index of the first record to return
     * @param size the max number of records to return
     * @param columns a variable-length list of columns to select from the entity
     * @throws IllegalStateException if any of the arguments is null or empty.
     * @return a list of object arrays holding the column values for each row.
     */
    public List selectColumnsFromEntity(String entityName, String entityAlias, int start, int size, String ... columns) throws
            IllegalStateException{

        if(entityName == null || entityName.trim().equals("") || entityAlias == null || entityAlias.trim().equals("") ||
        columns == null || columns.length == 0)
            throw new IllegalStateException("None of the arguments can be null or empty.") ;

        Session session = sessionFactory.openSession() ;
        String queryString = "SELECT " + createColumnsStringForColumnsThatBelongToSingleEntity(entityAlias,columns) +
                "FROM " + entityName + " as " + entityAlias ;
        Query query = session.createQuery(queryString) ;
        query.setFirstResult(start) ;
        query.setMaxResults(size) ;
        List listOfTuples = query.list() ;
        session.close() ;
        return listOfTuples ;
    }

    /**
     * select column(s) from all rows in the given parent entity related to other entities.
     * @param parentEntityName the parent entity name
     * @param parentEntityAlias the alias to use for the parent entity
     * @param joinClauses the list of join clauses specifying which type of join to use between the parent entity and
     * which child entity
     * @param pairsOfEntityNamesAndColumnsToSelect list of pairs that hold the name/alias of an entity and the column
     * to select from that entity.
     * @param start the index of the first record to return
     * @param size the max number of records to return
     * @throws IllegalStateException if any of the arguments is empty or null.
     * @return a list of object arrays holding the column values for each row.
     */
    public List selectColumnsFromEntity(String parentEntityName, String parentEntityAlias, List<JoinClause> joinClauses,
                                        List<Pair<String,String>> pairsOfEntityNamesAndColumnsToSelect, int start, int size)throws
            IllegalStateException{

        if(parentEntityName == null || parentEntityName.trim().equals("") || parentEntityAlias == null ||
        parentEntityAlias.trim().equals("") || joinClauses == null || joinClauses.isEmpty() ||
        pairsOfEntityNamesAndColumnsToSelect == null || pairsOfEntityNamesAndColumnsToSelect.isEmpty())
            throw new IllegalStateException("None of the arguments may be null.") ;

        Session session = sessionFactory.openSession() ;
        String queryString = "SELECT " + parentEntityAlias + ", " + createColumnsStringForColumnsThatBelongToDifferentEntities(pairsOfEntityNamesAndColumnsToSelect) +
                "FROM " + parentEntityName + " as " + parentEntityAlias + " " + combineJoinClauses(joinClauses) ;
        Query query = session.createQuery(queryString) ;
        query.setFirstResult(start) ;
        query.setMaxResults(size) ;
        List listOfTuples = query.list() ;
        session.close() ;
        return listOfTuples ;
    }

    /**
     * select column(s) from rows that satisfy the given where condition in the given entity
     * (without relations to other entities).
     * @param entityName the name of the entity to select from
     * @param entityAlias the alias to use for the entity in the select query
     * @param columns a variable-length list of columns to select from the entity
     * @param whereCondition the condition(that comes after the WHERE keyword in the query) to be used in filtering
     * the tuples.
     * @param namedParameters a map of named parameters in the query and the values to replace them with.
     * @param start the index of the first record to be returned
     * @param size the max number of records to return
     * @throws IllegalStateException if any of the arguments is null or empty.
     * @return a list of object arrays holding the column values for each row.
     */
    public List selectColumnsFromEntity(String entityName, String entityAlias, String whereCondition,
                                        Map<String,Object> namedParameters, int start, int size, String ... columns) throws
            IllegalStateException{

        if(entityName == null || entityName.trim().equals("") || entityAlias == null || entityAlias.trim().equals("") ||
                columns == null || columns.length == 0 || whereCondition == null || whereCondition.trim().equals("") ||
                namedParameters == null || namedParameters.isEmpty())
            throw new IllegalStateException("None of the arguments can be null or empty.") ;

        Session session = sessionFactory.openSession() ;
        String queryString = "SELECT " + createColumnsStringForColumnsThatBelongToSingleEntity(entityAlias,columns) +
                "FROM " + entityName + " as " + entityAlias + " " + createWhereClause(whereCondition) ;
        Query query = session.createQuery(queryString) ;
        setNamedParametersInQuery(query, namedParameters) ;
        query.setFirstResult(start) ;
        query.setMaxResults(size) ;
        List listOfTuples = query.list() ;
        session.close() ;
        return listOfTuples ;
    }

    /**
     * select column(s) from tuples that match the given where condition in the given parent entity related to other entities.
     * @param parentEntityName the parent entity name
     * @param parentEntityAlias the alias to use for the parent entity
     * @param joinClauses the list of join clauses specifying which type of join to use between the parent entity and
     * which child entity
     * @param pairsOfEntityNamesAndColumnsToSelect list of pairs that hold the name/alias of an entity and the column
     * to select from that entity.
     * @param whereCondition the condition(appears after the WHERE keyword in the query) to use in filtering the selected
     * tuples.
     * @param namedParameters the map of named parameters used in the query and the values to replace them with.
     * @param start the index of the first record to return
     * @param size the max number of records to return
     * @throws IllegalStateException if any of the arguments is empty or null.
     * @return a list of object arrays holding the column values for each row.
     */
    public List selectColumnsFromEntity(String parentEntityName, String parentEntityAlias, List<JoinClause> joinClauses,
                                        List<Pair<String,String>> pairsOfEntityNamesAndColumnsToSelect, String whereCondition,
                                        Map<String,Object> namedParameters, int start, int size)throws IllegalStateException{

        if(parentEntityName == null || parentEntityName.trim().equals("") || parentEntityAlias == null ||
                parentEntityAlias.trim().equals("") || joinClauses == null || joinClauses.isEmpty() ||
                pairsOfEntityNamesAndColumnsToSelect == null || pairsOfEntityNamesAndColumnsToSelect.isEmpty() ||
                whereCondition == null || whereCondition.trim().equals("") || namedParameters == null || namedParameters.isEmpty())
            throw new IllegalStateException("None of the arguments may be null.") ;

        Session session = sessionFactory.openSession() ;
        String queryString = "SELECT " + parentEntityAlias + ", " + createColumnsStringForColumnsThatBelongToDifferentEntities(pairsOfEntityNamesAndColumnsToSelect) +
                "FROM " + parentEntityName + " as " + parentEntityAlias + " " + combineJoinClauses(joinClauses) + " " +
                createWhereClause(whereCondition) ;
        Query query = session.createQuery(queryString) ;
        setNamedParametersInQuery(query, namedParameters) ;
        query.setFirstResult(start) ;
        query.setMaxResults(size) ;
        List listOfTuples = query.list() ;
        session.close() ;
        return listOfTuples ;
    }


    /**
     * update all records in an entity
     * @param entityName the entity name
     * @param columnsAndCorrespondingValues a string holding the columns to update and the corresponding values as named
     * params(appears after the SET keyword).
     * @param namedParameters a map of named parameters(key) used in the columnsAndCorrespondingValues string and the values
     * to replace them with
     * @throws HibernateException when there is an error in executing the update
     * @return true if the update operation was successful
     */
    public boolean updateRecordsInEntity(String entityName, String columnsAndCorrespondingValues, Map<String,Object> namedParameters)
            throws HibernateException{

        Transaction transaction = null ;
        int success = 0 ;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction() ;
            String queryString = "UPDATE " + entityName + " SET " + columnsAndCorrespondingValues ;
            Query query = session.createQuery(queryString) ;
            setNamedParametersInQuery(query, namedParameters) ;
            success = query.executeUpdate() ;
            transaction.commit() ;

        }
        catch (HibernateException ex){

            if (transaction != null) transaction.rollback() ;
            throw ex ;
        }

        return success > 0 ;
    }

    /**
     * update records that satisfy a given where condition in an entity
     * @param entityName the entity name
     * @param columnsAndCorrespondingValues a string holding the columns to update and the corresponding values as named
     * params(appears after the SET keyword).
     * @param whereCondition the condition to use in filtering the records to update
     * @param namedParameters a map of named parameters(key) used in columnsAndCorrespondingValues, whereCondition string and the values
     * to replace them with.
     * @throws HibernateException when there is an error in executing the update
     * @return true if the update operation was successful
     */
    public boolean updateRecordsInEntity(String entityName, String columnsAndCorrespondingValues, String whereCondition, Map<String,Object> namedParameters)
            throws HibernateException{

        Transaction transaction = null ;
        int success = 0 ;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction() ;
            String queryString = "UPDATE " + entityName + " SET " + columnsAndCorrespondingValues + " WHERE " + whereCondition ;
            Query query = session.createQuery(queryString) ;
            setNamedParametersInQuery(query, namedParameters) ;
            success = query.executeUpdate() ;
            transaction.commit() ;

        }
        catch (HibernateException ex){

            if (transaction != null) transaction.rollback() ;
            throw ex ;
        }

        return success > 0 ;
    }


    /**
     * create a comma separated string of all the columns(prefixed with 'entityAlias.') in the pairs in the list.
     * NB: This method is used to create a columns string for columns belonging to different entities(alias).
     * @param pairsOfEntityNamesAndColumnsToSelect the list of pairs(entity name/alias, column)
     * @return the created string
     */
    private String createColumnsStringForColumnsThatBelongToDifferentEntities(List<Pair<String, String>> pairsOfEntityNamesAndColumnsToSelect){

        StringBuilder columnsStringBuilder = new StringBuilder() ;

        for(int i = 0 ; i < pairsOfEntityNamesAndColumnsToSelect.size() ; i++){

            Pair<String,String> pair = pairsOfEntityNamesAndColumnsToSelect.get(i) ;
            columnsStringBuilder.append(pair.getKey()).append(".").append(pair.getValue()) ;

            //if the current index is not the last, append [, ] to the string builder.
            if(i != pairsOfEntityNamesAndColumnsToSelect.size() - 1)
                columnsStringBuilder.append(", ") ;
                //if its the last index, just append space to the string builder.
            else
                columnsStringBuilder.append(" ") ;
        }

        return columnsStringBuilder.toString() ;
    }

    /**
     * create a comma separated string of all the columns(prefixed with 'entityAlias.') in the columns array.
     * NB: This method is used to create a columns string for columns all belonging to a single entity(alias).
     * @param entityAlias the entity alias to prefix each column with
     * @param columns the array of columns.
     * @return the created string.
     */
    private String createColumnsStringForColumnsThatBelongToSingleEntity(String entityAlias, String[] columns){

        //prefix(entityAlias.) to append to every column
        String prefix = entityAlias + "." ;
        StringBuilder columnsStringBuilder = new StringBuilder() ;
        //for every column, create entityAlias.column and append to string builder
        for(int i = 0 ; i < columns.length ; i++){

            columnsStringBuilder.append(prefix).append(columns[i]) ;
            //if the current index is not the last, append [, ] to the string builder.
            if(i != columns.length - 1)
                columnsStringBuilder.append(", ") ;
            //if its the last index, just append space to the string builder.
            else
                columnsStringBuilder.append(" ") ;
        }

        return columnsStringBuilder.toString() ;
    }

    /**
     * call getJoinClause() on each joinClause in the list and combine(concat) all the returned string values into one.
     * @param joinClauses the list of joinClause instances
     * @return the concatenated string of all the joinClauses' getJoinClause()
     */
    private String combineJoinClauses(List<JoinClause> joinClauses){

        StringBuilder combinedJoinClauses = new StringBuilder() ;
        for(JoinClause joinClause : joinClauses)
            combinedJoinClauses.append(" ").append(joinClause.getJoinClause()) ;

        return combinedJoinClauses.toString() ;
    }

    /**
     * create a clause (string in the format of 'FROM [entity_name]')
     * @param entityClass the class of the entity from which [entity_name] will be derived
     * @param <T> the type of the entity
     * @return the created string
     */
    private <T> String createFromClause(Class<T> entityClass){

        return "FROM " + entityClass.getName() ;
    }

}
