package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * User: mshafeev
 * Date: 11.07.13
 * Time: 15:51
 */
public class ErrandsConnectionPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {
	final static protected Logger logger = LoggerFactory.getLogger(ErrandsConnectionPolicy.class);

	private PolicyComponent policyComponent;
	private DocumentConnectionService documentConnectionService;
	private NodeService nodeService;
	private DocumentService documentService;
	private DocumentMembersService documentMembersService;
	private ErrandsService errandsService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setErrandsService(ErrandsService errandsService) {
		this.errandsService = errandsService;
	}

	final public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				ErrandsService.TYPE_ERRANDS, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT, new JavaBehaviour(this, "onCreateAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				ErrandsService.TYPE_ERRANDS, ErrandsService.ASSOC_ERRANDS_EXECUTOR, new JavaBehaviour(this, "onCreateErrandExecutor"));
	}

	/**
	 * Добавление связи при создании поручения на основании документа
	 */
	@Override
	public void onCreateAssociation(AssociationRef associationRef) {
		documentConnectionService.createConnection(associationRef.getTargetRef(), associationRef.getSourceRef(), DocumentConnectionService.DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE, true, true);

		//обновление номера документа-основания в поручении
		NodeRef baseDoc = associationRef.getTargetRef();
		NodeRef errandDoc = associationRef.getSourceRef();

		QName type = nodeService.getType(baseDoc);
		if (type.equals(ErrandsService.TYPE_ERRANDS)) {
			NodeRef initiatorRef = nodeService.getTargetAssocs(baseDoc, ErrandsService.ASSOC_ERRANDS_INITIATOR).get(0).getTargetRef();
			documentMembersService.addMemberWithoutCheckPermission(errandDoc, initiatorRef, new HashMap<QName, Serializable>());
		}

		List<String> regNums = documentService.getRegNumbersValues(baseDoc);
		if (regNums != null && !regNums.isEmpty()) {
			String regNumberValue = "";
			for (String number : regNums) {
				if (number != null) {
					regNumberValue += ((regNumberValue.length() > 0 ? "," : "") + number);
				}
			}
			nodeService.setProperty(errandDoc, ErrandsService.PROP_BASE_DOC_NUMBER, regNumberValue);
		}

	    this.transferRightToBaseDocument(errandDoc);
	}

	public void onCreateErrandExecutor(AssociationRef associationRef) {
		NodeRef errandDoc = associationRef.getSourceRef();

		this.transferRightToBaseDocument(errandDoc);
	}

	public void transferRightToBaseDocument(NodeRef errandDoc) {
		if (errandsService.isTransferRightToBaseDocument()) {
			NodeRef baseDoc = errandsService.getBaseDocument(errandDoc);
			NodeRef executor = errandsService.getExecutor(errandDoc);

			if (baseDoc != null && executor != null) {
				if (nodeService.getType(baseDoc).equals(ErrandsService.TYPE_ERRANDS)) {
					List<NodeRef> connectedDocuments = documentConnectionService.getConnectedWithDocument(baseDoc, true);
					for (NodeRef document: connectedDocuments) {
						if (!nodeService.getType(document).equals(ErrandsService.TYPE_ERRANDS)) {
							documentConnectionService.createConnection(document, errandDoc, DocumentConnectionService.DICTIONARY_VALUE_FOR_INFORMATION, true, true);

							documentMembersService.addMemberWithoutCheckPermission(document, executor, new HashMap<QName, Serializable>());
						}
					}
				} else {
					documentMembersService.addMemberWithoutCheckPermission(baseDoc, executor, new HashMap<QName, Serializable>());
				}
			}
		}
	}

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}
}
