package ru.it.lecm.reporting.webscript;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.reporting.db.DatabaseHelperBean;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

public class ReportingStatus extends AbstractWebScript {

   private DatabaseHelperBean dbhb = null;
   private static Log logger = LogFactory.getLog(ReportingStatus.class);


   private String postFix(String base, int size, String filler) {
       StringBuilder stringBuilder = new StringBuilder(base);
      while(stringBuilder.length() < size) {
          stringBuilder.append(filler);
      }

      return stringBuilder.toString();
   }

   public void execute(WebScriptRequest arg0, WebScriptResponse pResponse) throws IOException {
      try {
         Map e = this.dbhb.getShowTablesDetails();
         Iterator keys = (new TreeSet(e.keySet())).iterator();
         JSONObject mainObject = new JSONObject();
         JSONArray mainArray = new JSONArray();

         while(keys.hasNext()) {
            String key = (String)keys.next();
            if(!"lastsuccessfulrun".equalsIgnoreCase(key) && !"type_tables".equals(key) && key != null && !"".equals(key)) {
               key = key.trim();
               JSONObject rowObject = new JSONObject();
               rowObject.put("table", this.postFix(key, 5, " "));
               logger.debug("Getting key=" + key);
               String keyList = (String)e.get(key);
               logger.debug("Getting values=" + keyList);

               try {
                  String[] e1 = keyList.split(",");
                  int tSize = e1.length;
                  if(tSize > 0) {
                     rowObject.put("last_run", e1[0]);
                  }

                  if(tSize > 1) {
                     rowObject.put("status", e1[1]);
                  }

                  if(tSize > 2) {
                     rowObject.put("number_of_rows", e1[2]);
                  }

                  if(tSize > 3) {
                     rowObject.put("number_of_latest", e1[3]);
                  }

                  if(tSize > 4) {
                     rowObject.put("number_of_non_latest", e1[4]);
                  }

                  if(tSize > 5) {
                     rowObject.put("number_in_workspace", e1[5]);
                  }

                  if(tSize > 6) {
                     rowObject.put("number_in_archivespace", e1[6]);
                  }
               } catch (Exception var12) {
                  logger.fatal(var12.getMessage());
               }

               mainArray.add(rowObject);
            }
         }

         mainObject.put("result", mainArray);
         pResponse.getWriter().write(mainObject.toJSONString());
      } catch (IOException var13) {
         var13.printStackTrace();
      }

   }

   public void setDatabaseHelperBean(DatabaseHelperBean databaseHelperBean) {
      this.dbhb = databaseHelperBean;
   }

}
