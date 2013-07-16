package ru.it.lecm.br5.semantic.webscripts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.br5.semantic.policies.DocumentBr5AspectPolicy;

/**
 *
 * @author snovikov
 */
public class Br5IntegrationWebScriptBean extends BaseWebScript {
	private final static Logger logger = LoggerFactory.getLogger(DocumentBr5AspectPolicy.class);
	public static final QName ASPECT_BR5_INTEGRATION = QName.createQName("http://www.it.ru/lecm/document/aspects/1.0","br5");
	public static final QName PROP_BR5_INTEGRATION_LOADED = QName.createQName("http://www.it.ru/lecm/document/aspects/1.0","loaded");

	private NodeService nodeService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void addBr5AspectToDocument(String sDocument){
		if (sDocument!=null && !sDocument.isEmpty()){
			NodeRef documentRef = new NodeRef(sDocument);
			Map<QName,Serializable> props = new HashMap<QName,Serializable>();
			props.put(PROP_BR5_INTEGRATION_LOADED,0);
			if (!nodeService.hasAspect(documentRef, ASPECT_BR5_INTEGRATION)){
				nodeService.addAspect(documentRef, ASPECT_BR5_INTEGRATION, props);
			}
		}
	}
}
