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
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * @author dbashmakov
 *         Date: 28.01.13
 *         Time: 15:07
 */
public class OrgstructureStaffPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

	private final static Logger logger = LoggerFactory.getLogger(OrgstructureStaffPolicy.class);

	private ServiceRegistry serviceRegistry;
	private PolicyComponent policyComponent;
	private OrgstructureBean orgstructureService;

	private BusinessJournalService businessJournalService;

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_LIST, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		Boolean prevPrimary = (Boolean) before.get(OrgstructureBean.PROP_STAFF_LIST_IS_BOSS);
		Boolean curPrimary = (Boolean) after.get(OrgstructureBean.PROP_STAFF_LIST_IS_BOSS);

		NodeRef employee = orgstructureService.getEmployeeByPosition(nodeRef);

		if (prevPrimary != null && !prevPrimary.equals(curPrimary) && employee != null) {
			NodeRef unit = serviceRegistry.getNodeService().getPrimaryParent(nodeRef).getParentRef();

			String initiator = serviceRegistry.getAuthenticationService().getCurrentUserName();

			String category;
			String defaultDescription;
			if (curPrimary) {
				defaultDescription = "Сотрудник #mainobject назначен руководителем в подразделении #object1";
				category = "Назначение руководителем подразделения";

			} else {
				defaultDescription = "Сотрудник #mainobject снят с руководящей позиции в подразделении #object1";
				category = "Снятие с назначения руководителем подразделения";
			}
			List<String> objects = new ArrayList<String>(1);
			objects.add(unit.toString());
			try {
				businessJournalService.log(initiator, employee, category, defaultDescription, objects);
			} catch (Exception e) {
				logger.error("Не удалось создать запись бизнес-журнала", e);
			}
		}
	}
}
