package ru.it.lecm.businessjournal.policies;

import java.util.HashSet;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

/**
 * @author dbashmakov
 *         Date: 11.01.13
 *         Time: 16:48
 */
public class BusinessEventsPolicy implements  NodeServicePolicies.OnCreateNodePolicy {
	public static final String ORGSTRUCTURE_NAMESPACE_URI = "http://www.it.ru/lecm/org/structure/1.0";

	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;
	private static BusinessJournalService businessJournalService;

	private final Set<QName> AFFECTED_TYPES
			= new HashSet<QName>() {{
		add(QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee"));
		add(QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workGroup"));
		add(QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staff-list"));
		add(QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workforce"));
		add(QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staffPosition"));
		add(QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workRole"));
		add(QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "organization-unit"));
	}};
	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		BusinessEventsPolicy.businessJournalService = businessJournalService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		BusinessEventsPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		BusinessEventsPolicy.policyComponent = policyComponent;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
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

			if (AFFECTED_TYPES.contains(serviceRegistry.getNodeService().getType(createdObj))){
				// категория события
				String eventCategory = "Создание";
				// дефолтное описание события (если не будет найдено в справочнике)
				String description = "Создан новый объект #mainobject";

				businessJournalService.log(initiator, createdObj, eventCategory, description, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
