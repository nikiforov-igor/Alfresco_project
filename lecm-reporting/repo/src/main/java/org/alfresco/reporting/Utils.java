package org.alfresco.reporting;

import java.util.Set;

public class Utils {

   public static String setToString(Set inputSet) {
      String returnString = "";
      if(inputSet != null) {
         returnString = inputSet.toString();
         if(returnString.startsWith("[")) {
            returnString = returnString.substring(1, returnString.length());
         }

         if(returnString.endsWith("]")) {
            returnString = returnString.substring(0, returnString.length() - 1);
         }
      }

      return returnString;
   }
}
