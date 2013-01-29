package ru.it.lecm.orgstructure.policies;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBeanImpl;

/**
 * @author dbashmakov
 *         Date: 28.01.13
 *         Time: 10:37
 */
public class OrgstructureUnitPolicy implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.OnUpdatePropertiesPolicy{

	final static protected Logger logger = LoggerFactory.getLogger(OrgstructureEmployeePolicy.class);

	private ServiceRegistry serviceRegistry;
	private PolicyComponent policyComponent;
	private OrgstructureBean orgstructureService;

	private BusinessJournalService businessJournalService;

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}
	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef unit = childAssocRef.getChildRef();
		NodeRef parent = orgstructureService.getParent(unit);

		List<String> objects = new ArrayList<String>(1);
		if (parent != null) {
			objects.add(parent.toString());
		} else { // корневое подразделение - берем Организацию
			objects.add(orgstructureService.getOrganizationRootRef().toString());
		}

		AuthenticationService authService = serviceRegistry.getAuthenticationService();
		String initiator = authService.getCurrentUserName();
		try {
			businessJournalService.log(initiator, unit, BusinessJournalService.EventCategories.ADD.toString(), "Созданo новое подразделение #mainobject в подразделении #object1", objects);
		} catch (Exception e) {
			logger.error("Не удалось создать запись бизнес-журнала", e);
		}
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		Boolean prevActive = (Boolean) before.get(OrgstructureBeanImpl.IS_ACTIVE);
		Boolean curActive = (Boolean) after.get(OrgstructureBeanImpl.IS_ACTIVE);

		if ((prevActive == null && curActive != null && !curActive) || prevActive != null && !prevActive.equals(curActive) && !curActive) {
			NodeRef parent = orgstructureService.getParent(nodeRef);

			List<String> objects = new ArrayList<String>(1);
			if (parent != null) {
				objects.add(parent.toString());
			} else { // корневое подразделение - берем Организацию
				objects.add(orgstructureService.getOrganizationRootRef().toString());
			}

			AuthenticationService authService = serviceRegistry.getAuthenticationService();
			String initiator = authService.getCurrentUserName();
			try {
				businessJournalService.log(initiator, nodeRef, BusinessJournalService.EventCategories.DELETE.toString(), "Подразделение \"#mainobject\" в подразделении #object1 расформировано", objects);
			} catch (Exception e) {
				logger.error("Не удалось создать запись бизнес-журнала", e);
			}
		}
	}
}
