package ru.it.lecm.documents.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.DocumentEventCategory;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.NotificationUnit;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 20.02.13
 * Time: 9:52
 */
public class DocumentConnectionPolicy implements NodeServicePolicies.OnCreateAssociationPolicy,
		NodeServicePolicies.BeforeDeleteNodePolicy,
		NodeServicePolicies.BeforeCreateNodePolicy,
		NodeServicePolicies.OnCreateNodePolicy {

	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private BusinessJournalService businessJournalService;
	private LecmPermissionService lecmPermissionService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public final void init() {
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DocumentConnectionService.TYPE_CONNECTION, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT,
				new JavaBehaviour(this, "onCreateAssociation"));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DocumentConnectionService.TYPE_CONNECTION, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				DocumentConnectionService.TYPE_CONNECTION, new JavaBehaviour(this, "beforeDeleteNode"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeCreateNodePolicy.QNAME,
				DocumentConnectionService.TYPE_CONNECTION, new JavaBehaviour(this, "beforeCreateNode"));
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
			NodeRef connectedDocument = connectedDocumentList.get(0).getTargetRef();

			List<AssociationRef> assocs = nodeService.getSourceAssocs(connectedDocument, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT);
			int size = 0;
			for (AssociationRef assocRef: assocs) {
				if (!assocRef.getSourceRef().getStoreRef().equals(StoreRef.STORE_REF_ARCHIVE_SPACESSTORE)) {
					size++;
				}
			}
			if (size == 1) {
				nodeService.removeAspect(connectedDocument, DocumentConnectionService.ASPECT_HAS_CONNECTED_DOCUMENTS);
			}

			NodeRef primaryDocument = null;
			List<AssociationRef> primaryDocumentAssocs = nodeService.getTargetAssocs(nodeRef, DocumentConnectionService.ASSOC_PRIMARY_DOCUMENT);
			if (primaryDocumentAssocs != null && primaryDocumentAssocs.size() == 1) {
				primaryDocument = primaryDocumentAssocs.get(0).getTargetRef();
			}

			if (primaryDocument != null && connectedDocument != null) {
				final List<String> objects = new ArrayList<String>(1);
				objects.add(connectedDocument.toString());

				businessJournalService.log(primaryDocument, EventCategory.DELETE_DOCUMENT_CONNECTION, "Сотрудник #initiator удалил связь документов #mainobject и #object1", objects);
			}
		}
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssociationRef) {
		NodeRef connection = childAssociationRef.getChildRef();

		NodeRef primaryDocument = null;
		List<AssociationRef> primaryDocumentAssocs = nodeService.getTargetAssocs(connection, DocumentConnectionService.ASSOC_PRIMARY_DOCUMENT);
		if (primaryDocumentAssocs != null && primaryDocumentAssocs.size() == 1) {
			primaryDocument = primaryDocumentAssocs.get(0).getTargetRef();
		}

		NodeRef connectedDocument = null;
		List<AssociationRef> connectedDocumentAssocs = nodeService.getTargetAssocs(connection, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT);
		if (connectedDocumentAssocs != null && connectedDocumentAssocs.size() == 1) {
			connectedDocument = connectedDocumentAssocs.get(0).getTargetRef();
		}

		if (primaryDocument != null && connectedDocument != null) {
			final List<String> objects = new ArrayList<String>(1);
			objects.add(connectedDocument.toString());

			businessJournalService.log(primaryDocument, EventCategory.CREATE_DOCUMENT_CONNECTION, "Сотрудник #initiator связал документ #mainobject и документ #object1", objects);
		}
	}

	@Override
	public void beforeCreateNode(NodeRef parentRef, QName assocTypeQName, QName assocQName, QName nodeTypeQName) {
		int a = 1 + 2;
	}
}
