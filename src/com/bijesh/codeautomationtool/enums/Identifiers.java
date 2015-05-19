/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bijesh.codeautomationtool.enums;

/**
 *
 * @author Bijesh
 */
public enum Identifiers {
    
    BYT(" byte "),
    BYTE(" Byte "),
    SHT(" short "),
    SHORT(" Short "),
    INT(" int "),
    INTEGER(" Integer "),
    LNG(" long "),
    LONG(" Long "),
    STRING(" String "),
    CHR(" char "),
    CHARACTER(" Character "),
    FLT(" float "),
    FLOAT(" Float "),
    DBL(" double "),
    DOUBLE(" Double ");
    
    Identifiers(String identifiers){
        this.identifiers = identifiers;
    }
    
    private final String identifiers;
    public String getIdentifier(){
        return this.identifiers;
    }
}
