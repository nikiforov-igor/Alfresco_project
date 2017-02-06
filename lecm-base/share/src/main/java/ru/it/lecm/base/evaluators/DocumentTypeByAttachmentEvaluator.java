package ru.it.lecm.base.evaluators;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.extensibility.SubComponentEvaluator;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;

/**
 *
 * @author vmalygin
 */
public class DocumentTypeByAttachmentEvaluator extends DefaultSubComponentEvaluator {

	private final static Log logger = LogFactory.getLog(DocumentTypeEvaluator.class);

	private ConnectorService connectorService;
	private SubComponentEvaluator evaluator;

	public void setConnectorService(ConnectorService connectorService) {
		this.connectorService = connectorService;
	}

	public void setEvaluator(SubComponentEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	@Override
	public boolean evaluate(RequestContext context, Map<String, String> params) {
		String attachmentRef = params.get("nodeRef");
		String url = "/lecm/document/attachments/api/getDocumentByAttachment?nodeRef=" + attachmentRef;
		try {
			Connector connector = connectorService.getConnector("alfresco", context.getUserId(), ServletUtil.getSession());
			Response response = connector.call(url);
			if (ResponseStatus.STATUS_OK == response.getStatus().getCode()) {
				JSONObject json = new JSONObject(response.getResponse());
				Map<String, String> newParams = new HashMap<>(params);
				newParams.put("nodeRef", json.getString("nodeRef"));
				return evaluator.evaluate(context, newParams);
			}
		} catch (ConnectorServiceException ex) {
			logger.error("Cannot get connector for " + url, ex);
		} catch (JSONException ex) {
			logger.error("Cannot parse json response for " + url, ex);
		}
		return false;
	}
}
