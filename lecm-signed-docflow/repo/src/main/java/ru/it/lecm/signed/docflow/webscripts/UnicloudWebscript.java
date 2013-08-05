package ru.it.lecm.signed.docflow.webscripts;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.util.ReflectionUtils;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.signed.docflow.UnicloudService;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

/**
 *
 * @author VLadimir Malygin
 * @since 31.07.2013 17:52:49
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class UnicloudWebscript extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger(UnicloudWebscript.class);
	private final static String ACTION_DEF = "action";

	private UnicloudService unicloudService;
	private OrgstructureBean orgstructureService;
	private NodeService nodeService;
	private TransactionService transactionService;

	public void setUnicloudService(UnicloudService unicloudService) {
		this.unicloudService = unicloudService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	private JSONObject getJsonContent(final Content content) {
		JSONObject json;
		try {
			json = new JSONObject(content.getContent());
		} catch(IOException ex) {
			String msg = "Can't read request content as json string";
			logger.error("{}. Caused by: {}", msg, ex.getMessage());
			throw new WebScriptException(msg, ex);
		} catch(JSONException ex) {
			String msg = "Can't marshall request content as json object";
			logger.error("{}. Caused by: {}", msg, ex.getMessage());
			throw new WebScriptException(msg, ex);
		}
		return json;
	}

	private void addAttriburesToPersonalData() {
		AuthenticationUtil.runAsSystem(new RunAsWork<Void>() {

			@Override
			public Void doWork() throws Exception {
				RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
				return transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>() {

					@Override
					public Void execute() throws Throwable {
						NodeRef currentEmployeeRef = orgstructureService.getCurrentEmployee();
						NodeRef personalDataRef = orgstructureService.getEmployeePersonalData(currentEmployeeRef);
						Set<QName> aspects = nodeService.getAspects(personalDataRef);
						if(!aspects.contains(SignedDocflow.ASPECT_PERSONAL_DATA_ATTRS)) {
							Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
//							properties.put(SignedDocflow.PROP_AUTH_TOKEN, "");
//							properties.put(SignedDocflow.PROP_CERT_THUMBPRINT, "");
//							properties.put(SignedDocflow.PROP_AUTH_TYPE, "");
							nodeService.addAspect(personalDataRef, SignedDocflow.ASPECT_PERSONAL_DATA_ATTRS, properties);
						}
						return null;
					}
				});
			}
		});
	}

	private JSONObject authenticateByCertificate(final JSONObject json) {
		String guidSignBase64;
		String timestamp;
		String timestampSignBase64;

		try {
			guidSignBase64 = json.getString("guidSign");
			timestamp = json.getString("timestamp");
			timestampSignBase64 = json.getString("timestampSign");
		} catch (JSONException ex) {
			String msg = "Can't parse incoming json";
			logger.error("{}. Caused by: {}", msg, ex.getMessage());
			throw new IllegalArgumentException(msg, ex);
		}
		Map<String, Object> properties = unicloudService.authenticateByCertificate(guidSignBase64, timestamp, timestampSignBase64);
		return new JSONObject(properties);
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

		final Map<String, String> templateArgs = req.getServiceMatch().getTemplateVars();
		final String action = templateArgs.get(ACTION_DEF);

		final Content content = req.getContent();
		if (content == null) {
			String msg = "UnicloudWebscript was called with empty json content";
			logger.error("{}. Executed action: {}", msg, action);
			throw new WebScriptException(String.format("%s. Executed action: %s", msg, action));
		}

		JSONObject requestJSON = getJsonContent(content);
		JSONObject responseJSON;

		try {
			Method actionMethod = ReflectionUtils.findMethod(getClass(), action, JSONObject.class);
			if (actionMethod != null) {
				addAttriburesToPersonalData();
				responseJSON = (JSONObject) ReflectionUtils.invokeMethod(actionMethod, this, requestJSON);
			} else {
				throw new WebScriptException(String.format("There is no method %s(JSONObject json) in UnicloudService class", action));
			}
		} catch(Exception ex) {
			String msg = "Can't execute action";
			logger.error("{} {}. Caused by: {}", new Object[] {msg, action, ex.getMessage()});
			throw new WebScriptException(String.format("%s %s", msg, action), ex);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", responseJSON);
		return result;
	}
}
