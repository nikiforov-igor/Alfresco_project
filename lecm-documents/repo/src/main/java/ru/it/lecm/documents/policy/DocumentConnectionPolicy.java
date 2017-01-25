package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.BeforeCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentConnectionServiceImpl;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 20.02.13
 * Time: 9:52
 */
public class DocumentConnectionPolicy implements OnCreateAssociationPolicy/*, OnDeleteAssociationPolicy*/, BeforeDeleteNodePolicy, BeforeCreateNodePolicy, OnCreateNodePolicy {

	final protected Logger logger = LoggerFactory.getLogger(DocumentConnectionPolicy.class);
	
	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private BusinessJournalService businessJournalService;
	private LecmPermissionService lecmPermissionService;
    private DocumentConnectionServiceImpl documentConnectionService;
//	private BehaviourFilter behaviourFilter;

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

    public void setDocumentConnectionService(DocumentConnectionServiceImpl documentConnectionService) {
        this.documentConnectionService = documentConnectionService;
    }

//	public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
//		this.behaviourFilter = behaviourFilter;
//	}

	public final void init() {
		policyComponent.bindAssociationBehaviour(OnCreateAssociationPolicy.QNAME,
				DocumentConnectionService.TYPE_CONNECTION, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT,
				new JavaBehaviour(this, "onCreateAssociation"));

        policyComponent.bindAssociationBehaviour(OnCreateAssociationPolicy.QNAME,
                DocumentService.TYPE_BASE_DOCUMENT, DocumentConnectionService.ASSOC_TEMP_CONNECTION,
                new JavaBehaviour(this, "onCreateTempAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindClassBehaviour(OnCreateNodePolicy.QNAME,
				DocumentConnectionService.TYPE_CONNECTION, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(BeforeDeleteNodePolicy.QNAME,
				DocumentConnectionService.TYPE_CONNECTION, new JavaBehaviour(this, "beforeDeleteNode"));
		policyComponent.bindClassBehaviour(BeforeCreateNodePolicy.QNAME,
				DocumentConnectionService.TYPE_CONNECTION, new JavaBehaviour(this, "beforeCreateNode"));

//		policyComponent.bindAssociationBehaviour(OnDeleteAssociationPolicy.QNAME,
//				DocumentConnectionService.TYPE_CONNECTION, DocumentConnectionService.ASSOC_PRIMARY_DOCUMENT,
//				new JavaBehaviour(this, "onDeleteAssociation"));
//		policyComponent.bindAssociationBehaviour(OnDeleteAssociationPolicy.QNAME,
//				DocumentConnectionService.TYPE_CONNECTION, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT,
//				new JavaBehaviour(this, "onDeleteAssociation"));
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		logger.debug("ДОКУМЕНТ. onCreateAssociation");
		NodeRef documentRef = nodeAssocRef.getTargetRef();
		if (!nodeService.hasAspect(documentRef, DocumentConnectionService.ASPECT_HAS_CONNECTED_DOCUMENTS)) {
			Map<QName, Serializable> aspectValues = new HashMap<QName, Serializable>();
			aspectValues.put(DocumentConnectionService.PROP_CONNECTIONS_WITH_LIST, "");

			nodeService.addAspect(documentRef, DocumentConnectionService.ASPECT_HAS_CONNECTED_DOCUMENTS, aspectValues);
		}
	}

//	@Override
//	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
//		NodeRef documentRef = nodeAssocRef.getTargetRef();
//		NodeRef connectionRef = nodeAssocRef.getSourceRef();
//		QName propDocumentRemoved = null;
//		if (DocumentConnectionService.ASSOC_PRIMARY_DOCUMENT.isMatch(nodeAssocRef.getTypeQName())) {
//			propDocumentRemoved = DocumentConnectionService.PROP_PRIMARY_DOCUMENT_REMOVED;
//		} else if (DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT.isMatch(nodeAssocRef.getTypeQName())) {
//			propDocumentRemoved = DocumentConnectionService.PROP_CONNECTED_DOCUMENT_REMOVED;
//		}
//		String presentString = (String)nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING);
//		String dt = new SimpleDateFormat("HH:mm dd.MM.yyyy").format(new Date());
//		String msg = String.format("Документ %s был удален %s", presentString, dt);
//		nodeService.setProperty(connectionRef, propDocumentRemoved, msg);
//		List<AssociationRef> primary = nodeService.getTargetAssocs(connectionRef, DocumentConnectionService.ASSOC_PRIMARY_DOCUMENT);
//		List<AssociationRef> connected = nodeService.getTargetAssocs(connectionRef, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT);
//		if (primary.isEmpty() && connected.isEmpty()) {
//			behaviourFilter.disableBehaviour(connectionRef);
//			nodeService.addAspect(connectionRef, ContentModel.ASPECT_TEMPORARY, null);
//			nodeService.deleteNode(connectionRef);
//		}
//	}

	@Override
	public void beforeDeleteNode(NodeRef nodeRef) {
		logger.debug("ДОКУМЕНТ. beforeDeleteNode");
		List<AssociationRef> connectedDocumentList = nodeService.getTargetAssocs(nodeRef, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT);
		if (connectedDocumentList.size() == 1) {
			NodeRef connectedDocument = connectedDocumentList.get(0).getTargetRef();

			List<AssociationRef> assocs = nodeService.getSourceAssocs(connectedDocument, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT);
			int size = 0;
			for (AssociationRef assocRef : assocs) {
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
				this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_DELETE, primaryDocument);

				List<String> objects = new ArrayList<String>(1);
				objects.add(connectedDocument.toString());

				businessJournalService.log(primaryDocument, EventCategory.DELETE_DOCUMENT_CONNECTION, "#initiator удалил(а) связь документов #mainobject и #object1", objects);
			}
		}
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssociationRef) {
		logger.debug("ДОКУМЕНТ. onCreateNode");
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

			businessJournalService.log(primaryDocument, EventCategory.CREATE_DOCUMENT_CONNECTION, "#initiator связал(а) документ #mainobject и документ #object1", objects);
		}
	}

