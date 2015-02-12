package ru.it.lecm.reporting.mybatis;


public class LastRunDefinition {

   private String lastrun;
   private String tablename;
   private String status;


   public LastRunDefinition(String tablename, String status, String lastrun) {
      this.setTablename(tablename);
      this.setStatus(status);
      this.setLastrun(lastrun);
   }

   public String getLastrun() {
      return this.lastrun;
   }

   public void setLastrun(String lastrun) {
      this.lastrun = lastrun;
   }

   public String getTablename() {
      return this.tablename;
   }

   public void setTablename(String tablename) {
      this.tablename = tablename.toLowerCase();
   }

   public String getStatus() {
      return this.status;
   }

   public void setStatus(String status) {
      this.status = status;
   }
}
