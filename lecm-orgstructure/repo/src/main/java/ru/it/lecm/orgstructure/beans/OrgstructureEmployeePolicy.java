package ru.it.lecm.orgstructure.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.PropertyCheck;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mShafeev
 * Date: 14.12.12
 * Time: 11:33
 */
public class OrgstructureEmployeePolicy implements NodeServicePolicies.OnCreateNodePolicy {
	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;
	private static OrgstructureBean orgstructureService;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		OrgstructureEmployeePolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		OrgstructureEmployeePolicy.policyComponent = policyComponent;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		OrgstructureEmployeePolicy.orgstructureService = orgstructureService;
	}


	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onCreateNode"));

	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef node = childAssocRef.getChildRef();
		NodeService nodeService = serviceRegistry.getNodeService();
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		// Получаем папку где сохраняются персональныен данные
		NodeRef personalDirectoryRef = orgstructureService.getPersonalDataDirectory();
		// Создаем пустые персональные данные
		ChildAssociationRef personalDataRef = nodeService.createNode(personalDirectoryRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				OrgstructureBean.TYPE_PERSONAL_DATA,
				properties);
		// Создаем ассоциацию сотруднику на персональные данные
		nodeService.createAssociation(node, personalDataRef.getChildRef(), OrgstructureBean.ASSOC_EMPLOYEE_PERSON_DATA);
	}
}
