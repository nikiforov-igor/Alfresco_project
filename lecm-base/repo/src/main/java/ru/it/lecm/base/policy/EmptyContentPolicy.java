package ru.it.lecm.base.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;

import java.io.Serializable;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 14.01.13
 * Time: 11:23
 */
public class EmptyContentPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		EmptyContentPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		EmptyContentPolicy.policyComponent = policyComponent;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onUpdateProperties"));

	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		ContentService contentService = serviceRegistry.getContentService();
		ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
		if (reader == null) {
			ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
			writer.setMimetype("text/plain");
			writer.putContent("");
		}
	}

}
