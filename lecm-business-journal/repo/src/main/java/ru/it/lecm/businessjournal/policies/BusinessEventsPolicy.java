package ru.it.lecm.businessjournal.policies;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * @author dbashmakov
 *         Date: 11.01.13
 *         Time: 16:48
 */
public class BusinessEventsPolicy implements  NodeServicePolicies.OnCreateNodePolicy {
	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;
	private static OrgstructureBean orgstructureService;
	private static BusinessJournalService businessJournalService;

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		BusinessEventsPolicy.businessJournalService = businessJournalService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		BusinessEventsPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		BusinessEventsPolicy.policyComponent = policyComponent;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		BusinessEventsPolicy.orgstructureService = orgstructureService;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				ContentModel.TYPE_CMOBJECT, new JavaBehaviour(this, "onCreateNode"));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		try {
			// получаем инициатора
			AuthenticationService authService = serviceRegistry.getAuthenticationService();
			String initiator = authService.getCurrentUserName();

			// получаем основной объект
			NodeRef createdObj = childAssocRef.getChildRef();

			// категория события
			String eventCategory = "Создание";
			// дефолтное описание события (если не будет найдено в справочнике)
			String description = "Создан новый объект #mainobject";

			businessJournalService.fire(initiator, createdObj, eventCategory, description, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
