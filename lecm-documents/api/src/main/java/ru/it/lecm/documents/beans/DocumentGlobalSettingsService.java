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
	QName PROP_ENABLE_PASSIVE_NOTIFICATIONS = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "enable-passive");
	QName PROP_N_DAYS = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "n-days");

	int DEFAULT_N_DAYS = 5;
	String DEFAULT_VIEW_MODE = "VIEW_ALL";

	NodeRef getSettingsNode();
	
	Boolean isHideProperties();
	
	String getLinksViewMode();

	/**
	 * @return Включены или выключены пассивные уведомления
	 */
	boolean isEnablePassiveNotifications();

	/**
	 * @return Количество рабочих дней за которое должно высылаться уведомление
	 */
	int getSettingsNDays();
}
