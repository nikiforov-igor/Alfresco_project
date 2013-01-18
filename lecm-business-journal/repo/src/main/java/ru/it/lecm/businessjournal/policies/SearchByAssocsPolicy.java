package ru.it.lecm.businessjournal.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

/**
 * @author dbashmakov
 *         Date: 17.01.13
 *         Time: 10:01
 */
public class SearchByAssocsPolicy implements  NodeServicePolicies.OnCreateAssociationPolicy{
	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		SearchByAssocsPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		SearchByAssocsPolicy.policyComponent = policyComponent;
	}


	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		// BRRecord
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				BusinessJournalService.TYPE_BR_RECORD, new JavaBehaviour(this, "onCreateAssociation"));

		//Message Template
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				BusinessJournalService.TYPE_MESSAGE_TEMPLATE, new JavaBehaviour(this, "onCreateAssociation"));
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NamespaceService nameService = serviceRegistry.getNamespaceService();
		NodeService nodeService = serviceRegistry.getNodeService();

		NodeRef record = nodeAssocRef.getSourceRef();
		String assocQName = nodeAssocRef.getTypeQName().toPrefixString(nameService);

		nodeService.setProperty(record, QName.createQName(assocQName + "-ref", nameService), nodeAssocRef.getTargetRef().toString());
	}
}
