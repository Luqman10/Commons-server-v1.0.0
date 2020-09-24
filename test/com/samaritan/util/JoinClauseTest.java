package com.samaritan.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class JoinClauseTest {

    @Test(expected = IllegalStateException.class)
    public void joinClause(){

        JoinClause joinClause = new JoinClause("dept", "manager",2) ;
    }


    @Test
    public void getJoinClauseForLeftOuterJoin(){

        JoinClause joinClause = new JoinClause("dept", "manager",
                JoinClause.LEFT_OUTER_JOIN) ;
        String expected = "LEFT JOIN FETCH dept.manager" ;
        assertEquals(expected, joinClause.getJoinClause()) ;
    }

    @Test
    public void getJoinClauseForInnerJoin(){

        JoinClause joinClause = new JoinClause("dept", "manager",
                JoinClause.INNER_JOIN) ;
        String expected = "JOIN FETCH dept.manager" ;
        assertEquals(expected, joinClause.getJoinClause()) ;
    }

    @Test
    public void getJoinClauseForRightOuterJoin(){

        JoinClause joinClause = new JoinClause("dept", "manager",
                JoinClause.RIGHT_OUTER_JOIN) ;
        String expected = "RIGHT JOIN FETCH dept.manager" ;
        assertEquals(expected, joinClause.getJoinClause()) ;
    }
}