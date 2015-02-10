package org.alfresco.reporting.mybatis;


public class InsertInto {

   private String tablename;
   private String keys;
   private String values;


   public InsertInto(String table, String keys, String values) {
      if(table != null) {
         this.setTablename(table);
      }

      if(keys != null) {
         this.setKeys(keys);
      }

      if(values != null) {
         this.setValues(values);
      }

   }

   public String getTablename() {
      return this.tablename;
   }

   public void setTablename(String tablename) {
      this.tablename = tablename.toLowerCase();
   }

   public String getKeys() {
      return this.keys;
   }

   public void setKeys(String keys) {
      this.keys = keys;
   }

   public String getValues() {
      return this.values;
   }

   public void setValues(String values) {
      this.values = values;
   }
}
