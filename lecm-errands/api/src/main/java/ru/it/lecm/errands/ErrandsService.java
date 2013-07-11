package ru.it.lecm.errands;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * User: AIvkin
 * Date: 09.07.13
 * Time: 12:09
 */
public interface ErrandsService {
	public static final String ERRANDS_ROOT_NAME = "Сервис Поручения";
	public static final String ERRANDS_ROOT_ID = "ERRANDS_ROOT_ID";

	public static final String ERRANDS_SETTINGS_NODE_NAME = "Settings";

	public static final String ERRANDS_NAMESPACE_URI = "http://www.it.ru/logicECM/errands/1.0";

	public static final QName TYPE_ERRANDS = QName.createQName(ERRANDS_NAMESPACE_URI, "document");
	public static final QName TYPE_ERRANDS_SETTINGS = QName.createQName(ERRANDS_NAMESPACE_URI, "settings");
	public static final QName TYPE_ERRANDS_USER_SETTINGS = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings");

	public static final QName SETTINGS_PROP_MODE_CHOOSING_EXECUTORS = QName.createQName(ERRANDS_NAMESPACE_URI, "settings-mode-choosing-executors");
	public static final String SETTINGS_PROP_MODE_CHOOSING_EXECUTORS_ORGANIZATION = "ORGANIZATION";
	public static final String SETTINGS_PROP_MODE_CHOOSING_EXECUTORS_UNIT = "UNIT";

	public static final QName USER_SETTINGS_PROP_WITHOUT_INITIATOR_APPROVAL = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings-without-initiator-approval");
	public static final QName USER_SETTINGS_ASSOC_DEFAULT_INITIATOR = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings-default-initiator-assoc");
	public static final QName USER_SETTINGS_ASSOC_DEFAULT_SUBJECT = QName.createQName(ERRANDS_NAMESPACE_URI, "user-settings-default-subject-assoc");

	/**
	 * Получение папки для черновиков
	 * @return ссылку на папку с черновиками
	 */
	public NodeRef getDraftRoot();

	/**
	 * Получение объекта глобальных настроек для поручений
	 * @return ссылка на объект глобальных настроек для поручений
	 */
	public NodeRef getSettingsNode();

	/**
	 * Получение объекта настроек поручений текущего пользователя
	 * @return ссылка на объект пользовательских настроек поручений
	 */
	public NodeRef getCurrentUserSettingsNode();
}
