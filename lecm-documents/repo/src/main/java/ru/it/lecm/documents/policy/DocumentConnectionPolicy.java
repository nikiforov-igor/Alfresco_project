package ru.it.lecm.documents.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentConnectionService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 20.02.13
 * Time: 9:52
 */
public class DocumentConnectionPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.BeforeDeleteNodePolicy {
	private PolicyComponent policyComponent;
	private NodeService nodeService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public final void init() {
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DocumentConnectionService.TYPE_CONNECTION, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT,
				new JavaBehaviour(this, "onCreateAssociation"));

		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				DocumentConnectionService.TYPE_CONNECTION, new JavaBehaviour(this, "beforeDeleteNode"));
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NodeRef documentRef = nodeAssocRef.getTargetRef();
		if (!nodeService.hasAspect(documentRef, DocumentConnectionService.ASPECT_HAS_CONNECTED_DOCUMENTS)){
            Map<QName, Serializable> aspectValues = new HashMap<QName, Serializable>();
            aspectValues.put(DocumentConnectionService.PROP_CONNECTIONS_WITH_LIST, "");

			nodeService.addAspect(documentRef, DocumentConnectionService.ASPECT_HAS_CONNECTED_DOCUMENTS, aspectValues);
		}
	}

	@Override
	public void beforeDeleteNode(NodeRef nodeRef) {
		List<AssociationRef> connectedDocumentList = nodeService.getTargetAssocs(nodeRef, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT);
		if (connectedDocumentList.size() == 1) {
			NodeRef documentRef = connectedDocumentList.get(0).getTargetRef();

			List<AssociationRef> assocs = nodeService.getSourceAssocs(documentRef, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT);
			int size = 0;
			for (AssociationRef assocRef: assocs) {
				if (!assocRef.getSourceRef().getStoreRef().equals(StoreRef.STORE_REF_ARCHIVE_SPACESSTORE)) {
					size++;
				}
			}
			if (size == 1) {
				nodeService.removeAspect(documentRef, DocumentConnectionService.ASPECT_HAS_CONNECTED_DOCUMENTS);
			}
		}
	}
}
