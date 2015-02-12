package ru.it.lecm.reporting.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import ru.it.lecm.reporting.mybatis.impl.ReportingDAOImpl;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ReportingTest {

   private static Log logger = LogFactory.getLog(ReportingDAOImpl.class);
   private static final String BASE_URL = "http://localhost:8080/alfresco/service/reporting";
   private static final String USERNAME = "admin";
   private static final String PASSWORD = "admin";
   private static final String TEST_DB = "test-db";
   private static final String TEST_REPO = "test-repo";
   private static final String COUNT = "count";
   private static final String DROP = "drop";
   private static final String HARVEST = "harvest";
   private static final String CLEAR = "clear";
   private static final String UPDATE = "update";
   private static final String DELETE = "delete";
   private static final String VERSIONS = "versions";
   private static final String ALL = "all";
   private static final String LATEST = "latest";
   private static final String NONLATEST = "nonlatest";
   private static final String WORKSPACE = "workspace";
   private static final String ARCHIVE = "archive";
   private static String ALL_DOCUMENT_TABLES = "document, calendar,link,datalistitem";
   private static String ALL_FOLDER_TABLES = "datalist,folder";
   private static String ALL_PEOPLE_TABLES = "person,sitepersons,groups";
   private static String ALL_AUDIT_TABLES = "";
   private static int TIME_BEFORE_HARVEST = 20000;
   static final boolean isShort = true;


   public static JSONObject getRequest(String script, String method, String table, String details, String noderef) {
      JSONObject json = new JSONObject();

      try {
         String e = "http://localhost:8080/alfresco/service/reporting/" + script + ".json" + "?method=" + method;
         if(table != null) {
            e = e + "&table=" + table;
         }

         if(details != null) {
            e = e + "&details=" + details;
         }

         if(noderef != null) {
            e = e + "&noderef=" + noderef;
         }

         URL url = new URL(e);
         HttpURLConnection conn = (HttpURLConnection)url.openConnection();
         conn.setRequestMethod("GET");
         conn.setRequestProperty("Accept", "text/html");
         String userpass = "admin:admin";
         String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
         conn.setRequestProperty("Authorization", basicAuth);
         if(conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
         }

         BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         StringBuffer totalOutput = new StringBuffer();

         String output;
         while((output = br.readLine()) != null) {
            totalOutput.append(output);
         }

         json = new JSONObject(totalOutput.toString());
         conn.disconnect();
      } catch (MalformedURLException var14) {
         var14.printStackTrace();
      } catch (IOException var15) {
         var15.printStackTrace();
      } catch (JSONException var16) {
         var16.printStackTrace();
      }

      return json;
   }

   public static void makeNoise(String noise) {
      System.out.println("**FAILED** " + noise);
   }

   public static void assertCompareNoderef(TestCycleResults previousRun, TestCycleResults nextRun, TestCycleResults expectations) {
      if(expectations.getHarvested() > -100 && previousRun.getHarvested() != nextRun.getHarvested() - expectations.getHarvested()) {
         makeNoise("against harvested");
      }

      if(expectations.getDbIsLatest() > -100 && previousRun.getDbIsLatest() != nextRun.getDbIsLatest() - expectations.getDbIsLatest()) {
         makeNoise("against isLatest");
      }

      if(expectations.getDbIsNonLatest() > -100 && previousRun.getDbIsNonLatest() != nextRun.getDbIsNonLatest() - expectations.getDbIsNonLatest()) {
         makeNoise("against isNonLatest");
      }

      if(expectations.getDbWorkspace() > -100 && previousRun.getDbWorkspace() != nextRun.getDbWorkspace() - expectations.getDbWorkspace()) {
         makeNoise("against isWorkspace");
      }

      if(expectations.getDbArchive() > -100 && previousRun.getDbArchive() != nextRun.getDbArchive() - expectations.getDbArchive()) {
         makeNoise("against isArchive");
      }

      if(expectations.getDbAll() > -100 && previousRun.getDbAll() != nextRun.getDbAll() - expectations.getDbAll()) {
         makeNoise("against isAll");
      }

      if(expectations.getDbVersions() > -100 && previousRun.getDbVersions() != nextRun.getDbVersions() - expectations.getDbVersions()) {
         makeNoise("against getVersion");
      }

      if(nextRun.getDbIsLatest() + nextRun.getDbIsNonLatest() != nextRun.getDbAll()) {
         makeNoise("isLatest + isNonLatest!= all");
      }

      if(nextRun.getDbWorkspace() + nextRun.getDbArchive() != nextRun.getDbAll()) {
         makeNoise("Workspace + Archive != all");
      }

   }

   private static void testycleNoderefFullRunDocument() throws JSONException {
      String[] tables = ALL_DOCUMENT_TABLES.split(",");
      String[] arr$ = tables;
      int len$ = tables.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String table = arr$[i$];
         table = table.trim();
         System.out.println("### Dropping reporting table " + table);
         JSONObject json = getRequest("test-db", "clear", table, (String)null, (String)null);
         json = getRequest("test-db", "drop", table, (String)null, (String)null);
         String vendor = json.getString("vendor");
         System.out.println("### Reporting database powered by: " + vendor);
         TestCycleResults prevRun = testCyclusNoderef(table, (String)null);
         System.out.print("Creating new " + table + "... ");
         json = getRequest("test-repo", "update", table, "create", (String)null);
         String noderef = json.getString("noderef");
         System.out.println(noderef);

         try {
            Thread.sleep((long)TIME_BEFORE_HARVEST);
         } catch (InterruptedException var13) {
            Thread.currentThread().interrupt();
         }

         TestCycleResults nextRun = testCyclusNoderef(table, noderef);
         TestCycleResults expectations = new TestCycleResults(1, 1, 0, 1, -999, 1, -999);
         assertCompareNoderef(prevRun, nextRun, expectations);
         prevRun = nextRun;
         System.out.println("Update title " + table + "... ");
         getRequest("test-repo", "update", table, "updateTitle", noderef);

         try {
            Thread.sleep((long)TIME_BEFORE_HARVEST);
         } catch (InterruptedException var12) {
            Thread.currentThread().interrupt();
         }

         nextRun = testCyclusNoderef(table, noderef);
         expectations = new TestCycleResults(0, 0, 1, 1, 0, 1, 1);
         assertCompareNoderef(prevRun, nextRun, expectations);
      }

   }

   private static void testycleNoderefFullRunFolder() throws JSONException {
      String[] tables = ALL_FOLDER_TABLES.split(",");
      String[] arr$ = tables;
      int len$ = tables.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String table = arr$[i$];
         table = table.trim();
         System.out.println("### Dropping reporting table " + table);
         JSONObject json = getRequest("test-db", "clear", table, (String)null, (String)null);
         json = getRequest("test-db", "drop", table, (String)null, (String)null);
         String vendor = json.getString("vendor");
         System.out.println("### Reporting database powered by: " + vendor);
         TestCycleResults prevRun = testCyclusNoderef(table, (String)null);
         System.out.print("Creating new " + table + "... ");
         json = getRequest("test-repo", "update", table, "create", (String)null);
         String noderef = json.getString("noderef");
         System.out.println(noderef);

         try {
            Thread.sleep((long)TIME_BEFORE_HARVEST);
         } catch (InterruptedException var13) {
            Thread.currentThread().interrupt();
         }

         TestCycleResults nextRun = testCyclusNoderef(table, noderef);
         TestCycleResults expectations;
         if(table.equals("folder")) {
            expectations = new TestCycleResults(1, 1, 1, 2, 0, 2, -999);
         } else {
            expectations = new TestCycleResults(1, 1, 0, 1, 0, 1, -999);
         }

         assertCompareNoderef(prevRun, nextRun, expectations);
         prevRun = nextRun;
         System.out.println("Update title " + table + "... ");
         getRequest("test-repo", "update", table, "updateTitle", noderef);

         try {
            Thread.sleep((long)TIME_BEFORE_HARVEST);
         } catch (InterruptedException var12) {
            Thread.currentThread().interrupt();
         }

         nextRun = testCyclusNoderef(table, noderef);
         expectations = new TestCycleResults(0, 0, 1, 1, 0, 1, -999);
         assertCompareNoderef(prevRun, nextRun, expectations);
      }

   }

   public static TestCycleResults testCyclusNoderef(String table, String noderef) throws JSONException {
      int dbVersions = 0;
      System.out.print("Harvest " + table);
      int repoListSize = Integer.parseInt(getRequest("test-repo", "harvest", table, (String)null, (String)null).getString("amount"));
      System.out.print("\t repo: " + repoListSize);
      int dbIsLatest = Integer.parseInt(getRequest("test-db", "count", table, "latest", (String)null).getString("amount"));
      System.out.print("\t latest: " + dbIsLatest);
      int dbIsNonLatest = Integer.parseInt(getRequest("test-db", "count", table, "nonlatest", (String)null).getString("amount"));
      System.out.print("\t nonLatest: " + dbIsNonLatest);
      int dbWorkspace = Integer.parseInt(getRequest("test-db", "count", table, "workspace", (String)null).getString("amount"));
      System.out.print("\t workspace: " + dbWorkspace);
      int dbArchive = Integer.parseInt(getRequest("test-db", "count", table, "archive", (String)null).getString("amount"));
      System.out.print("\t archive: " + dbArchive);
      int dbAll = Integer.parseInt(getRequest("test-db", "count", table, "all", (String)null).getString("amount"));
      System.out.print("\t all: " + dbAll);
      if(noderef != null) {
         dbVersions = Integer.parseInt(getRequest("test-db", "count", table, "versions", noderef).getString("amount"));
         System.out.println("\t versions: " + dbVersions);
      } else {
         System.out.println("");
      }

      TestCycleResults tsr = new TestCycleResults(repoListSize, dbIsLatest, dbIsNonLatest, dbWorkspace, dbArchive, dbAll, dbVersions);
      return tsr;
   }

   public static void main(String[] args) throws JSONException {
      JSONObject json = getRequest("test-db", "drop", (String)null, "all", (String)null);
      testycleNoderefFullRunDocument();
   }

}
