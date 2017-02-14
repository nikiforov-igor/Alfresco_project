package ru.it.lecm.documents.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;

import java.util.List;
import java.util.Objects;

/**
 * User: AIvkin
 * Date: 15.07.13
 * Time: 10:16
 */
public class DocumentResponseToPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {
	final static protected Logger logger = LoggerFactory.getLogger(DocumentResponseToPolicy.class);

	private PolicyComponent policyComponent;
	private DocumentConnectionService documentConnectionService;
	private NodeService nodeService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	final public void init() {
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, DocumentService.ASSOC_RESPONSE_TO, new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, DocumentService.ASSOC_RESPONSE_TO, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	/**
	 * Добавление связи при создании поручения на основании документа
	 */
	@Override
	public void onCreateAssociation(AssociationRef associationRef) {
		logger.debug("ДОКУМЕНТ. onCreateAssociation");
		documentConnectionService.createConnection(associationRef.getSourceRef(), associationRef.getTargetRef(), "inResponseTo", true);
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		NodeRef parent = nodeAssocRef.getSourceRef();
		NodeRef connected = nodeAssocRef.getTargetRef();

		List<NodeRef> connections = documentConnectionService.getConnectionsWithDocument(connected, "inResponseTo", false);
		for (NodeRef connection : connections) {
			NodeRef targetDoc = nodeService.getTargetAssocs(connection, DocumentConnectionService.ASSOC_PRIMARY_DOCUMENT).get(0).getTargetRef();
			if (Objects.equals(targetDoc, parent)) {
				Boolean isSystem = Boolean.TRUE.equals(nodeService.getProperty(connection, DocumentConnectionService.PROP_IS_SYSTEM));
				if (isSystem) {
					documentConnectionService.deleteConnection(connection);
				}
			}
		}
	}
}
