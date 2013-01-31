package ru.it.lecm.subscriptions.policy;

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
import ru.it.lecm.subscriptions.beans.SubscriptionsService;

/**
 * User: PMelnikov
 * Date: 17.01.13
 * Time: 17:21
 */
public class TypeCategoryPolicy  implements  NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {

	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		TypeCategoryPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		TypeCategoryPolicy.policyComponent = policyComponent;
	}


	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				SubscriptionsService.TYPE_SUBSCRIPTION_TO_TYPE, SubscriptionsService.ASSOC_OBJECT_TYPE,
				new JavaBehaviour(this, "onDeleteAssociation"));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				SubscriptionsService.TYPE_SUBSCRIPTION_TO_TYPE, SubscriptionsService.ASSOC_EVENT_CATEGORY,
				new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				SubscriptionsService.TYPE_SUBSCRIPTION_TO_TYPE, SubscriptionsService.ASSOC_OBJECT_TYPE,
				new JavaBehaviour(this, "onCreateAssociation"));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				SubscriptionsService.TYPE_SUBSCRIPTION_TO_TYPE, SubscriptionsService.ASSOC_EVENT_CATEGORY,
				new JavaBehaviour(this, "onCreateAssociation"));
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NamespaceService nameService = serviceRegistry.getNamespaceService();
		NodeService nodeService = serviceRegistry.getNodeService();

		NodeRef record = nodeAssocRef.getSourceRef();
		String assocQName = nodeAssocRef.getTypeQName().toPrefixString(nameService);

		nodeService.setProperty(record, QName.createQName(assocQName + "-ref", nameService), nodeAssocRef.getTargetRef().toString());
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		NamespaceService nameService = serviceRegistry.getNamespaceService();
		NodeService nodeService = serviceRegistry.getNodeService();

		NodeRef record = nodeAssocRef.getSourceRef();
		String assocQName = nodeAssocRef.getTypeQName().toPrefixString(nameService);

		nodeService.setProperty(record, QName.createQName(assocQName + "-ref", nameService), "");
	}
}