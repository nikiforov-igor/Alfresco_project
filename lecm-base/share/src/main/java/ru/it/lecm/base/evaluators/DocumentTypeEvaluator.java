package ru.it.lecm.base.evaluators;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;

/**
 *
 * @author vmalygin
 */
public class DocumentTypeEvaluator extends DefaultSubComponentEvaluator {

	private final static Log logger = LogFactory.getLog(DocumentTypeEvaluator.class);

	private ConnectorService connectorService;

	public void setConnectorService(ConnectorService connectorService) {
		this.connectorService = connectorService;
	}

	@Override
	public boolean evaluate(RequestContext context, Map<String, String> params) {
		String documentRef = params.get("nodeRef");
		String documentType = params.get("type");
		String url = "/api/metadata?nodeRef=" + documentRef;
		try {
			Connector connector = connectorService.getConnector("alfresco", context.getUserId(), ServletUtil.getSession());
			Response response = connector.call(url);
			if (ResponseStatus.STATUS_OK == response.getStatus().getCode()) {
				JSONObject json = new JSONObject(response.getResponse());
				return documentType.equals(json.getString("type"));
			} else {
				logger.error("Cannot get response for " + url);
			}
		} catch (ConnectorServiceException ex) {
			logger.error("Cannot get connector for " + url, ex);
		} catch (JSONException ex) {
			logger.error("Cannot parse json response for " + url, ex);
		}
		return false;
	}
}
