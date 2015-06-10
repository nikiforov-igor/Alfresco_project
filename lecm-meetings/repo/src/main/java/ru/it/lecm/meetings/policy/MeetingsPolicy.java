package ru.it.lecm.meetings.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.meetings.beans.MeetingsService;
import ru.it.lecm.security.LecmPermissionService;

/**
 *
 * @author vkuprin
 */
public class MeetingsPolicy extends BaseBean implements NodeServicePolicies.OnUpdatePropertiesPolicy {

	public static final String FILE_PREFIX_STRING = "Пункт_";
	public static final String FILE_DEFAULT_CATEGORY = "Вложения";
	public static final String MEETINGS_TRANSACTION_LISTENER = "meetings_transaction_listener";

	private DocumentAttachmentsService documentAttachmentsService;
	private DocumentTableService documentTableService;
	private PolicyComponent policyComponent;
	private TransactionListener transactionListener;
	private BehaviourFilter behaviourFilter;
	private LecmPermissionService lecmPermissionService;

	public LecmPermissionService getLecmPermissionService() {
		return lecmPermissionService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}
	
	public BehaviourFilter getBehaviourFilter() {
		return behaviourFilter;
	}

	public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
		this.behaviourFilter = behaviourFilter;
	}
	
	public DocumentAttachmentsService getDocumentAttachmentsService() {
		return documentAttachmentsService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public PolicyComponent getPolicyComponent() {
		return policyComponent;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public DocumentTableService getDocumentTableService() {
		return documentTableService;
	}

	public void setDocumentTableService(DocumentTableService documentTableService) {
		this.documentTableService = documentTableService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public void init() {
		transactionListener = new MeetingsPolicyTransactionListener();

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				MeetingsService.TYPE_MEETINGS_TS_AGENDA_ITEM,
				new JavaBehaviour(this, "onCreateAgendaItem", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				MeetingsService.TYPE_MEETINGS_DOCUMENT,
				new JavaBehaviour(this, "onCreateMeeting", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				MeetingsService.TYPE_MEETINGS_TS_AGENDA_ITEM,
				new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				MeetingsService.TYPE_MEETINGS_DOCUMENT, MeetingsService.ASSOC_MEETINGS_CHAIRMAN, 
				new JavaBehaviour(this, "onChairmanAdded", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				MeetingsService.TYPE_MEETINGS_DOCUMENT, MeetingsService.ASSOC_MEETINGS_CHAIRMAN, 
				new JavaBehaviour(this, "onChairmanRemoved", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				MeetingsService.TYPE_MEETINGS_DOCUMENT, MeetingsService.ASSOC_MEETINGS_SECRETARY, 
				new JavaBehaviour(this, "onSecretaryAdded", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				MeetingsService.TYPE_MEETINGS_DOCUMENT, MeetingsService.ASSOC_MEETINGS_SECRETARY, 
				new JavaBehaviour(this, "onSecretaryRemoved", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void onChairmanAdded(AssociationRef nodeAssocRef) {
		NodeRef event = nodeAssocRef.getSourceRef();
		NodeRef chairman = nodeAssocRef.getTargetRef();
		//TODO Тут нужно поменять роль на Мероприятия. Инициатор. когда появится.
		lecmPermissionService.grantDynamicRole("EVENTS_MEMBER_DYN", event, chairman.getId(), lecmPermissionService.findPermissionGroup("LECM_BASIC_PG_ActionPerformer"));
	}
	
	public void onChairmanRemoved(AssociationRef nodeAssocRef) {
		NodeRef event = nodeAssocRef.getSourceRef();
		NodeRef chairman = nodeAssocRef.getTargetRef();
		lecmPermissionService.revokeDynamicRole("EVENTS_MEMBER_DYN", event, chairman.getId());
		lecmPermissionService.grantAccess(lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Reader), event, chairman);
	}
	
	public void onSecretaryAdded(AssociationRef nodeAssocRef) {
		NodeRef event = nodeAssocRef.getSourceRef();
		NodeRef secretary = nodeAssocRef.getTargetRef();
		//TODO Тут нужно поменять роль на Мероприятия. Инициатор. когда появится.
		lecmPermissionService.grantDynamicRole("EVENTS_MEMBER_DYN", event, secretary.getId(), lecmPermissionService.findPermissionGroup("LECM_BASIC_PG_ActionPerformer"));
	}
	
	public void onSecretaryRemoved(AssociationRef nodeAssocRef) {
		NodeRef event = nodeAssocRef.getSourceRef();
		NodeRef secretary = nodeAssocRef.getTargetRef();
		lecmPermissionService.revokeDynamicRole("EVENTS_MEMBER_DYN", event, secretary.getId());
		lecmPermissionService.grantAccess(lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Reader), event, secretary);
	}

	public void onCreateMeeting(ChildAssociationRef childAssocRef) {
		NodeRef document = childAssocRef.getChildRef();
		List<AssociationRef> items = nodeService.getTargetAssocs(document, MeetingsService.ASSOC_MEETINGS_TEMP_ITEMS);
		Integer index = 0;
		
		for (AssociationRef itemAssoc : items) {
			index++;
			NodeRef item = itemAssoc.getTargetRef();
			try {
				behaviourFilter.disableBehaviour(item);
				NodeRef table = documentTableService.getTable(document, MeetingsService.TYPE_MEETINGS_TS_AGENDA_TABLE);
				String assocName = nodeService.getProperty(item, ContentModel.PROP_NAME).toString();
				QName itemAssocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, assocName);
				nodeService.moveNode(item, table, ContentModel.ASSOC_CONTAINS, itemAssocQName);
				nodeService.setProperty(item, DocumentTableService.PROP_INDEX_TABLE_ROW, index);
				//нужно дёрнуть, чтоб создать папки категорий вложений
				documentAttachmentsService.getCategories(document);
				moveFiles(document, item);
			} finally {
				behaviourFilter.enableBehaviour(item);
			}
		}
		refreshFiles(document);
	}

	public void onCreateAgendaItem(ChildAssociationRef childAssocRef) {
		NodeRef row = childAssocRef.getChildRef();
		NodeRef document = documentTableService.getDocumentByTableDataRow(row);
		moveFiles(document, row);
	}

	private void moveFiles(NodeRef document, NodeRef row) {
		if (null != document && null != row) {
			List<AssociationRef> files = nodeService.getTargetAssocs(row, MeetingsService.ASSOC_MEETINGS_TS_ITEM_ATTACHMENTS);
			for (AssociationRef fileAssoc : files) {
				NodeRef file = fileAssoc.getTargetRef();
				documentAttachmentsService.addAttachment(file, documentAttachmentsService.getCategory(FILE_DEFAULT_CATEGORY, document));
			}
		}
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {

		Object beforeIndex = before.get(DocumentTableService.PROP_INDEX_TABLE_ROW);
		Object afterIndex = after.get(DocumentTableService.PROP_INDEX_TABLE_ROW);
		if (null != afterIndex && (null == beforeIndex || !beforeIndex.equals(afterIndex))) {
			NodeRef document = documentTableService.getDocumentByTableDataRow(nodeRef);
			if (null != document) {
				AlfrescoTransactionSupport.bindListener(this.transactionListener);

				List<NodeRef> pendingActions = AlfrescoTransactionSupport.getResource(MEETINGS_TRANSACTION_LISTENER);
				if (pendingActions == null) {
					pendingActions = new ArrayList<>();
					AlfrescoTransactionSupport.bindResource(MEETINGS_TRANSACTION_LISTENER, pendingActions);
				}

				if (!pendingActions.contains(document)) {
					pendingActions.add(document);
				}
			}
		}

	}

	public void refreshFiles(NodeRef document) {
		NodeRef table = documentTableService.getTable(document, MeetingsService.TYPE_MEETINGS_TS_AGENDA_TABLE);
		List<NodeRef> rows = documentTableService.getTableDataRows(table);
		String regexp = "^(" + FILE_PREFIX_STRING + "\\d*_)*";
		for (NodeRef row : rows) {
			List<AssociationRef> files = nodeService.getTargetAssocs(row, MeetingsService.ASSOC_MEETINGS_TS_ITEM_ATTACHMENTS);
			Object index = nodeService.getProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW);
			String newPrefix = FILE_PREFIX_STRING + index.toString() + "_";
			for (AssociationRef fileAssoc : files) {
				NodeRef file = fileAssoc.getTargetRef();
				String currentName = nodeService.getProperty(file, ContentModel.PROP_NAME).toString();
				String fileName = currentName.replaceFirst(regexp, newPrefix);
				nodeService.setProperty(file, ContentModel.PROP_NAME, fileName);
			}
		}

	}

	private class MeetingsPolicyTransactionListener implements TransactionListener {

		@Override
		public void flush() {

		}

		@Override
		public void beforeCommit(boolean readOnly) {

		}

		@Override
		public void beforeCompletion() {

		}

		@Override
		public void afterCommit() {
			List<NodeRef> pendingDocs = AlfrescoTransactionSupport.getResource(MEETINGS_TRANSACTION_LISTENER);
			if (pendingDocs != null) {
				while (!pendingDocs.isEmpty()) {
					final NodeRef document = pendingDocs.remove(0);
					transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {

						@Override
						public Void execute() throws Throwable {
							refreshFiles(document);
							return null;
						}

					}, false, true);
				}
			}
		}

		@Override
		public void afterRollback() {

		}

	}

}
