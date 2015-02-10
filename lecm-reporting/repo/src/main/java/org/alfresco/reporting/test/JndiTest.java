package org.alfresco.reporting.test;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JndiTest {

   public static void main(String[] args) throws NamingException {
      System.out.println("start JndiTest");
      InitialContext ctx = new InitialContext();
      System.out.println("Done initialContext");
      DataSource ds = (DataSource)ctx.lookup("ibfdReporting");
      System.out.println("Done lookup");
      System.out.println(ds.toString());
   }
}
