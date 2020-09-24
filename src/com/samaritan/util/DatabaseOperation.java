package com.samaritan.util;

import java.io.Serializable;
import java.util.List;

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
     */
    public DatabaseOperation(SessionFactory sessionFactory){
        
        this.sessionFactory = sessionFactory ;
    }
    
    /**
     * insert an object(instance of a persistent class) into the database table that the entity is mapped to.
     * @param object the object to save
     * @throws ConstraintViolationException if the insertion violates an integrity constraint placed on the table
     * @return the id of the saved entity
     */
    public Serializable insertObjectIntoEntity(Object object) throws ConstraintViolationException{
        
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
     * @return a list of objects of the entity type selected from the entity.
     */
    public <T> List<T> selectFromEntity(Class<T> entityClass){

        Session session = sessionFactory.openSession() ;
        Query<T> query = session.createQuery(createFromClause(entityClass),entityClass) ;
        List<T> allRecordsInEntity = query.getResultList() ;
        session.close() ;
        return allRecordsInEntity ;
    }

    /**
     * select all the records in an entity (that has relations with other entities) using the given join clauses
     * @param entityClass the class of the entity to select from
     * @param entityAlias the alias to use for the entity in the query
     * @param joinClauses the list of join clauses to use in querying the entity
     * @param <T> the entity type
     * @return a list of objects of the entity type selected from the entity.
     */
    public <T> List<T> selectFromEntity(Class<T> entityClass, String entityAlias, List<JoinClause> joinClauses){

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
