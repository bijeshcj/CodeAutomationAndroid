/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bijesh.codeautomationtool.ui.helper;

import com.bijesh.codeautomationtool.enums.Identifiers;
import com.bijesh.codeautomationtool.models.Generate;
import com.bijesh.codeautomationtool.models.ModelAttribs;
import com.bijesh.codeautomationtool.types.GenerateType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;

/**
 *
 * @author Bijesh
 */
public class GenerateHelper implements com.bijesh.codeautomationtool.constants.Package {

    private Generate mGenerate;
    private ModelAttribs mModelAttribs;
    private StringBuilder mConstructorBuilder = new StringBuilder();
    private String mTableClassName;

    public void generate(GenerateType generateType, ModelAttribs modelAttribs) {
        initGenerateModel();
        switch (generateType) {
            case MODEL:
                createModel(modelAttribs);
                break;
            case SQLITETABLE:

                break;
            case MODEL_SQLITETABLE:
                createModel(modelAttribs);
                createTable(modelAttribs);
                break;
        }
    }

    private void initGenerateModel() {
        chooseGenerateFolder();
    }

    private void chooseGenerateFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retVal = fileChooser.showOpenDialog(null);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            mGenerate = new Generate(file);
            System.out.println("Generate path " + file);
        }
    }

    private void createModel(ModelAttribs modelAttribs) {
        try {
            generateModelFile(modelAttribs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void createTable(ModelAttribs modelAttribs){
        try{
            generateTableFile(modelAttribs);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void generateModelFile(ModelAttribs attrbs) throws IOException {
        File javaFile = generateJavaFile(attrbs.getModelName(),"");
        System.out.println("java file generated " + javaFile.toString());
        setContentsToFile(javaFile, attrbs);
    }
    
    private void generateTableFile(ModelAttribs attrbs) throws IOException{
        File javaFile = generateJavaFile(attrbs.getModelName(), "Table");
        mTableClassName = attrbs.getModelName()+"Table";
        setContentsToTableFile(javaFile, attrbs);
    }

    private File generateJavaFile(String name,String name1) throws IOException {
        String fileName = mGenerate.getGeneratePath() + File.separator + name+name1 + ".java";
        System.out.println("fileName "+fileName);
        File javaFile = new File(fileName);
        javaFile.createNewFile();
        return javaFile;
    }
    
  

    private void setContentsToFile(File javaFile, ModelAttribs attrbs) {
        StringBuilder fileContent = new StringBuilder();
        fileContent.append(getClassInitContents(attrbs));

        fileContent.append(getAttribs(attrbs));

        fileContent.append(getSetterGetter(attrbs));

        fileContent.append(getClassEndContents());
        writeContentToFile(javaFile, fileContent.toString());
    }
    
    private void setContentsToTableFile(File javaFile,ModelAttribs attrbs){
        TableContentHelper helper = new TableContentHelper(mTableClassName);
        StringBuilder fileContent = new StringBuilder();
        fileContent.append(helper.getTableClassInitContents(attrbs));
        fileContent.append(helper.createTableName(attrbs.getModelName()));
        fileContent.append(helper.createColumns(attrbs));
        fileContent.append(helper.generateCreateQuery(attrbs));
        fileContent.append(helper.generateOnCreateAndUpgradeMethod());
        
        fileContent.append(getClassEndContents());
        writeContentToFile(javaFile, fileContent.toString());
    }

    private String getAttribs(ModelAttribs attrbs) {
        return attrbs.getModelAttribs();
    }

    private String getSetterGetter(ModelAttribs attrbs) {
//        String retVal = new String();
        String[] allVariablesWithModifiers = getVariablesWithModifiers(attrbs);
        StringBuilder allVariables = getVariables(allVariablesWithModifiers);

        return allVariables.toString();
    }

    private StringBuilder getVariables(String[] variables) {
        StringBuilder retBuilder = new StringBuilder();
        for (String variable : variables) {
            System.out.println("variable " + variable);
            retBuilder.append(hasIdentifiers(variable));
//            System.out.println("has identifiers "+hasIdentifiers);
        }
        return retBuilder;
    }

    private String hasIdentifiers(String variable) {
        StringBuilder getterSetterConstructor = new StringBuilder();
        for (Identifiers identifier : Identifiers.values()) {
//            System.out.println("variable: " + variable + " identifier: " + identifier.getIdentifier());
            if (variable.contains(identifier.getIdentifier())) {
                String[] splitVariables = variable.split(identifier.getIdentifier());
                if (splitVariables != null) {
                    String varNameOrg = splitVariables[splitVariables.length - 1].trim();                  
                    String varName = varNameOrg.split(";")[0];
                    System.out.println("varName " + varName);
                    String[] getterSetterCamelCasedVariable = getCamelCased(varName);
                    getterSetterConstructor.append(getterMethod(identifier.getIdentifier(),getterSetterCamelCasedVariable[0],varName));
                    getterSetterConstructor.append(setterMethod(identifier.getIdentifier(),getterSetterCamelCasedVariable[1],varName));
                }
//                System.out.println("getter method "+getterSetterConstructor);
                return getterSetterConstructor.toString();
            }
        }
//        System.out.println("getter method "+getterSetterConstructor);
        return getterSetterConstructor.toString();
    }
    
    private String getterMethod(String identifier,String getterName,String varName){
        StringBuilder builder = new StringBuilder();
        builder.append("\n\npublic "+identifier+" "+getterName+"(){\n");
        builder.append("\treturn "+varName+";\n}\n");
        return builder.toString();
    }
    
    private String setterMethod(String identifier,String setterName,String varName){
        StringBuilder builder = new StringBuilder();
        builder.append("public void "+setterName+"("+identifier+" "+varName+"){\n");
        builder.append("\tthis."+varName+" = "+varName+";\n}");
        return builder.toString();
    }

    private String[] getCamelCased(String varName) {
        String[] retVal = new String[2];
        char[] varAsChars = new char[varName.length()];
        if (varName.length() > 0) {
            varAsChars = varName.toCharArray();
            char upperChar = toUpperCase(varAsChars[0]);
//            System.out.println("upper cased char "+upperChar);
            varAsChars[0] = upperChar;
        }
        String camelCaseVariable = String.valueOf(varAsChars);
//        System.out.println("camel cased var "+camelCaseVariable);
        retVal[0] = "get"+camelCaseVariable;
        retVal[1] = "set"+camelCaseVariable;
        System.out.println("camel cased getter and setter "+retVal[0]+" :::: "+retVal[1]);
        return retVal;
    }

    private String[] getVariablesWithModifiers(ModelAttribs attrbs) {
        return attrbs.getModelAttribs().split("\n");
    }

    private String getClassInitContents(ModelAttribs attrbs) {
        StringBuilder fileContent = new StringBuilder();
        fileContent.append("package " + PACKAGE_MODEL + ";");
        fileContent.append("\n\n\npublic class " + attrbs.getModelName() + " extends BaseData{");
        fileContent.append("\n\n");
        return fileContent.toString();
    }

    private String getClassEndContents() {
        return "\n\n}";
    }

    private void writeContentToFile(File file, String content) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "utf-8"));
            writer.write(content);
        } catch (IOException ex) {
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private char toUpperCase(char c) {
//        System.out.println("toUpperCase method "+c);
        switch (c) {
            case 'a':
                return 'A';
            case 'b':
                return 'B';
            case 'c':
                return 'C';
            case 'd':
                return 'D';
            case 'e':
                return 'E';
            case 'f':
                return 'F';
            case 'g':
                return 'G';
            case 'h':
                return 'H';
            case 'i':
                return 'I';
            case 'j':
                return 'J';
            case 'k':
                return 'K';
            case 'l':
                return 'L';
            case 'm':
                return 'M';
            case 'n':
                return 'N';
            case 'o':
                return 'O';
            case 'p':
                return 'P';
            case 'q':
                return 'Q';
            case 'r':
                return 'R';
            case 's':
                return 'S';
            case 't':
                return 'T';
            case 'u':
                return 'U';
            case 'v':
                return 'V';
            case 'w':
                return 'W';
            case 'x':
                return 'X';
            case 'y':
                return 'Y';
            case 'z':
                return 'Z';
        }

        return c;
    }
}
