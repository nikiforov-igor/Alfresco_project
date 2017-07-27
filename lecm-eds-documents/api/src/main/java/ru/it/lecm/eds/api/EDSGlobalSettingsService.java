package ru.it.lecm.eds.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author dbayandin
 */
public interface EDSGlobalSettingsService {

	String EDS_GLOBAL_SETTINGS_FOLDER_NAME = "Глобальные настройки СЭД";
	String EDS_GLOBAL_SETTINGS_FOLDER_ID = "GLOBAL_EDS_SETTINGS_FOLDER_ID";

	String EDS_GLOBAL_SETTINGS_NODE_NAME = "Settings-node";
	String TERMS_OF_NOTIFICATION_SETTINGS_NODE_NAME = "Terms-of-notification-settings-node";

	String POTENTIAL_ROLES_DICTIONARY_NAME = "Потенциальные роли";

	String GLOBAL_SETTINGS_PREFIX = "lecm-eds-globset";
	String GLOBAL_SETTINGS_NAMESPACE = "http://www.it.ru/logicECM/eds-global-settings/1.0";

	QName TYPE_SETTINGS = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "settings");
	QName PROP_SETTINGS_CENTRALIZED_REGISTRATION = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "centralized-registration");
	QName PROP_SETTINGS_LINKS_VIEW_MODE = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "links-view-mode");
	QName PROP_SETTINGS_HIDE_PROPS = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "hide-properties-for-recipients");
	QName ASSOC_SETTINGS_ARM_DASHLET_NODE = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "arm-node-for-dashlet-assoc");
	QName ASSOC_SETTINGS_ARM_DASHLET = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "arm-for-dashlet-assoc");

	QName TYPE_POTENTIAL_ROLE = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "potential-role");
	QName PROP_POTENTIAL_ROLE_BUSINESS_ROLE_REF = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "potential-role-business-role-assoc-ref");
	QName PROP_POTENTIAL_ROLE_ORG_ELEMENT_REF = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "potential-role-organization-element-assoc-ref");
	QName ASSOC_POTENTIAL_ROLE_BUSINESS_ROLE = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "potential-role-business-role-assoc");
	QName ASSOC_POTENTIAL_ROLE_EMPLOYEE = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "potential-role-employee-assoc");
	QName ASSOC_POTENTIAL_ROLE_ORGANIZATION_ELEMENT = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "potential-role-organization-element-assoc");
	QName ASSOC_DUTY_REGISTRAR = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "duty-registrar-assoc");

	QName TYPE_TERMS_OF_NOTIFICATION_SETTINGS = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "terms-of-notification-settings");

	QName PROP_N_DAYS = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "n-days");
	QName PROP_SHORT_N_DAYS = QName.createQName(GLOBAL_SETTINGS_NAMESPACE,"short-n-days");
	QName PROP_SHORT_LIMIT_DAYS = QName.createQName(GLOBAL_SETTINGS_NAMESPACE,"short-limit-days");
	QName PROP_ALLOW_SIGNING_ON_PAPER = QName.createQName(GLOBAL_SETTINGS_NAMESPACE,"allow-signing-on-paper");

	int DEFAULT_N_DAYS = 5;
	int DEFAULT_SHORT_N_DAYS = 1;
	int DEFAULT_SHORT_LIMIT_DAYS = 10;

	NodeRef getSettingsNode();
	NodeRef createSettingsNode() throws WriteTransactionNeededException;

	Collection<NodeRef> getPotentialWorkers(NodeRef businessRoleRef, NodeRef organizationElementRef);
	Collection<NodeRef> getPotentialWorkers(String businessRoleId, NodeRef organizationElementRef);

	void savePotentialWorkers(String businessRoleId, NodeRef orgElementRef, List<NodeRef> employeesRefs);
	void savePotentialWorkers(NodeRef businessRoleRef, NodeRef orgElementRef, List<NodeRef> employeesRefs);

	NodeRef createPotentialRole(NodeRef businessRoleRef, NodeRef orgElementRef, List<NodeRef> employeesRefs);
	NodeRef updatePotentialRole(NodeRef potentialRoleRef, List<NodeRef> employeesRefs);

	boolean isRegistrationCenralized();

	/**
	 * Разрешено подписание на бумажном носителе
	 */
	boolean isAllowSigningOnPaper();

	@Deprecated
	Boolean isHideProperties();

	NodeRef getArmDashletNode();

    NodeRef getArm();

	List<NodeRef> getRegistras(NodeRef employeeRef, String businessRoleId);

	/**
	 * Получение дежурного регистратора
	 * @return nodeRef дежурного регистратора
     */
	NodeRef getDutyRegistrar();

	@Deprecated
	String getLinksViewMode();

	/**
	 * Получение количества рабочих дней за которое должно высылаться уведомление
	 * @return Количество рабочих дней за которое должно высылаться уведомление
	 */
	int getSettingsNDays();

	/**
	 * Получение количества рабочих дней для краткосрочного исполнения за которое должно высылаться уведомление
	 * @return Количество рабочих дней для краткосрочного исполнения за которое должно высылаться уведомление
	 */
	int getSettingsShortNDays();

	/**
	 * Получение предельного количества календарных дней краткосрочного исполнения
	 * @return Предельное количество календарных дней краткосрочного исполнения
	 */
	int getSettingsShortLimitDays();

	/**
	 * Получение настроек сроков уведомлений
	 * @return узел с настройками сроков уведомлений
	 */
	NodeRef getTermsOfNotificationSettingsNode();

	/**
	 * Создание настроек сроков уведомлений
	 * Create an instance of {@link NodeRef }
	 */
	NodeRef createTermsOfNotificationSettingsNode();
}
