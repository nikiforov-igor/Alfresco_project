package ru.it.lecm.signed.docflow.webscripts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.base.DeclarativeWebScriptHelper;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.signed.docflow.api.SignedDocflowModel;

/**
 * Конфигурационный вебскрипт
 * устанавливает/сбрасывает аспекты signable/docfloable
 * получает конфиг для криптоаплета
 * @author VLadimir Malygin
 * @since 08.08.2013 17:27:18
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class SignConfigWebscript extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger(SignConfigWebscript.class);

	private final static String ACTOR_DEF = "actor";

	private NodeService nodeService;
	private OrgstructureBean orgstructureService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	JSONObject executeAspectAction(final JSONObject json) throws Exception {
		JSONObject result = new JSONObject();
		String action = json.getString("action");
		NodeRef node = new NodeRef(json.getString("node"));
		QName aspect = QName.createQName(json.getString("aspect"));
		result.put("aspect", aspect.toString());
		if("get".equals(action)) {
			result.put("enabled", nodeService.getAspects(node).contains(aspect));
			Map<String, Object> properties = new HashMap<>();
			Serializable lockOwner = nodeService.getProperty(node, ContentModel.PROP_LOCK_OWNER);
			if (lockOwner != null) {
				properties.put(ContentModel.PROP_LOCK_OWNER.toString(), lockOwner);
			}
			Serializable lockType = nodeService.getProperty(node, ContentModel.PROP_LOCK_TYPE);
			if (lockType != null) {
				properties.put(ContentModel.PROP_LOCK_TYPE.toString(), lockType);
			}
			result.put("properties", properties);
		} else if ("set".equals(action)) {
			boolean enabled = json.getBoolean("enabled");
			result.put("enabled", enabled);
			if (enabled) {
				nodeService.addAspect(node, aspect, null);
			} else {
				nodeService.removeAspect(node, aspect);
			}
		} else {
			throw new IllegalArgumentException(String.format("Can't perform action %s using aspect %s", action, aspect));
		}
		return result;
	}

	JSONObject executeAppletAction(final JSONObject json) throws Exception  {
		JSONObject result = new JSONObject();
		String action = json.getString("action");
		if("get".equals(action)) {
			NodeRef employeeRef = orgstructureService.getCurrentEmployee();
			String licKey = (String) nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_APPLET_LIC_KEY);
			String licCert = (String) nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_APPLET_CERT);
			String licContainer = (String) nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_APPLET_CONTAINER);
			result.put("licKey", licKey);
			result.put("licCert", licCert);
			result.put("storeName", licContainer);
		} else {
			throw new IllegalArgumentException(String.format("Can't perform action %s using applet", action));
		}
		return result;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

		final Map<String, String> templateArgs = req.getServiceMatch().getTemplateVars();
		final String actor = templateArgs.get(ACTOR_DEF);

		final Content content = req.getContent();
		if (content == null) {
			String msg = "SignConfigWebScript was called with empty json content";
			logger.error("{}. Executed actor: {}", msg, actor);
			throw new WebScriptException(String.format("%s. Executed actor: %s", msg, actor));
		}

		JSONObject requestJSON = DeclarativeWebScriptHelper.getJsonContent(content);
		JSONObject responseJSON;

		try {
			if ("aspect".equals(actor)) {
				responseJSON = executeAspectAction(requestJSON);
			} else if ("applet".equals(actor)) {
				responseJSON = executeAppletAction(requestJSON);
			} else {
				throw new IllegalArgumentException(String.format("Actor %s is unknown and unsupported!", actor));
			}
		} catch(Exception ex) {
			throw new WebScriptException(null, ex);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("result", responseJSON);
		return result;
	}
}
