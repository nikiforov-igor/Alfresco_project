package ru.it.lecm.statemachine.expression;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * User: PMelnikov
 * Date: 24.01.13
 * Time: 15:47
 */
public class ExpressionDocument {

	private NodeRef nodeRef;
	private ServiceRegistry serviceRegistry;

	public ExpressionDocument(NodeRef nodeRef, ServiceRegistry serviceRegistry) {
		this.nodeRef = nodeRef;
		this.serviceRegistry = serviceRegistry;
	}

	//Значение аттрибута
	public Object attribute(String attributeName) {
		QName attribute = QName.createQName(attributeName,serviceRegistry.getNamespaceService());
		return serviceRegistry.getNodeService().getProperty(nodeRef, attribute);
	}

	//Наличие вложения с определенным типом
	public boolean hasAttachmentType(String attachmentType) {
		return true;
	}

	//Проверка условий на корректность хотя бы у одного из вложений
	public boolean anyAttachmentAttribute(String attributeName, String condition, String value) {
		return true;
	}

	//Проверка условий на корректность у всех вложений
	public boolean allAttachmentAttribute(String attributeName, String condition, String value) {
		return true;
	}

}
