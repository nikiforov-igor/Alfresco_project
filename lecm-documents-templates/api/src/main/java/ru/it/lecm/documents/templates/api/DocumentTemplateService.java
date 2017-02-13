package ru.it.lecm.documents.templates.api;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author vmalygin
 */
public interface DocumentTemplateService {
	String TEMPLATES_URL = "http://www.it.ru/lecm/document/template/1.0";
	QName ASSOC_ORGANIZATION = QName.createQName(TEMPLATES_URL, "organizationAssoc");
	QName ASSOC_ORG_UNIT = QName.createQName(TEMPLATES_URL, "unitAssoc");

	NodeRef getDocumentTemplateFolder();

	/**
	 * Получить список шаблонов документов по типу документа
	 * Тип указывается в префиксной форме
	 * @param type тип документа в префиксной форме
	 * @return список шаблонов или пустой список
	 */
	List<NodeRef> getDocumentTemplatesForType(String type);

	/**
	 * Получить список шаблонов документов по типу документа
	 * Тип указывается как QName
	 * @param type тип документа
	 * @return список шаблонов или пустой список
	 */
	List<NodeRef> getDocumentTemplatesForType(QName type);
}
