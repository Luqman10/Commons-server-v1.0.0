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
     * create a clause (string in the format of 'FROM [entity_name]')
     * @param entityClass the class of the entity from which [entity_name] will be derived
     * @param <T> the type of the entity
     * @return the created string
     */
    private <T> String createFromClause(Class<T> entityClass){

        return "FROM " + entityClass.getName() ;
    }

}
