/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bijesh.codeautomationtool.commonutils;

import com.bijesh.codeautomationtool.models.ModelAttribs;

/**
 *
 * @author Bijesh
 */
public class BasicUtils {
    
    public static String[] getVariablesWithModifiers(ModelAttribs attrbs) {
        return attrbs.getModelAttribs().split("\n");
    }
}
