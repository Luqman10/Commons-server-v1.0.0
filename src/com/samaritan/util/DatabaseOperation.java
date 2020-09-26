package com.samaritan.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

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
     * @throws IllegalStateException when entityClass is null
     * @return a list of objects of the entity type selected from the entity.
     */
    public <T> List<T> selectFromEntity(Class<T> entityClass) throws IllegalStateException{

        if(entityClass == null)
            throw new IllegalStateException("The class of the entity to select from cannot be null") ;

        Session session = sessionFactory.openSession() ;
        Query<T> query = session.createQuery(createFromClause(entityClass),entityClass) ;
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
     * @param <T> the entity type
     * @throws IllegalStateException when any of the parameters is null. if the where condition is empty.
     * if namedParameters is empty.
     * @return a list of objects of the entity type that satisfy the where condition
     */
    public <T> List<T> selectFromEntity(Class<T> entityClass, String whereCondition, Map<String,Object> namedParameters)
    throws IllegalStateException{

        if(entityClass == null || whereCondition == null || whereCondition.trim().equals("") || namedParameters == null
        || namedParameters.size() == 0)
            throw new IllegalStateException("None of the params can be null. Also, whereCondition and namedParameters " +
                    "cannot be empty.") ;

        Session session = sessionFactory.openSession() ;
        String queryString = createFromClause(entityClass) + " " + createWhereClause(whereCondition) ;
        Query<T> query = session.createQuery(queryString, entityClass) ;
        setNamedParametersInQuery(query, namedParameters) ;
        List<T> recordsInEntityThatSatisfyWhereCondition = query.getResultList() ;
        session.close() ;
        return recordsInEntityThatSatisfyWhereCondition ;
    }

    /**
     * calls .setParameter() on the query object for every entry in the namedParameters map. it is used to set named
     * parameters in the query.
     * @param query the query instance
     * @param namedParameters the map holding named parameters and their values
     * @param <T> the type of the entity we are querying
     */
    private <T> void setNamedParametersInQuery(Query<T> query, Map<String, Object> namedParameters){

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
     * @param <T> the entity type
     * @throws IllegalStateException when any of the params is null. if entityAlias is empty. if joinClauses is empty.
     * @return a list of objects of the entity type selected from the entity.
     */
    public <T> List<T> selectFromEntity(Class<T> entityClass, String entityAlias, List<JoinClause> joinClauses)
            throws IllegalStateException{

        if(entityClass == null || entityAlias == null || entityAlias.trim().equals("") || joinClauses == null ||
        joinClauses.isEmpty())
            throw new IllegalStateException("None of the params may be null. Also entityAlias or joinClauses cannot be " +
                    "empty") ;

        Session session = sessionFactory.openSession() ;
        String queryString = createFromClause(entityClass) + " " + entityAlias + combineJoinClauses(joinClauses) ;
        Query<T> query = session.createQuery(queryString, entityClass) ;
        List<T> allRecordsInEntity = query.getResultList() ;
        session.close() ;
        return allRecordsInEntity ;
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
