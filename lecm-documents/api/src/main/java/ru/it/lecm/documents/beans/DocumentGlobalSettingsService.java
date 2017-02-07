package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author apalm
 */
public interface DocumentGlobalSettingsService {

	String DOCUMENT_GLOBAL_SETTINGS_FOLDER_NAME = "Глобальные настройки документов";
	String DOCUMENT_GLOBAL_SETTINGS_FOLDER_ID = "GLOBAL_DOCUMENT_SETTINGS_FOLDER_ID";

	String DOCUMENT_GLOBAL_SETTINGS_NODE_NAME = "Settings-node";
	
	String GLOBAL_SETTINGS_PREFIX = "lecm-document-global-settings";
	String GLOBAL_SETTINGS_NAMESPACE = "http://www.it.ru/logicECM/document/global-settings/1.0";

	QName TYPE_SETTINGS = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "settings");
	QName PROP_SETTINGS_LINKS_VIEW_MODE = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "links-view-mode");
	QName PROP_SETTINGS_HIDE_PROPS = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "hide-properties-for-recipients");
	
	NodeRef getSettingsNode();
	
	Boolean isHideProperties();
	
	String getLinksViewMode();
}
