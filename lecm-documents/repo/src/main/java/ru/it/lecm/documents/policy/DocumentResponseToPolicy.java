package ru.it.lecm.documents.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;

/**
 * User: AIvkin
 * Date: 15.07.13
 * Time: 10:16
 */
public class DocumentResponseToPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {
	final static protected Logger logger = LoggerFactory.getLogger(DocumentResponseToPolicy.class);

	private PolicyComponent policyComponent;
	private DocumentConnectionService documentConnectionService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	final public void init() {
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
}
