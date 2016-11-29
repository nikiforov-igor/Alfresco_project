package ru.it.lecm.search.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.springframework.context.ApplicationEvent;

/**
 * User: DBashmakov
 * Date: 07.05.2015
 * Time: 14:05
 */
public class SearchEditorService extends BaseBean {
    final public static char CH_WALL = '\\'; // символ экранировки
    public static final String DOC_TYPE = "docType";

    private final static Log logger = LogFactory.getLog(SearchEditorService.class);

    final static String DATE_FMTISO8601 = "yyyy-MM-dd'T'HH:mm";
    final static public SimpleDateFormat DateFormatISO8601 = new SimpleDateFormat(DATE_FMTISO8601);

    final static String DATE_FMTWEBSCRIPT = "yyyy-MM-dd'T'HH:mm:ss.SSSz"; // like "2013-04-03T00:00:00.000GMT+06:00"
    final static public SimpleDateFormat DateFormatWebScript = new SimpleDateFormat(DATE_FMTWEBSCRIPT);

    final static String DATE_FMT_YMD_HM = "yyyy-MM-dd HH:mm";
    final static public SimpleDateFormat DateFormatYMD_HM = new SimpleDateFormat(DATE_FMT_YMD_HM);

    final static String DATE_FMT_YMD_HMS = "yyyy-MM-dd'T'HH:mm:ss";
    final static public SimpleDateFormat DateFormat_YMD_HMS = new SimpleDateFormat(DATE_FMT_YMD_HMS);

    final static String DATE_FMT_YMD = "yyyy-MM-dd";
    final static public SimpleDateFormat DateFormat_YMD = new SimpleDateFormat(DATE_FMT_YMD);

    final static SimpleDateFormat[] FORMATS = {DateFormatISO8601, DateFormatWebScript, DateFormatYMD_HM, DateFormat_YMD_HMS, DateFormat_YMD};

    public static final Pattern MULTIPLE_NOT_QUERY = Pattern.compile("^NOT[\\s]+.*(?=\\sOR\\s|\\sAND\\s|\\s\\+|\\s\\-)");

    public static final String LECM_SEARCH_QUERIES_ROOT_ID = "LECM_SEARCH_QUERIES_ROOT_ID";
    private static final String LECM_SEARCH_QUERIES_STORE_ID = "Личные поисковые запросы";

    public final static String SEARCH_QUERIES_NAMESPACE = "http://www.it.ru/logicECM/lecm/search-queries/1.0";
    public final static QName TYPE_SEARCH_QUERY_DIC = QName.createQName(SEARCH_QUERIES_NAMESPACE, "dic");
    public final static QName PROP_SEARCH_QUERY_QUERY = QName.createQName(SEARCH_QUERIES_NAMESPACE, "query");
    public final static QName PROP_SEARCH_QUERY_SETTING = QName.createQName(SEARCH_QUERIES_NAMESPACE, "query-setting");

    private OrgstructureBean orgstructureBean;

