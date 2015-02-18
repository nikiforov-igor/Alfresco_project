package ru.it.lecm.reporting;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Constants {

    public static final String INSERT_ONLY = "INSERT_ONLY";
    public static final String SINGLE_INSTANCE = "SINGLE_INSTANCE";
    public static final String UPDATE_VERSIONED = "UPDATE_VERSIONED";
    public static final String QUERYLANGUAGE = "Lucene";
    public static final String SEPARATOR = "~";
    public static final String property_jndiName = "reporting.db.jndiName";
    public static final String property_audit_maxResults = "reporting.harvest.audit.maxResults";
    public static final String property_noderef_maxResults = "reporting.harvest.noderef.maxResults";
    public static final String property_blacklist = "reporting.harvest.blacklist";
    public static final String property_blockkeys = "reporting.harvest.blockkeys";
    public static final String property_blockNameSpaces = "reporting.harvest.blockNameSpaces";
    public static final String property_storelist = "reporting.harvest.stores";
    public static final String property_invalid_table_names = "reporting.harvest.invalidTableNames";
    public static final String property_invalid_table_chars = "reporting.harvest.invalidTableChars";
    public static final String property_treshold_child_assocs = "reporting.harvest.treshold.child.assocs";
    public static final String property_treshold_soucetarget_assocs = "reporting.harvest.treshold.sourcetarget.assocs";
    public static final String property_harvesting_enabled = "reporting.harvest.enabled";
    public static final String property_execution_enabled = "reporting.harvest.enabled";
    public static final String TABLE_PERSON = "person";
    public static final String TABLE_GROUPS = "groups";
    public static final String TABLE_SITEPERSON = "siteperson";
    public static final String TABLE_WOKFLOW_INSTANCE = "workflowinstance";
    public static final String TABLE_WOKFLOW_TASK = "workflowtask";
    public static final String COLUMN_ORIG_NODEREF = "orig_noderef";
    public static final String COLUMN_MIMETYPE = "mimetype";
    public static final String COLUMN_TIMESTAMP = "event_timestamp";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_SYS_NODE_UUID = "sys_node_uuid";
    public static final String COLUMN_ZONES = "zones";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_SIZE_ORACLE = "docsize";
    public static final int HARVESTING_SAFETY_MARGIN = 5000;
    public static final int MAX_COLUMNNAME_LENGTH_ORACLE = 30;
    public static final int MAX_COLUMNNAME_LENGTH_POSTGRES = 64;
    public static final int MAX_COLUMNNAME_LENGTH_MYSQL = 64;
    public static final String VENDOR_ORACLE = "Oracle";
    public static final String VENDOR_MYSQL = "MySQL";
    public static final String VENDOR_POSTGRES = "PostgreSQL";
    public static final String VENDOR_MSSQL = "not implemented";
    public static final String KEY_MODIFIED = "cm_modified";
    public static final String KEY_CREATED = "cm_created";
    public static final String KEY_VERSION_LABEL = "cm_versionlabel";
    public static final String KEY_ARCHIVED_DATE = "sys_archiveddate";
    public static final String KEY_NODE_UUID = "sys_node_uuid";
    public static final String KEY_STORE_PROTOCOL = "sys_store_protocol";
    private ReportingHelper reportingHelper;
    public static final String REPORTING_PROPERTIES = "alfresco/module/lecm-reporting-repo/reporting-model.properties";
    public static final String REPORTING_CUSTOM_PROPERTIES = "alfresco/extension/reporting-custom-model.properties";
    public static final String MULTIVALUE_SEPERATOR = ",";
    public static String DATE_FORMAT_AUDIT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_MYSQL = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_FORMAT_POSTGRESQL = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_FORMAT_ORACLE = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_MSSQL = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String STATUS_RUNNING = "Running";
    public static final String STATUS_DONE = "Done";
    public static final String TABLE_LASTRUN = "lastsuccessfulrun";
    public static final String COLUMN_LASTRUN = "lastrun";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_TABLENAME = "tablename";

    public static final String COLUMN_PARENT_NODEREF = "parent_noderef";
    public static final String COLUMN_CHILD_NODEREF = "child_noderef";
    public static final String COLUMN_NODEREFS = "noderefs";
    public static final String COLUMN_NODEREF = "noderef";
    public static final String COLUMN_OBJECT_TYPE = "object_type";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_ASPECTS = "aspects";
    public static final String TYPE_TABLES = "type_tables";

    public static SimpleDateFormat getAuditDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_AUDIT);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf;
    }

}
