package ru.it.lecm.base.scripts;

import java.io.IOException;
import java.util.*;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author dbashmakov
 *         Date: 23.10.12
 *         Time: 14:12
 */
public class GetNodePropertiesScript extends AbstractWebScript {

	public static final String DEFAULT_STORE_TYPE = "workspace";
	public static final String DEFAULT_STORE_ID = "SpacesStore";

	private static ServiceRegistry serviceRegistry;
	public static final String NODE_REF = "nodeRef";
	public static final String PROPS = "props";

	private static final Log log = LogFactory.getLog(GetNodePropertiesScript.class);

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		GetNodePropertiesScript.serviceRegistry = serviceRegistry;
	}


	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		Map<String, String> templateParams = req.getServiceMatch().getTemplateVars();
		String storeType = templateParams.get("store_type") != null ? templateParams.get("store_type") : DEFAULT_STORE_TYPE;
		String storeId = templateParams.get("store_id") != null ? templateParams.get("store_id") : DEFAULT_STORE_ID;
		String id = templateParams.get("id");
		String ref;
		if (id == null) {
			res.getWriter().write(new JSONObject().toString());
			return;
		} else {
			ref = storeType + "://" + storeId + "/" + id;
		}


		Map<String, String[]> requestParameters = new HashMap<String, String[]>();
		for (String paramName : req.getParameterNames()) {
			requestParameters.put(paramName, req.getParameterValues(paramName));
		}

		JSONObject result = new JSONObject();
		if (ref != null && !ref.isEmpty()) {
			NodeService nodeService = serviceRegistry.getNodeService();
			NodeRef nodeRef = new NodeRef(ref);

			String props = req.getContent().getContent();
			if (props != null && !props.isEmpty() && props.startsWith("{")) {
				try {
					JSONObject propsObj = new JSONObject(props);
					Iterator keys = propsObj.keys();
					while (keys.hasNext()) {
						String prop = (String) keys.next();
						String getFrom = (String) propsObj.get(prop);
						Object value = getValue(nodeRef, getFrom, nodeService);
						result.put(prop, value);
					}
				} catch (JSONException e) {
					log.error(e);
				}
			}
		}

		res.setContentEncoding("utf-8");
		res.getWriter().write(result.toString());
	}

	private Object getValue(NodeRef nodeRef, String getFrom, NodeService nodeService) {
		Object value = null;
		Set<NodeRef> checkedRefs = new HashSet<NodeRef>();
		checkedRefs.add(nodeRef);
		if (getFrom.indexOf("->") > 0) {// get from assocs
			int index = getFrom.indexOf("->");
			String assocName = getFrom.substring(0, index);

			checkedRefs.remove(nodeRef);// get value from assocs not from current Ref
			List<NodeRef> assocs = getAvaiableAssocs(nodeRef, assocName, nodeService);
			checkedRefs.addAll(assocs);

			String lastPart = getFrom.substring(index + 2);
			for (NodeRef checkedRef : checkedRefs) {
				value = getValue(checkedRef, lastPart, nodeService);
				if (value != null) {
					break;
				}
			}
		} else {
			value = nodeService.getProperty(nodeRef, QName.createQName(getFrom));
		}
		return value != null ? value : "";
	}

	private List<NodeRef> getAvaiableAssocs(NodeRef nodeRef, String assocName, NodeService nodeService) {
		List<NodeRef> result = new ArrayList<NodeRef>();
		QName assocProperty = QName.createQName(assocName);
		List<ChildAssociationRef> childs = nodeService.getChildAssocs(nodeRef, assocProperty, RegexQNamePattern.MATCH_ALL);
		if (childs != null && childs.size() > 0) {
			for (ChildAssociationRef child : childs) {
				result.add(child.getChildRef());
			}
		} else { // find target assocs
			List<AssociationRef> assocs = nodeService.getTargetAssocs(nodeRef, assocProperty);
			if (assocs != null && assocs.size() > 0) {
				for (AssociationRef assoc : assocs) {
					result.add(assoc.getTargetRef());
				}
			}
		}
		return result;
	}
}
