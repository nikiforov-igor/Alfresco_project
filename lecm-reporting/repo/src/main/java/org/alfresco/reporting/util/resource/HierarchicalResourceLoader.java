package org.alfresco.reporting.util.resource;

import javax.sql.DataSource;
import org.alfresco.util.PropertyCheck;
import org.springframework.core.io.Resource;

public class HierarchicalResourceLoader extends org.alfresco.util.resource.HierarchicalResourceLoader {

   public static final String DEFAULT_DIALECT_PLACEHOLDER = "#reporting.resource.dialect#";
   public static final String DEFAULT_DIALECT_REGEX = "\\#reporting\\.resource\\.dialect\\#";
   private String dialectBaseClass;
   private String dialectClass;
   private DataSource datasource;
   private String mySqlClassName = "org.hibernate.dialect.MySQLInnoDBDialect";
   private String postgreSqlClassName = "org.hibernate.dialect.PostgreSQLDialect";
   private String oracleClassName;
   private String msSqlClassName;
   private String resourcePath = "";
   private String databaseVendor = "";


   private void setDatabaseVendor(String vendor) {
      this.databaseVendor = vendor;
   }

   public String getDatabaseVendor() {
      return this.databaseVendor;
   }

   public void setDialectBaseClass(String className) {
      this.dialectBaseClass = className;
   }

   public void setDialectClass(String className) {
      this.dialectClass = className;
   }

   public void setDatasource(DataSource datasource) {
      this.datasource = datasource;
      this.dialectClass = "";
   }

   public void setMySqlClassName(String mySqlClassName) {
      this.mySqlClassName = mySqlClassName;
   }

   public void setPostgreSqlClassName(String postgreSqlClassName) {
      this.postgreSqlClassName = postgreSqlClassName;
   }

   public void setOracleClassName(String oracleSqlClassName) {
      this.oracleClassName = oracleSqlClassName;
   }

   public void setMsSqlClassName(String msSqlClassName) {
      this.msSqlClassName = msSqlClassName;
   }

   public void afterPropertiesSet() throws Exception {
      String database = this.datasource.getConnection().getMetaData().getDatabaseProductName();
      this.setDatabaseVendor(database);
      if("PostgreSQL".equalsIgnoreCase(database)) {
         this.dialectClass = this.postgreSqlClassName;
      }

      if("MySQL".equalsIgnoreCase(database)) {
         this.dialectClass = this.mySqlClassName;
      }

      if("oracle".equalsIgnoreCase(database)) {
         this.dialectClass = this.oracleClassName;
      }

      if("sqlserver".equalsIgnoreCase(database)) {
         this.dialectClass = this.msSqlClassName;
      }

      PropertyCheck.mandatory(super.getClass(), "dialectBaseClass", this.dialectBaseClass);
      PropertyCheck.mandatory(super.getClass(), "dialectClass", this.dialectClass);
   }

   public Resource getResource(String location) {
      if(this.dialectClass != null && this.dialectBaseClass != null) {
         String dialectBaseClassStr = this.dialectBaseClass;
         String dialectClassStr;
         if(!PropertyCheck.isValidPropertyString(this.dialectBaseClass)) {
            dialectClassStr = PropertyCheck.getPropertyName(this.dialectBaseClass);
            dialectBaseClassStr = System.getProperty(dialectClassStr, this.dialectBaseClass);
         }

         dialectClassStr = this.dialectClass;
         if(!PropertyCheck.isValidPropertyString(this.dialectClass)) {
            String dialectBaseClazz = PropertyCheck.getPropertyName(this.dialectClass);
            dialectClassStr = System.getProperty(dialectBaseClazz, this.dialectClass);
         }

         Class dialectBaseClazz1;
         try {
            dialectBaseClazz1 = Class.forName(dialectBaseClassStr);
         } catch (ClassNotFoundException var10) {
            throw new RuntimeException("Dialect base class not found: " + dialectBaseClassStr);
         }

         Class dialectClazz;
         try {
            dialectClazz = Class.forName(dialectClassStr);
         } catch (ClassNotFoundException var9) {
            throw new RuntimeException("Dialect class not found: " + dialectClassStr);
         }

         if(!Object.class.isAssignableFrom(dialectBaseClazz1)) {
            throw new RuntimeException("Dialect base class must be derived from java.lang.Object: " + dialectBaseClazz1.getName());
         } else if(!Object.class.isAssignableFrom(dialectClazz)) {
            throw new RuntimeException("Dialect class must be derived from java.lang.Object: " + dialectClazz.getName());
         } else if(!dialectBaseClazz1.isAssignableFrom(dialectClazz)) {
            throw new RuntimeException("Non-existent HierarchicalResourceLoader hierarchy: " + dialectBaseClazz1.getName() + " is not a superclass of " + dialectClazz);
         } else {
            Class clazz = dialectClazz;
            Resource resource = null;

            while(resource == null) {
               String newLocation = location.replaceAll("\\#reporting\\.resource\\.dialect\\#", clazz.getName());
               resource = super.getResource(newLocation);
               if(resource != null && resource.exists()) {
                  break;
               }

               resource = null;
               if(clazz.equals(dialectBaseClazz1)) {
                  break;
               }

               clazz = clazz.getSuperclass();
               if(clazz == null) {
                  throw new RuntimeException("Non-existent HierarchicalResourceLoaderBean hierarchy: " + dialectBaseClazz1.getName() + " is not a superclass of " + dialectClazz);
               }
            }

            this.setResourcePath(resource.toString());
            return resource;
         }
      } else {
         return super.getResource(location);
      }
   }

   private void setResourcePath(String path) {
      this.resourcePath = path;
   }

   public String getResourcePath() {
      return this.resourcePath;
   }
}
