package ru.it.lecm.documents.templates.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vmalygin
 */
public final class DocumentTemplateModel {

	public final static String DOCUMENT_TEMPLATE_MODEL_URL = "http://www.it.ru/lecm/document/template/1.0";
	public final static String DOCUMENT_TEMPLATE_MODEL_PREFIX = "lecm-template";

	public final static QName MODEL_DOCUMENT_TEMPLATE = QName.createQName(DOCUMENT_TEMPLATE_MODEL_URL, "document-template-model");
	public final static QName TYPE_DOCUMENT_TEMPLATE = QName.createQName(DOCUMENT_TEMPLATE_MODEL_URL, "document-template");
	public final static QName PROP_DOCUMENT_TEMPLATE_DOC_TYPE = QName.createQName(DOCUMENT_TEMPLATE_MODEL_URL, "doc-type");
	public final static QName PROP_DOCUMENT_TEMPLATE_ATTRIBUTES = QName.createQName(DOCUMENT_TEMPLATE_MODEL_URL, "attributes");

	private DocumentTemplateModel() {
		throw new IllegalStateException("Class DocumentTemplateModel can not be instantiated");
	}
}
