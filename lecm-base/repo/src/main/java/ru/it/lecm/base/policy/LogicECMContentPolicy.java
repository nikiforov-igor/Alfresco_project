package ru.it.lecm.base.policy;

import java.io.Serializable;
import java.util.Map;

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

/**
 * User: PMelnikov
 * Date: 14.01.13
 * Time: 11:23
 *
 * Политика объектов типа cm:object и его наследников для обеспечения работы Logic ECM Бизнес-платформа
 */
public class LogicECMContentPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy{

	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		LogicECMContentPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		LogicECMContentPolicy.policyComponent = policyComponent;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onUpdateProperties"));
	}

	/**
	 * Метод создает содержимое объекта по умолчанию для отображения его в стандартном проводнике
	 *
	 * @param nodeRef
	 * @param before
	 * @param after
	 */
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
