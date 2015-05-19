/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bijesh.codeautomationtool.models;

/**
 *
 * @author Bijesh
 */
public class ModelAttribs {
    
    private String modelName;
    private String modelAttribs;
    
    public ModelAttribs(String modelName,String modelAttribs){
        this.modelName = modelName;
        this.modelAttribs = modelAttribs;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelAttribs() {
        return modelAttribs;
    }

    public void setModelAttribs(String modelAttribs) {
        this.modelAttribs = modelAttribs;
    }
    
}
