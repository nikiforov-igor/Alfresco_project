package ru.it.lecm.reporting.mybatis;


public class UpdateWhere {

   private String tablename;
   private String updateClause;
   private String whereClause;


   public UpdateWhere(String table, String update, String where) {
      if(table != null) {
         this.setTablename(table);
      }

      if(update != null) {
         this.setUpdateClause(update);
      }

      if(where != null) {
         this.setWhereClause(where);
      }

   }

   public String getTablename() {
      return this.tablename;
   }

   public void setTablename(String tablename) {
      this.tablename = tablename.toLowerCase();
   }

   public String getUpdateClause() {
      return this.updateClause;
   }

   public void setUpdateClause(String updateClause) {
      this.updateClause = updateClause;
   }

   public String getWhereClause() {
      return this.whereClause;
   }

   public void setWhereClause(String whereClause) {
      this.whereClause = whereClause;
   }
}
