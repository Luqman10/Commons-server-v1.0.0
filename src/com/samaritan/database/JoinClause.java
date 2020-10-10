package com.samaritan.database;

/**
 * This class represents a join clause. It has 3 parts:
 * 1. The join type(LEFT_JOIN_FETCH/JOIN_FETCH/RIGHT_JOIN_FETCH)
 * 2. The parent entity alias
 * 3. The child entity name
 */
public class JoinClause{

    public static final int LEFT_OUTER_JOIN = -1 ;
    public static final int INNER_JOIN = 0 ;
    public static final int RIGHT_OUTER_JOIN = 1 ;
    private String childEntityName ;
    private String parentEntityAlias ;
    private int joinType ;

    public JoinClause(String parentEntityAlias, String childEntityName, int joinType) throws IllegalStateException{

        //if join type is not one of [-1,0,1], throw ex
        if(joinType < LEFT_OUTER_JOIN || joinType > RIGHT_OUTER_JOIN)
            throw new IllegalStateException("Join type can be -1,0 or 1") ;

        //else init data fields
        this.parentEntityAlias = parentEntityAlias ;
        this.childEntityName = childEntityName ;
        this.joinType = joinType ;
    }

    /**
     * get the join clause represented by this instance.
     * @return a string in the format of [LEFT_JOIN_FETCH/JOIN_FETCH/RIGHT_JOIN_FETCH] parentEntityAlias.childEntityName
     */
    public String getJoinClause(){

        //initial empty join clause
        String joinClause = "" ;

        //decide the keywords to use based on the value of joinType
        if(joinType == LEFT_OUTER_JOIN)
            joinClause += "LEFT JOIN FETCH" ;
        else if(joinType == INNER_JOIN)
            joinClause += "JOIN FETCH" ;
        else if(joinType == RIGHT_OUTER_JOIN)
            joinClause += "RIGHT JOIN FETCH" ;

        //concat 'parentEntityAlias.childEntityName' to the selected keyword above
        joinClause += (" " + parentEntityAlias + "." + childEntityName) ;
        return joinClause ;
    }
}