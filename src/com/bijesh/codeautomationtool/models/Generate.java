/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bijesh.codeautomationtool.models;

import java.io.File;

/**
 *
 * @author Bijesh
 */
public class Generate {
    private File generatePath;
    public Generate(File generatePath){
        this.generatePath = generatePath;
    }

    public File getGeneratePath() {
        return generatePath;
    }

    public void setGeneratePath(File generatePath) {
        this.generatePath = generatePath;
    }
    
}
