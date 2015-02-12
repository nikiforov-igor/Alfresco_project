package ru.it.lecm.reporting;

import org.alfresco.error.AlfrescoRuntimeException;

public class ReportingException extends AlfrescoRuntimeException {

   private static final long serialVersionUID = 13497349832482745L;


   public ReportingException(String msgId) {
      super(msgId);
   }
}
