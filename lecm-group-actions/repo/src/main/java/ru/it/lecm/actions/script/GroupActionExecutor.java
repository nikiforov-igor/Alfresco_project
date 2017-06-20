package ru.it.lecm.actions.script;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.MD5;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.*;
import ru.it.lecm.actions.bean.GroupActionsService;
import ru.it.lecm.actions.bean.GroupActionsServiceImpl;
import ru.it.lecm.base.beans.LecmTransactionHelper;
import ru.it.lecm.base.beans.SubstitudeBean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: pmelnikov
 * Date: 21.02.14
 * Time: 13:16
 */
public class GroupActionExecutor extends DeclarativeWebScript {

	private static final Pattern nodeRefPattern = Pattern.compile("^[a-zA-Z]+://[a-zA-Z]+/[a-zA-Z0-9/-]+$");

    private NodeService nodeService;
    private GroupActionsServiceImpl actionsService;
    private ServiceRegistry serviceRegistry;
    private TransactionService transactionService;
	private LecmTransactionHelper lecmTransactionHelper;
    private SubstitudeBean substitudeBean;

    final private static Logger logger = LoggerFactory.getLogger(GroupActionExecutor.class);

	private static boolean isNodeRef(String nodeRef) {
		Matcher matcher = nodeRefPattern.matcher(nodeRef);
		return matcher.matches();
	}

	public LecmTransactionHelper getLecmTransactionHelper() {
		return lecmTransactionHelper;
	}

	public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
		this.lecmTransactionHelper = lecmTransactionHelper;
	}

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

        final List<NodeRef> items = new ArrayList<NodeRef>();
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

        List<NodeRef> actions = actionsService.getActiveGroupActions(items, true);
        actions.addAll(actionsService.getActiveGroupActions(items, false));
        if (!actions.contains(action)) {
            throw new IllegalStateException("Action \"" + actionId + "\" not found for this items and user");
        }

        final HashMap<String, Object> result = new HashMap<String, Object>();
        if (action != null && items.size() > 0) {
            Map<String, Object> model = new HashMap<String, Object>(8, 1.0f);
            final Map<String, Object> scriptModel = createScriptParameters(req, null, null, model);
            addParamenersToModel(paramenters, scriptModel);

            final Map<String, Object> returnModel = new HashMap<String, Object>(8, 1.0f);
            scriptModel.put("model", returnModel);

            String script = nodeService.getProperty(action, GroupActionsService.PROP_SCRIPT).toString();
            final ScriptProcessor scriptProcessor = getContainer().getScriptProcessorRegistry().getScriptProcessorByExtension("js");

            if (Boolean.TRUE.equals(nodeService.getProperty(action, GroupActionsService.PROP_FOR_COLLECTION))) {
                result.put("forCollection", true);
                result.put("withErrors", false);
                script = "var documents = [];" +
                         "for each (var doc in documentsArray) {" +
                         "   var node = search.findNode(doc.toString());" +
                         "   if (node != null) documents.push(node);" +
                         "}\r\n" + script;
                final ScriptContent scriptContent = new StringScriptContent(script);
                scriptModel.put("documentsArray", items.toArray());
                //TODO В транзакцию завёрнуто исполнение скрипта.
                try {
                    transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
                        @Override
                        public Object execute() throws Throwable {
                            scriptProcessor.executeScript(scriptContent, scriptModel);
                            return null;
                        }
                    }, false, true);
                } catch (Exception e) {
                    logger.error("Error while execute script: ", e);
                    result.put("withErrors", true);
                }
                result.put("redirect", returnModel.get("redirect"));
                result.put("postRedirect", returnModel.get("postRedirect"));
                result.put("openWindow", returnModel.get("openWindow"));
                result.put("showModalWindow", returnModel.get("showModalWindow"));
                result.put("messageVar", returnModel.get("message"));
            } else {
                result.put("forCollection", false);
                result.put("withErrors", false);
                result.put("messageVar", "");
                final ScriptContent scriptContent = new StringScriptContent(script);
                final ArrayList<HashMap<String, Object>> itemsResult = new ArrayList<HashMap<String, Object>>();

				lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
					@Override
					public Object execute() throws Throwable {
						for (NodeRef item : items) {
							scriptModel.put("document", item);
							HashMap<String, Object> itemResult = new HashMap<String, Object>();
                            String message;
							itemResult.put("withErrors", false);
                            try {
                                message = substitudeBean.getObjectDescription(item);
                                scriptProcessor.executeScript(scriptContent, scriptModel);
                                if (returnModel.get("message") != null) {
                                    message = returnModel.get("message").toString();
                                }
                            } catch (Exception e) {
                                logger.error("Error while execute script: ", e);
                                result.put("withErrors", true);
                                itemResult.put("withErrors", true);
                                message = e.getMessage();
                            }

							itemResult.put("redirect", returnModel.get("redirect"));
							itemResult.put("postRedirect", returnModel.get("postRedirect"));
							itemResult.put("openWindow", returnModel.get("openWindow"));
							itemResult.put("showModalWindow", returnModel.get("showModalWindow"));
							itemResult.put("message", message);
							itemsResult.add(itemResult);
						}
						return null;
					}
				});
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

    public void setSubstitudeBean(SubstitudeBean substitudeBean) {
        this.substitudeBean = substitudeBean;
    }

    private void addParamenersToModel(HashMap<String, String> parameters, Map<String, Object> model) {
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            String normalizeKey = param.getKey().replace(":", "_");
            String stringValue = param.getValue();
            Object value;
            if (isNodeRef(stringValue)) {
                value = new NodeRef(stringValue);
            } else {
                value = stringValue;
            }
            model.put(normalizeKey, value);
        }
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
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
