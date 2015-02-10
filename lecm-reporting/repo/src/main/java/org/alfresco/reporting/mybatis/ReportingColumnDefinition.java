package org.alfresco.reporting.mybatis;


public class ReportingColumnDefinition {

   private String tablename;
   private String columnname;
   private String columntype;


   public ReportingColumnDefinition(String tablename, String columnname, String columntype) {
      this.setTablename(tablename);
      this.setColumnname(columnname);
      this.setColumntype(columntype);
   }

   public String getTablename() {
      return this.tablename;
   }

   public void setTablename(String tablename) {
      this.tablename = tablename;
   }

   public String getColumnname() {
      return this.columnname;
   }

   public void setColumnname(String columnname) {
      this.columnname = columnname;
   }

   public String getColumntype() {
      return this.columntype;
   }

   public void setColumntype(String columntype) {
      this.columntype = columntype;
   }
}
