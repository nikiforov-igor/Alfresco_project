package ru.it.lecm.statemachine.expression;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * User: PMelnikov
 * Date: 24.01.13
 * Time: 15:47
 */
public class ExpressionDocument {

	private NodeRef nodeRef;

	public ExpressionDocument(NodeRef nodeRef) {
		this.nodeRef = nodeRef;
	}

	//Значение аттрибута
	public Object attribute(String attributeName) {
		return "Test";
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
