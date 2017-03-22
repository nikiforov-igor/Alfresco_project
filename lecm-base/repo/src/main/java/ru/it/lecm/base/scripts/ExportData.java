package ru.it.lecm.base.scripts;

import com.csvreader.CsvWriter;
import org.alfresco.repo.jscript.RhinoScriptProcessor;
import org.alfresco.scripts.ScriptResourceHelper;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.MD5;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.*;

import java.io.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: PMelnikov
 * Date: 11.06.14
 */

public class ExportData extends AbstractWebScript {

    private static final transient Logger log = LoggerFactory.getLogger(ExportData.class);

    protected NodeService nodeService;

    /**
     * Russian locale
     */
    private static final Locale LOCALE_RU = new Locale("RU");

    /**
     * Формат даты
     */
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String DATETIME_FORMAT = "dd.MM.yyyy HH:mm";
    private RhinoScriptProcessor rhinoScriptProcessor;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        OutputStream resOutputStream = null;
        try {
            InputStream scriptStream = this.getClass().getResourceAsStream("/alfresco/templates/webscripts/ru/it/lecm/search/search.post.json.js");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[8 * 1024];
            int size;
            while ((size = scriptStream.read(buf)) != -1) {
                baos.write(buf, 0, size);
            }

            // Подготавливаем скрипт
            Log logger = LogFactory.getLog(ExportData.class);
            String script = ScriptResourceHelper.resolveScriptImports(baos.toString(), rhinoScriptProcessor, logger);

            int timeZoneOffset = TimeZone.getDefault().getRawOffset();
            try {
                timeZoneOffset = - Integer.parseInt(req.getParameter("timeZoneOffset")) * 1000 * 60;
            } catch (Exception ignored) {}


            //Подготавливаем модель данных для скрипта
            Map<String, Object> model = new HashMap<String, Object>(8, 1.0f);
            Map<String, Object> scriptModel = createScriptParameters(req, res, null, model);

            JSONObject parameters = new JSONObject(req.getParameter("parameters"));
            //сбрасываем настройки пагинации
            parameters.getJSONObject("params").put("maxResults", "" + Integer.MAX_VALUE);
            parameters.getJSONObject("params").put("startIndex", "0");

            scriptModel.put("json", parameters);
            Map<String, Object> returnModel = new HashMap<String, Object>(8, 1.0f);
            scriptModel.put("model", returnModel);


            final ScriptContent scriptContent = new StringScriptContent(script);
            executeScript(scriptContent, scriptModel);
            HashMap<String, Object> dataModel = new HashMap<String, Object>();
            mergeScriptModelIntoTemplateModel(returnModel, dataModel);

            res.setContentEncoding("UTF-8");
            res.setContentType("text/csv");
            res.addHeader("Content-Disposition", "attachment; filename=datagrid.csv");
            // Create an XML stream writer
            resOutputStream = res.getOutputStream();

            // По умолчанию charset в UTF-8
            Charset charset = Charset.defaultCharset();
            CsvWriter wr = new CsvWriter(resOutputStream, ';', charset);
            JSONArray fields = null;
            try {
                fields = parameters.getJSONArray("columns");
            } catch (JSONException e) {
                log.error("Wrong columns config");
            }

            ArrayList<String> fieldKeys = new ArrayList<String>();
            if (fields != null) {
                try {
                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject field = fields.getJSONObject(i);
                        fieldKeys.add(field.getString("formsName"));
                        if (i == 0) {
                            wr.write("\ufeff" + field.getString("label")); //UTF c BOM идентификатором
                        } else {
                            wr.write(field.getString("label"));
                        }
                    }
                } catch (JSONException e) {
                    log.error("Error while parsing fields config");
                }
            }

            wr.endRecord();

            ArrayList<HashMap<String, Object>> items = (ArrayList<HashMap<String, Object>>) ((HashMap<String, Object>) dataModel.get("data")).get("items");
            if (items != null && !items.isEmpty()) {
                int timeZoneDiff = timeZoneOffset - TimeZone.getDefault().getRawOffset();
                for (HashMap<String, Object> item : items) {
                    HashMap<String, Object> data = (HashMap<String, Object>) item.get("nodeData");
                    for (String key : fieldKeys) {
                        Object valueObject = data.get(key);
                        String result = "";
                        if (valueObject instanceof HashMap) {
                            HashMap<String, Object> value = (HashMap<String, Object>) valueObject;
                            result = getValue(timeZoneDiff, value);
                        } else if (valueObject instanceof List) {
                            List<HashMap<String, Object>> value = (List<HashMap<String, Object>>) valueObject;
                            StringBuilder stringBuilder = new StringBuilder(result);
                            for (HashMap<String, Object> dataItem : value) {
                                stringBuilder.append(getValue(timeZoneDiff, dataItem)).append("; ");
                            }
                            result = stringBuilder.substring(0, stringBuilder.length() - 2);
                        }
                        wr.write(result);
                    }
                    wr.endRecord();
                }
            }
            wr.close();
            resOutputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (JSONException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (resOutputStream != null) {
                resOutputStream.close();
            }
        }
        log.info("Export CSV complete");
    }
    /**
     * extract string value
     */
    private String getValue(int timeZoneDiff, HashMap<String, Object> value) {
        String result = "";
        if (value != null) {
            String type = (String) value.get("type");
            if ("date".equals(type) || "datetime".equals(type)) {
                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(value.get("displayValue").toString());
                } catch (ParseException e) {
                    log.error("Error while parsing date format", e);
                }

                if (timeZoneDiff != 0) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);

                    cal.add(Calendar.MILLISECOND, timeZoneDiff);
                    date = cal.getTime();
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("date".equals(type) ? DATE_FORMAT : DATETIME_FORMAT, LOCALE_RU);
                result = dateFormat.format(date);
            } else if ("boolean".equals(type)){
                Boolean booleanValue = Boolean.parseBoolean(value.get("displayValue").toString());
                result = booleanValue ? "Да" :"Нет";
            } else {
                result = value.get("displayValue").toString();
            }
        }
        result = result.replaceAll("<\\/?[^>]+>", "");
        return result;
    }

    /**
     * Merge script generated model into template-ready model
     *
     * @param scriptModel   script model
     * @param templateModel template model
     */
    private void mergeScriptModelIntoTemplateModel(Map<String, Object> scriptModel, Map<String, Object> templateModel) {
        // determine script processor
        final ScriptProcessor scriptProcessor = getContainer().getScriptProcessorRegistry().getScriptProcessorByExtension("js");
        if (scriptProcessor != null) {
            for (Map.Entry<String, Object> entry : scriptModel.entrySet()) {
                // retrieve script model value
                Object value = entry.getValue();
                Object templateValue = scriptProcessor.unwrapValue(value);
                templateModel.put(entry.getKey(), templateValue);
            }
        }
    }

    public void setRhinoScriptProcessor(RhinoScriptProcessor rhinoScriptProcessor) {
        this.rhinoScriptProcessor = rhinoScriptProcessor;
    }

    private static class StringScriptContent implements ScriptContent {
        private final String content;

        public StringScriptContent(String content) {
            this.content = content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content.getBytes(Charset.forName("UTF-8")));
        }

        @Override
        public String getPath() {
            return MD5.Digest(content.getBytes()) + ".js";
        }

        @Override
        public String getPathDescription() {
            return "Javascript Console Script";
        }

        @Override
        public Reader getReader() {
            return new StringReader(content);
        }

        @Override
        public boolean isCachable() {
            return false;
        }

        @Override
        public boolean isSecure() {
            return true;
        }
    }
}
