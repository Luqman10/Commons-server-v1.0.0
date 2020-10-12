package com.samaritan.util;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Sets the Base64 data field of every Base64DataFieldPossessor in the list by invoking setDataFieldWithBase64() method
 * of each element in a list of Base64DataFieldPossessor(s).
 */
public class Base64DataFieldSetter{

    private List<Base64DataFieldPossessor> base64DataFieldPossessors ;

    public Base64DataFieldSetter(@NotNull List<Base64DataFieldPossessor> base64DataFieldPossessors){

        this.base64DataFieldPossessors = base64DataFieldPossessors ;
    }

    public Base64DataFieldSetter(@NotNull Base64DataFieldPossessor base64DataFieldPossessor){

        List<Base64DataFieldPossessor> base64DataFieldPossessors = new ArrayList<>() ;
        base64DataFieldPossessors.add(base64DataFieldPossessor) ;
        this.base64DataFieldPossessors = base64DataFieldPossessors ;
    }

    /**
     * Invoke setDataFieldWithBase64() method of each element in the list of Base64DataFieldPossessor(s).
     */
    public void setBase64DataFields(){

        for(Base64DataFieldPossessor base64DataFieldPossessor : base64DataFieldPossessors)
            base64DataFieldPossessor.setDataFieldWithBase64() ;
    }
}