    public OrgstructureBean getOrgstructureBean() {
        return orgstructureBean;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public enum Types {
        ASSOC("association") {
            @Override
            public String getFTSPreparedValue(String value) {
                return value != null && value.length() > 0 ? ("'*" + value + "*'") : "''";
            }
        },
        TEXT("text") {
            @Override
            public String getFTSPreparedValue(String value) {
                return value != null ? ("'" + value + "'") : "'*'";
            }
        },

        DATE("date") {
            @Override
            public String getFTSPreparedValue(String value) {
                Date valueDate = tryMakeDate(value);
                if (valueDate != null) {
                    return value != null ? ("'" + DateFormat_YMD.format(valueDate) + "'") : "*";
                }
                return "''";
            }
        },

        DATETIME("datetime") {
            @Override
            public String getFTSPreparedValue(String value) {
                Date valueDate = tryMakeDate(value);
                if (valueDate != null) {
                    if (value != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(valueDate);

                        if (cal.get(Calendar.HOUR) == 0 && cal.get(Calendar.MINUTE) == 0) {
                            return ("'" + DateFormat_YMD.format(valueDate) + "'");
                        } else {
                            return ("'" + DateFormatISO8601.format(valueDate) + "'");
                        }
                    } else {
                        return "*";
                    }
                }
                return "''";
            }
        },
        BOOL("boolean") {},

        DOUBLE("double") {},

        LONG("long") {},

        INTEGER("int") {},

        FLOAT("float") {}
        ;

        final private String alfDataType;

        Types(String alfDataType) {
            this.alfDataType = alfDataType;
        }

        public String getFTSPreparedValue(String value) {
            return value != null ? value : "*";
        }

        public static Types findType(String alfDataType) {
            if (alfDataType != null) {
                // поиск точного соот-вия
                for (Types t : values()) {
                    if (alfDataType.equalsIgnoreCase(t.alfDataType))
                        return t;
                }
            }
            return Types.TEXT;
        }


        protected static Date tryMakeDate(final String value) {
            if (value == null || value.isEmpty()){
                return null;
            }
            for (SimpleDateFormat fmt : FORMATS) {
                try {
                    return fmt.parse(value);
                } catch (ParseException ignored) {
                }
            }

            return null;
        }
    }

    public enum Operations {
        EQL_ASSOC () {
            @Override
            public String getFTSQuery(String field, String value, Types type) {
                StringBuilder query = new StringBuilder();
                String fieldWithProtect = doCharsProtection(field + "-ref", ":-");
                if (value != null) {
                    String[] values = value.split(",");
                    for (String val : values) {
                        //if (val.length() > 0) {
                        if (query.length() > 1) {
                            query.append(" OR ");
                        }
                        query.append("@").append(fieldWithProtect).append(":").append(type.getFTSPreparedValue(val));
                        //}
                    }
                } else {
                    query.append("@").append(fieldWithProtect).append(":").append(type.getFTSPreparedValue(null));
                }
                return query.toString();
            }
        },
        NEQL_ASSOC () {
            @Override
            public String getFTSQuery(String field, String value, Types type) {
                StringBuilder query = new StringBuilder();
                String fieldWithProtect = doCharsProtection(field + "-ref", ":-");
                if (value != null) {
                    String[] values = value.split(",");
                    for (String val : values) {
                        //if (val.length() > 0) {
                        if (query.length() > 1) {
                            query.append(" AND ");
                        }
                        query.append("NOT @").append(fieldWithProtect).append(":").append(type.getFTSPreparedValue(val));
                        //}
                    }
                } else {
                    query.append("NOT @").append(fieldWithProtect).append(":").append(type.getFTSPreparedValue(null));
                }
                return query.toString();
            }
        },
        EQL () {
            @Override
            public String getFTSQuery(String field, String value, Types type) {
                StringBuilder query = new StringBuilder();
                String fieldWithProtect = doCharsProtection(field, ":-");
                if (value != null) {
                    String[] values = value.split(",");
                    for (String val : values) {
                        //if (val.length() > 0) {
                            if (query.length() > 1) {
                                query.append(" OR ");
                            }
                            query.append("=@").append(fieldWithProtect).append(":").append(type.getFTSPreparedValue(val));
                        //}
                    }
                } else {
                    query.append("=@").append(fieldWithProtect).append(":").append(type.getFTSPreparedValue(null));
                }
                return query.toString();
            }
        },
        NEQL () {
            @Override
            public String getFTSQuery(String field, String value, Types type) {
                StringBuilder query = new StringBuilder();
                String fieldWithProtect = doCharsProtection(field, ":-");
                if (value != null) {
                    String[] values = value.split(",");
                    for (String val : values) {
                        //if (val.length() > 0) {
                            if (query.length() > 1) {
                                query.append(" AND ");
                            }
                            query.append("NOT @").append(fieldWithProtect).append(":").append(type.getFTSPreparedValue(val));
                        //}
                    }
                } else {
                    query.append("NOT @").append(fieldWithProtect).append(":").append(type.getFTSPreparedValue(null));
                }
                return query.toString();
            }
        },
        BEF() {
            @Override
            public String getFTSQuery(String field, String value, Types type) {
                String preparedValue = "";
                if (type.equals(Types.DATE)) {
                    Date valueDate = Types.tryMakeDate(value);
                    Calendar calendar = Calendar.getInstance();
                    if (valueDate != null) {
                        calendar.setTime(valueDate);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                    }
                    preparedValue = "\'" + (valueDate != null ? DateFormat_YMD_HMS.format(calendar.getTime()) : "") + "\'";
                } else if (type.equals(Types.DATETIME)) {
                    preparedValue = type.getFTSPreparedValue(value);
                }
                return "@" + doCharsProtection(field, ":-")   + ":[MIN TO " +  preparedValue + "]";
            }
        },
        AFT () {
            @Override
            public String getFTSQuery(String field, String value, Types type) {
                String preparedValue = "";
                if (type.equals(Types.DATE)) {
                    Date valueDate = Types.tryMakeDate(value);
                    Calendar calendar = Calendar.getInstance();
                    if (valueDate != null) {
                        calendar.setTime(valueDate);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);

                    }
                    preparedValue = "\'" + (valueDate != null ? DateFormat_YMD_HMS.format(calendar.getTime()) : "") +  "\'";
                }
                if (type.equals(Types.DATETIME)) {
                    preparedValue = type.getFTSPreparedValue(value);
                }
                return "@" + doCharsProtection(field, ":-") + ":[" + preparedValue + " TO MAX]";
            }
        },
        BEQL () {
            @Override
            public String getFTSQuery(String field, String value, Types type) {
                return "@" + doCharsProtection(field, ":-") + ":[" + type.getFTSPreparedValue(value) + " TO MAX]";
            }
        },
        LEQL () {
            @Override
            public String getFTSQuery(String field, String value, Types type) {
                return "@" + doCharsProtection(field, ":-") + ":[MIN TO " + type.getFTSPreparedValue(value) + "]";
            }
        },
        BEGN () {
            @Override
            public String getFTSQuery(String field, String value, Types type) {
                return "@" + doCharsProtection(field, ":-") + ":\'" + value + "*\'";
            }
        },
        ENDS () {
            @Override
            public String getFTSQuery(String field, String value, Types type) {
                return "@" + doCharsProtection(field, ":-") + ":\'*" + value + "\'";
            }
        },
        CONT() {
            @Override
            public String getFTSQuery(String field, String value, Types type) {
                return "@" + doCharsProtection(field, ":-") + ":\'*" + value + "*\'";
            }
        };

        abstract public String getFTSQuery(String field, String value, Types type);

        public static Operations findOperation(String code) {
            if (code != null) {
                // поиск точного соот-вия
                for (Operations t : values()) {
                    if (code.toUpperCase().equals(t.name()))
                        return t;
                }
            }
            return null;
        }

        protected String doCharsProtection(String s, String chars) {
            if (s == null ||s.length() == 0 || chars == null ||chars.length() == 0) {
                return s;
            }
            final StringBuilder result = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                final char ch = s.charAt(i);
                if (CH_WALL == ch   /* сам символ "экрана" тоже надо экранировать */
                        || chars.indexOf(ch) >= 0
                        )// надо экранировку
                    result.append(CH_WALL);
                result.append(ch); // сам символ
            }
            return result.toString();
        }
    }

    public enum Operators {
        OR,
        AND;

        public static Operators findOperator(String code) {
            if (code != null) {
                // поиск точного соот-вия
                for (Operators t : values()) {
                    if (code.toUpperCase().equals(t.name()))
                        return t;
                }
            }
            return null;
        }
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return getFolder(LECM_SEARCH_QUERIES_ROOT_ID);
    }

    /**
    * @return Возвращает NodeRef где лежат поисковые запросы пользователя в родительской дериктории "Личные поисковые запросы"
    * Если дериктории нет, то создаем ее с именем идентификатора текущего пользователя.
    *
    * */
	// TODO: Может, стоит всё-таки избавиться от getOrCreate?
    public NodeRef getStoreFolder() {
        final NodeRef nodeRefParent = nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, "Личные поисковые запросы");
        NodeRef nodeRefChild = getFolder(nodeRefParent, orgstructureBean.getCurrentEmployee().getId());

        if (nodeRefChild != null) {
            return nodeRefChild;
        } else {
            return lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>(){
                @Override
                public NodeRef execute() throws Throwable {
                    return createFolder(nodeRefParent,  orgstructureBean.getCurrentEmployee().getId());
                }

            });
        }
    }

    public String buildQuery(JSONObject config) {
        StringBuilder queryBuilder = new StringBuilder();
        if (config.has(DOC_TYPE)) {
            try {
                String docType = config.getString(DOC_TYPE);
                if (!docType.isEmpty()) {
                    queryBuilder.append("TYPE:\'");
                    queryBuilder.append(docType);
                    queryBuilder.append("\'");
                }
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (config.has("attributes")) {
            try {
                JSONArray attributes = config.getJSONArray("attributes");
                StringBuilder orBuffer = new StringBuilder();
                StringBuilder attributesBuffer = new StringBuilder();
                for (int i = 0; i < attributes.length(); i++) {
                    JSONObject attibuteObj = attributes.getJSONObject(i);
                    if (!attibuteObj.getString("id").equals("OPERATOR")) {
                        String operationKey = attibuteObj.getString("case");
                        Operations operation = Operations.findOperation(operationKey);
                        if (operation != null) {
                            String field = attibuteObj.getString("id");
                            String type = attibuteObj.getString("type");
                            String value;
                            try {
                                value = java.net.URLDecoder.decode(attibuteObj.getString("value"), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                logger.error(e.getMessage(), e);
                                value = attibuteObj.getString("value");
                            }
                            String attrQuery = operation.getFTSQuery(field, value, Types.findType(type));
                            if (!attrQuery.isEmpty()) {
                                orBuffer.append(attrQuery);
                            }
                        }
                    } else {
                        String operatorValue = attibuteObj.getString("value").toUpperCase();
                        if (operatorValue.equals("OR")) {
                            if (orBuffer.length() > 0 &&
                                    !"OR ".equals(orBuffer.substring(orBuffer.length() - 4, orBuffer.length() - 1))) {
                                orBuffer.append(" OR ");
                            }
                        } else {
                            if (orBuffer.length() > 0) {
                                boolean useBrackets = true;
                                if (orBuffer.toString().startsWith("NOT")) {
                                    Matcher m = MULTIPLE_NOT_QUERY.matcher(orBuffer.toString().toUpperCase());
                                    if (!m.find()) { //
                                        useBrackets = false;
                                    }
                                }
                                attributesBuffer.append(useBrackets ? "(" : "");
                                attributesBuffer.append(orBuffer);
                                attributesBuffer.append(useBrackets ? ")" : "");

                                attributesBuffer.append(" AND ");

                                orBuffer.delete(0, orBuffer.length());
                            }
                        }
                    }

                }
                if (orBuffer.length() > 0) {
                    boolean useBrackets = true;
                    if (orBuffer.toString().startsWith("NOT")) {
                        Matcher m = MULTIPLE_NOT_QUERY.matcher(orBuffer.toString().toUpperCase());
                        if (!m.find()) { //
                            useBrackets = false;
                        }
                    }
                    attributesBuffer.append(useBrackets ? "(" : "");
                    attributesBuffer.append(orBuffer.toString());
                    attributesBuffer.append(useBrackets ? ")" : "");
                }

                if (attributesBuffer.length() > 0) {
                    boolean useBrackets = true;
                    if (attributesBuffer.toString().startsWith("NOT")) {
                        Matcher m = MULTIPLE_NOT_QUERY.matcher(attributesBuffer.toString().toUpperCase());
                        if (!m.find()) { //
                            useBrackets = false;
                        }
                    }
                    boolean add = false;
                    if (queryBuilder.length() > 0) {
                        add = true;
                        queryBuilder.append(" AND ");
                        queryBuilder.append(useBrackets ? "(" : "");
                    }

                    queryBuilder.append(attributesBuffer.toString());

                    if (add) {
                        queryBuilder.append(useBrackets ? ")" : "");
                    }
                }
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return queryBuilder.toString();
    }
	
}
