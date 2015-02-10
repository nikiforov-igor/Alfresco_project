package org.alfresco.reporting;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;

public class FileHelper {

   public static boolean isWindows() {
      String os = System.getProperty("os.name").toLowerCase();
      return os.indexOf("win") >= 0;
   }

   public static boolean isMac() {
      String os = System.getProperty("os.name").toLowerCase();
      return os.indexOf("mac") >= 0;
   }

   public static boolean isUnix() {
      String os = System.getProperty("os.name").toLowerCase();
      return os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0;
   }

   public static boolean isSolaris() {
      String os = System.getProperty("os.name").toLowerCase();
      return os.indexOf("sunos") >= 0;
   }

   public static NodeRef fileToAlfrescoNode(NodeRef nodeRef, File file, ContentService contentService) throws IOException {
      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
      ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
      writer.putContent(bis);
      return nodeRef;
   }
}
