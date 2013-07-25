package ru.it.lecm.br5.semantic.api;

import org.alfresco.service.namespace.QName;
import org.alfresco.model.ContentModel;

/**
 *
 * @author snovikov
 */
public interface ConstantsBean {
	public static final String MODEL_SEMANTIC_ASPECTS_NAMESPACE_URI = "http://www.it.ru/lecm/br5/semantic/aspects/1.0";
	public static final String MODEL_DOCUMENTS_NAMESPACE_URI = "http://www.it.ru/logicECM/document/1.0";

	public static final QName ASPECT_BR5_INTEGRATION = QName.createQName(MODEL_SEMANTIC_ASPECTS_NAMESPACE_URI,"br5");
	public static final QName PROP_BR5_INTEGRATION_LOADED = QName.createQName(MODEL_SEMANTIC_ASPECTS_NAMESPACE_URI,"loaded");
	public static final QName PROP_BR5_INTEGRATION_VERSION = QName.createQName(MODEL_SEMANTIC_ASPECTS_NAMESPACE_URI,"version");
	public static final QName PROP_BR5_INTEGRATION_TAGS = QName.createQName(MODEL_SEMANTIC_ASPECTS_NAMESPACE_URI,"tags");
	public static final QName PROP_DOCUMENT_EMP_AUTHOR_REF = QName.createQName(MODEL_DOCUMENTS_NAMESPACE_URI,"employee-ref");

}
