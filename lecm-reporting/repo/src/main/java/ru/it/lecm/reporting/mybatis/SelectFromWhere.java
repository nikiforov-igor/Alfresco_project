package ru.it.lecm.reporting.mybatis;


public class SelectFromWhere {

   private String select;
   private String from;
   private String where;
   private String orderby;
   private String groupby;
   private String andwhere;
   private String andandwhere;
   private String database;


   public SelectFromWhere(String select, String from, String where) {
      if(select != null) {
         this.setSelect(select);
      }

      if(from != null) {
         this.setFrom(from);
      }

      if(where != null) {
         this.setWhere(where);
      }

   }

   public String getDatabase() {
      return this.database;
   }

   public void setDatabase(String database) {
      this.database = database;
   }

   public String getSelect() {
      return this.select;
   }

   public void setSelect(String select) {
      this.select = select;
   }

   public void setTablename(String tablename) {
      this.from = tablename;
   }

   public String getTablename() {
      return this.from;
   }

   public String getFrom() {
      return this.from;
   }

   public void setFrom(String from) {
      this.from = from;
   }

   public String getWhere() {
      return this.where;
   }

   public void setWhere(String where) {
      this.where = where;
   }

   public String getAndwhere() {
      return this.andwhere;
   }

   public void setAndwhere(String andwhere) {
      this.andwhere = andwhere;
   }

   public String getAndandwhere() {
      return this.andandwhere;
   }

   public void setAndandwhere(String andandwhere) {
      this.andandwhere = andandwhere;
   }

   public String getOrderby() {
      return this.orderby;
   }

   public void setOrderby(String orderby) {
      this.orderby = orderby;
   }

   public String getGroupby() {
      return this.groupby;
   }

   public void setGroupby(String groupby) {
      this.groupby = groupby;
   }
}
