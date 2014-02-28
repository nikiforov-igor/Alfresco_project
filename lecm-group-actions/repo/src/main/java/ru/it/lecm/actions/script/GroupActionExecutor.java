package ru.it.lecm.actions.script;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.MD5;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.*;
import ru.it.lecm.actions.bean.GroupActionsService;
import ru.it.lecm.actions.bean.GroupActionsServiceImpl;
import ru.it.lecm.documents.beans.DocumentService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * User: pmelnikov
 * Date: 21.02.14
 * Time: 13:16
 */
public class GroupActionExecutor extends DeclarativeWebScript {

    private NodeService nodeService;
    private GroupActionsServiceImpl actionsService;
    private ServiceRegistry serviceRegistry;

    final private static Logger logger = LoggerFactory.getLogger(GroupActionExecutor.class);

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

        List<NodeRef> items = new ArrayList<NodeRef>();
        String actionId = null;

        HashMap<String, String> paramenters = new HashMap<String, String>();
        try {
            String content = req.getContent().getContent();
            JSONObject object = new JSONObject(content);
            actionId = object.getString("actionId");
            JSONArray iArray = null;
            try {
                iArray = object.getJSONArray("items");
            } catch (JSONException e) {
                iArray = new JSONArray(object.getString("items"));
            }
            for (int i = 0; i < iArray.length(); i++) {
                String item = iArray.getString(i);
                if (NodeRef.isNodeRef(item)) {
                    items.add(new NodeRef(item));
                }
            }
            Iterator it = object.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (!"actionId".equals(key) && !"items".equals(key)) {
                    paramenters.put(key, object.getString(key));
                }
            }
        } catch (Exception e) {
            logger.error("Cannot parse request", e);
        }

        NodeRef action = null;
        if (actionId != null) {
            action = nodeService.getChildByName(actionsService.getHomeRef(), ContentModel.ASSOC_CONTAINS, actionId);
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (action != null && items.size() > 0) {
            Map<String, Object> model = new HashMap<String, Object>(8, 1.0f);
            Map<String, Object> scriptModel = createScriptParameters(req, null, null, model);
            addParamenersToModel(paramenters, scriptModel);

            Map<String, Object> returnModel = new HashMap<String, Object>(8, 1.0f);
            scriptModel.put("model", returnModel);

            String script = nodeService.getProperty(action, GroupActionsService.PROP_SCRIPT).toString();
            ScriptProcessor scriptProcessor = getContainer().getScriptProcessorRegistry().getScriptProcessorByExtension("js");

            if (Boolean.TRUE.equals(nodeService.getProperty(action, GroupActionsService.PROP_FOR_COLLECTION))) {
                result.put("forCollection", true);
                result.put("withErrors", false);
                script = "var documents = [];" +
                         "for each (var doc in documentsArray) {" +
                         "   var node = search.findNode(doc.toString());" +
                         "   if (node != null) documents.push(node);" +
                         "}\r\n" + script;
                ScriptContent scriptContent = new StringScriptContent(script);
                scriptModel.put("documentsArray", items.toArray());
                try {
                    scriptProcessor.executeScript(scriptContent, scriptModel);
                } catch (Exception e) {
                    logger.error("Error while execute script: ", e);
                    result.put("withErrors", true);
                }
                result.put("redirect", returnModel.get("redirect"));
                result.put("openWindow", returnModel.get("openWindow"));
            } else {
                result.put("forCollection", false);
                result.put("withErrors", false);
                ScriptContent scriptContent = new StringScriptContent(script);
                ArrayList<HashMap<String, Object>> itemsResult = new ArrayList<HashMap<String, Object>>();
                for (NodeRef item : items) {
                    scriptModel.put("document", item);
                    HashMap<String, Object> itemResult = new HashMap<String, Object>();
                    itemResult.put("message", nodeService.getProperty(item, DocumentService.PROP_PRESENT_STRING).toString());
                    itemResult.put("withErrors", false);
                    try {
                        scriptProcessor.executeScript(scriptContent, scriptModel);
                    } catch (Exception e) {
                        logger.error("Error while execute script: ", e);
                        itemResult.put("withErrors", true);
                    }
                    itemResult.put("redirect", returnModel.get("redirect"));
                    itemResult.put("openWindow", returnModel.get("openWindow"));
                    itemsResult.add(itemResult);
                }
                result.put("items", itemsResult);
            }
        }

        return result;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setActionsService(GroupActionsServiceImpl actionsService) {
        this.actionsService = actionsService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    private void addParamenersToModel(HashMap<String, String> parameters, Map<String, Object> model) {
        for (String key : parameters.keySet()) {
            String normalizeKey = key.replace(":", "_");
            String stringValue = parameters.get(key);
            Object value;
            if (NodeRef.isNodeRef(stringValue)) {
                value = new NodeRef(stringValue);
            } else {
                value = stringValue;
            }
            model.put(normalizeKey, value);
        }
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
