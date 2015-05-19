/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bijesh.codeautomationtool.ui.helper;

import com.bijesh.codeautomationtool.commonutils.BasicUtils;
import com.bijesh.codeautomationtool.constants.CodeConstants;
import static com.bijesh.codeautomationtool.constants.Package.PACKAGE_MODEL;
import com.bijesh.codeautomationtool.enums.Identifiers;
import com.bijesh.codeautomationtool.models.ModelAttribs;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Bijesh
 */
public class TableContentHelper implements com.bijesh.codeautomationtool.constants.Package,CodeConstants{
    
    private String mTableName;
    private ArrayList<String> mQueryColumnsList = new ArrayList<String>();
    private String mTABLE,mCOLUMN_ID;
    
    public TableContentHelper(String tableClassName){
        System.out.println("$$$ inside table creation");
        this.mTableName = tableClassName;
    }
    
     public String getTableClassInitContents(ModelAttribs attrbs) {
        StringBuilder fileContent = new StringBuilder();
        fileContent.append("package " + PACKAGE_TABLE + ";");
        fileContent.append(getImports());
        fileContent.append("\n\n\npublic class " + mTableName + " extends BaseTable{");
        fileContent.append("\n\n");
        fileContent.append("\tprivate static final String TAG = "+mTableName+".class.getCanonicalName();");
        return fileContent.toString();
    }
     
     private String getImports(){
         String imports = "\n\nimport android.database.sqlite.SQLiteDatabase;\n" +
                          "import android.util.Log;";
         return imports;
     }
     
     public String createTableName(String modelName){
         StringBuilder builder = new StringBuilder();
         mTABLE = "TABLE_NAME_"+modelName.toUpperCase();
         builder.append("\n\n\tpublic static final String TABLE_NAME_"+modelName.toUpperCase()+" = "+"\""+modelName+"\";");
         return builder.toString();
     }
     
     public String createColumns(ModelAttribs attrbs){
         StringBuilder builder = new StringBuilder();
//         column _id creation
         mCOLUMN_ID = attrbs.getModelName().toUpperCase()+columnNameId;
         builder.append("\n\n\t"+publicStaticFinal_String+column_+attrbs.getModelName().toUpperCase()+
                 columnNameId+" = "+doubleQuotes+columnValueId+doubleQuotes+";");
         
         String[] allVariablesWithModifiers = BasicUtils.getVariablesWithModifiers(attrbs);
         builder.append(getColumns(allVariablesWithModifiers));
         
         return builder.toString();
     }
     
     public String generateCreateQuery(ModelAttribs modelAttrbs){
         StringBuilder builder = new StringBuilder();
         builder.append("\n\n\t"+publicStaticFinal_String+mTableName.toUpperCase()+"_TABLE_CREATE = "+
                 doubleQuotes+"create table if not exists "+doubleQuotes+"+ "+mTABLE+"+"+doubleQuotes+"("+
                 doubleQuotes+"+\n");
         builder.append("\t\t"+column_+mCOLUMN_ID+"+"+doubleQuotes
                 +" INTEGER PRIMARY KEY AUTOINCREMENT,"+doubleQuotes+"+\n");
         
         for(int i=0;i<mQueryColumnsList.size();i++){
             String actualVal = mQueryColumnsList.get(i);
             String[] allVal = actualVal.split(" ");
             if(i == mQueryColumnsList.size()-1){
                 builder.append("\n\t\t"+allVal[0]+"+"+doubleQuotes+" "+allVal[1]+doubleQuotes+"+");
             }else{
               builder.append("\n\t\t"+allVal[0]+"+"+doubleQuotes+" "+allVal[1]+", "+doubleQuotes+"+");
             }          
         }
         builder.append("\n\t\t"+doubleQuotes+")"+doubleQuotes+";");
         
         return builder.toString();
     }
     
     public String generateOnCreateAndUpgradeMethod(){
         StringBuilder builder = new StringBuilder();
         builder.append("\n\n\n\t@Override\n\t public void onCreate(SQLiteDatabase database) {\n");
         builder.append("\t\tdatabase.execSQL("+mTABLE+");\n");
         builder.append("\t\tLog.d(TAG,"+doubleQuotes+"created table "+doubleQuotes+"+"+mTABLE+");\n\t}");
         
         
//         onupgrade 
         
         builder.append("\n\n\n\t@Override\n\t public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion) {\n");
         builder.append("\t\tdatabase.execSQL("+doubleQuotes+"DROP TABLE IF EXISTS "+doubleQuotes+" + "+mTABLE+");\n");
         builder.append("\t\tonCreate(database);\n\t}");
         return builder.toString();
     }
     
     private String getColumns(String[] variables){
         StringBuilder builder = new StringBuilder();
         for(String variable:variables){
             for (Identifiers identifier : Identifiers.values()) {
                System.out.println("variable: " + variable + " identifier: " + identifier.getIdentifier());
              if (variable.contains(identifier.getIdentifier())) {
                   String[] splitVariables = variable.split(identifier.getIdentifier());
                if (splitVariables != null) {
                    String varNameOrg = splitVariables[splitVariables.length - 1].trim();                  
                    String varName = varNameOrg.split(";")[0];
                    System.out.println("varName " + varName);
                    builder.append("\n\t"+publicStaticFinal_String+column_+varName.toUpperCase()+" = "+doubleQuotes
                    +varName+doubleQuotes+";");
                    addToList(identifier,varName.toUpperCase());
                    break;
                  }
                }
             }
         }
         System.out.println("$$$ query list"+mQueryColumnsList);
         return builder.toString();
     }
     
     private void addToList(Identifiers identifier,String columnName){
         switch(identifier){
             case STRING:
                 mQueryColumnsList.add(column_+columnName+" TEXT");
                 break;
             default:
                 mQueryColumnsList.add(column_+columnName+" INTEGER");
                 break;
         }
     }
     
}
