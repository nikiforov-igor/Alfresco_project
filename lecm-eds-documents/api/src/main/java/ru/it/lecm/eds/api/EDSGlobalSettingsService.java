package ru.it.lecm.eds.api;

import java.util.Collection;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author dbayandin
 */
public interface EDSGlobalSettingsService {
	
	public static final String EDS_GLOBAL_SETTINGS_FOLDER_NAME = "Глобальные настройки СЭД";
	public static final String EDS_GLOBAL_SETTINGS_FOLDER_ID = "GLOBAL_EDS_SETTINGS_FOLDER_ID";
	
	public static final String EDS_GLOBAL_SETTINGS_NODE_NAME = "Settings-node";
	
	public static final String POTENTIAL_ROLES_DICTIONARY_NAME = "Потенциальные роли";
	
	public final static String GLOBAL_SETTINGS_PREFIX = "lecm-eds-globset";
	public final static String GLOBAL_SETTINGS_NAMESPACE = "http://www.it.ru/logicECM/eds-global-settings/1.0";

	public final static QName TYPE_SETTINGS = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "settings");
	public final static QName PROP_SETTINGS_CENTRALIZED_REGISTRATION = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "centralized-registration");
	public final static QName PROP_SETTINGS_HIDE_PROPS = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "hide-properties-for-recipients");

	public final static QName TYPE_POTENTIAL_ROLE = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "potential-role");
	public final static QName PROP_POTENTIAL_ROLE_BUSINESS_ROLE_REF = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "potential-role-business-role-assoc-ref");
	public final static QName PROP_POTENTIAL_ROLE_ORG_ELEMENT_REF = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "potential-role-organization-element-assoc-ref");
	public final static QName ASSOC_POTENTIAL_ROLE_BUSINESS_ROLE = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "potential-role-business-role-assoc");
	public final static QName ASSOC_POTENTIAL_ROLE_EMPLOYEE = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "potential-role-employee-assoc");
	public final static QName ASSOC_POTENTIAL_ROLE_ORGANIZATION_ELEMENT = QName.createQName(GLOBAL_SETTINGS_NAMESPACE, "potential-role-organization-element-assoc");
	
	public NodeRef getServiceRootFolder();
	public NodeRef getSettingsNode();

	public Collection<NodeRef> getPotentialWorkers(NodeRef businessRoleRef, NodeRef organizationElementRef);
	public Collection<NodeRef> getPotentialWorkers(String businessRoleId, NodeRef organizationElementRef);

	public void savePotentialWorkers(String businessRoleId, NodeRef orgElementRef, List<NodeRef> employeesRefs);
	public void savePotentialWorkers(NodeRef businessRoleRef, NodeRef orgElementRef, List<NodeRef> employeesRefs);
	
	public NodeRef createPotentialRole(NodeRef businessRoleRef, NodeRef orgElementRef, List<NodeRef> employeesRefs);
	public NodeRef updatePotentialRole(NodeRef potentialRoleRef, List<NodeRef> employeesRefs);
	
	public Boolean isRegistrationCenralized();

	public Boolean isHideProperties();
}
