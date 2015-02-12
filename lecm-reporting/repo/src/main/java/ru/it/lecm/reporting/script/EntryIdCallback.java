package ru.it.lecm.reporting.script;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.audit.AuditService.AuditQueryCallback;
import ru.it.lecm.reporting.ReportLine;

import java.util.Map;
import java.util.Properties;

public class EntryIdCallback implements AuditQueryCallback {

   private final boolean valuesRequired;
   private Long entryId;
   private String LOGIN_AUDIT_APPLICATION = "ReportingLoginAudit";
   private ReportLine rl;
   private Properties replacementTypes;
   private String tableName;
   private Properties cache = new Properties();


   public EntryIdCallback(boolean valuesRequired, ReportLine rl, Properties replacementTypes, String tableName, String feedName) {
      this.valuesRequired = valuesRequired;
      this.rl = rl;
      this.replacementTypes = replacementTypes;
      this.tableName = tableName;
      this.LOGIN_AUDIT_APPLICATION = feedName;
   }

   public String getEntryId() {
      return this.entryId == null?null:this.entryId.toString();
   }

   public boolean valuesRequired() {
      return this.valuesRequired;
   }

   public final boolean handleAuditEntry(Long entryId, String applicationName, String user, long time, Map values) {
      return applicationName.equals(this.LOGIN_AUDIT_APPLICATION)?this.handleAuditEntry(entryId, user, time, values):true;
   }

   public boolean handleAuditEntry(Long entryId, String user, long time, Map values) {
      this.entryId = entryId;
      return true;
   }

   public boolean handleAuditEntryError(Long entryId, String errorMsg, Throwable error) {
      throw new AlfrescoRuntimeException("Audit entry " + entryId + ": " + errorMsg, error);
   }

   public Properties getReplacementTypes() {
      return this.replacementTypes;
   }

   public ReportLine getRl() {
      return this.rl;
   }

   public String getTableName() {
      return this.tableName;
   }

   public Properties getCache() {
      return this.cache;
   }

   public void addToCache(String key, String value) {
      this.cache.setProperty(key, value);
   }
}
