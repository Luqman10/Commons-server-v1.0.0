package com.samaritan.util;

/**
 * This a functional interface that declares a method (setDataFieldWithBase64()).
 * Classes that have data fields that need to be assigned with the Base64 representation of
 * a binary data should implement this interface.
 */
public interface Base64DataFieldPossessor{

    /**
     * Set data field(s) that need to be assigned with the Base64 representation of binary data. The implementer of this
     * method is free to set multiple data fields with Base64 representations.
     */
    void setDataFieldWithBase64() ;
}