	@Override
	public void beforeCreateNode(NodeRef parentRef, QName assocTypeQName, QName assocQName, QName nodeTypeQName) {
		logger.debug("ДОКУМЕНТ. beforeCreateNode");
        // ALF-1583
        // При добавлении поручения через блок "Задачи" появляется сообщение "Ваши изменения не удалось сохранить"
        // В транзакцию добавляется переменная DocumentConnectionService.DO_NOT_CHECK_PERMISSION_CREATE_DOCUMENT_LINKS,
        // позволяющая отключить прооверку прав на создание связи к документу.
        // Переменная устанавливается в методе ru.it.lecm.documents.beans.DocumentConnectionServiceImpl.createConnection()
        // Проверяется в ru.it.lecm.documents.policy.DocumentConnectionPolicy.beforeCreateNode()
        Boolean doNotCheckPermission = AlfrescoTransactionSupport.getResource(DocumentConnectionService.DO_NOT_CHECK_PERMISSION_CREATE_DOCUMENT_LINKS);
        if (doNotCheckPermission != null && doNotCheckPermission) {
            return;
        }

		NodeRef document = null;
		if (nodeService.getProperty(parentRef, ContentModel.PROP_NAME).equals(DocumentConnectionService.DOCUMENT_CONNECTIONS_ROOT_NAME)) {
			document = nodeService.getPrimaryParent(parentRef).getParentRef();
		}
		if (document != null) {
			lecmPermissionService.checkPermission(LecmPermissionService.PERM_LINKS_CREATE, document);
		}
	}

    public void onCreateTempAssociation(AssociationRef nodeAssocRef) {
    	logger.debug("ДОКУМЕНТ. onCreateTempAssociation");
        //Текущий документ
        NodeRef primary = nodeAssocRef.getSourceRef();
        //Временная связь
        NodeRef tempConnection = nodeAssocRef.getTargetRef();
        //Присоединяемый документ
        NodeRef connected = nodeService.getTargetAssocs(tempConnection, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT).get(0).getTargetRef();
        //Тип связи
        NodeRef type = nodeService.getTargetAssocs(tempConnection, DocumentConnectionService.ASSOC_CONNECTION_TYPE).get(0).getTargetRef();
        //Создаем связь
        documentConnectionService.createConnection(primary, connected, type, false, true);
        //Удаляем временную связь
        nodeService.deleteNode(tempConnection);
    }

}
