package ru.it.lecm.approval;

import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.ParameterCheck;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.approval.api.ApprovalListService;
import static ru.it.lecm.approval.api.ApprovalListService.PROP_APPROVAL_LIST_APPROVE_DATE;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vlevin
 */
public class ApprovalListServiceImpl extends BaseBean implements ApprovalListService {


	private final static Logger logger = LoggerFactory.getLogger(ApprovalListServiceImpl.class);
	private final static QName TYPE_CONTRACT_DOCUMENT = QName.createQName("http://www.it.ru/logicECM/contract/1.0", "document");
	private final static QName TYPE_CONTRACT_FAKE_DOCUMENT = QName.createQName("http://www.it.ru/logicECM/contract/fake/1.0", "document");

	private OrgstructureBean orgstructureService;
	private DocumentAttachmentsService documentAttachmentsService;

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	/**
	 * создаем папку у указанного родителя
	 * @param parentRef ссылка на родителя
	 * @param folder имя папки без слешей и прочей ерунды
	 * @return NodeRef свежесозданной папки
	 */
	private NodeRef createFolder (final NodeRef parentRef, final String folder) {
		ParameterCheck.mandatory ("parentRef", parentRef);
		ParameterCheck.mandatory ("folder", folder);
		NodeRef folderRef = AuthenticationUtil.runAsSystem (new RunAsWork<NodeRef> () {
			@Override
			public NodeRef doWork () throws Exception {
				RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
				return transactionHelper.doInTransaction (new RetryingTransactionCallback<NodeRef> () {
					@Override
					public NodeRef execute () throws Throwable {
						QName assocQName = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, folder);
						Map<QName, Serializable> properties = new HashMap<QName, Serializable> ();
						properties.put (ContentModel.PROP_NAME, folder);
						ChildAssociationRef childAssoc = nodeService.createNode (parentRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties);
						return childAssoc.getChildRef ();
					}
				});
			}
		});
		logger.trace ("NodeRef {} was sucessfully created for {} folder", folderRef, folder);
		return folderRef;
	}

	/**
	 * получаем папку у указанного родителя
	 * @param parentRef ссылка на родителя
	 * @param folder имя папки без слешей и прочей ерунды
	 * @return NodeRef если папка есть, null в противном случае
	 */
	private NodeRef getFolder(final NodeRef parentRef, final String folder) {
		NodeRef folderRef = null;
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(parentRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		if (childAssocs != null) {
			for (ChildAssociationRef childAssoc : childAssocs) {
				NodeRef childRef = childAssoc.getChildRef ();
				if (folder.equals (nodeService.getProperty (childRef, ContentModel.PROP_NAME))) {
					folderRef = childRef;
					logger.trace ("Folder {} already exists, it's noderef is {}", folder, folderRef);
					break;
				}
			}
		}
		return folderRef;
	}

	/**
	 * создание папки "Согласование" внутри объекта lecm-contract:document
	 * @param parentRef
	 * @return
	 */
	private NodeRef getOrCreateApprovalFolder(NodeRef parentRef) {
		NodeRef approvalRef = getFolder(parentRef, "Согласование");
		if (approvalRef == null) {
			approvalRef = createFolder(parentRef, "Согласование");
		}
		return approvalRef;
	}

	/**
	 * Создание папки "Согласование/Параллельное согласование" внутри документа
	 *
	 * @param parentRef
	 * @return
	 */
	private NodeRef getOrCreateParallelApprovalFolder(NodeRef parentRef) {
		NodeRef parallelApprovalRef = getFolder(parentRef, "Параллельное согласование");
		if (parallelApprovalRef == null) {
			parallelApprovalRef = createFolder(parentRef, "Параллельное согласование");
		}
		return parallelApprovalRef;
	}

	private String getContractDocumentVersion(final NodeRef contractDocumentRef) {
		NodeRef contractCategory = null;
		List<NodeRef> contractCategories = documentAttachmentsService.getCategories(contractDocumentRef);
		for (NodeRef categoryRef: contractCategories) {
			String categoryName = (String) nodeService.getProperty(categoryRef, ContentModel.PROP_NAME);
			if ("Договор".equals(categoryName)) {
				contractCategory = categoryRef;
				break;
			}
		}
		if (contractCategory == null) {
			logger.error("Document {} has no Contracts attachment category", contractDocumentRef);
			return "0.0";
		}
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(contractCategory, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		if (childAssocs.isEmpty()) {
			logger.error("Document {} has no Contracts attachment", contractDocumentRef);
			return "0.0";
		} else if (childAssocs.size() > 1) {
			logger.error("Document {} has {} Contracts attachments. I'll use first.", contractDocumentRef, childAssocs.size());
		}

		NodeRef contractAttachmentRef = childAssocs.get(0).getChildRef();
		Collection<Version> attachmentVersions = documentAttachmentsService.getAttachmentVersions(contractAttachmentRef);
		if (attachmentVersions != null && !attachmentVersions.isEmpty()) {
			Version[] versionsArray = attachmentVersions.toArray(new Version[]{});
			return versionsArray[0].getVersionLabel();
		} else {
			return "1.0";
		}
	}

	private NodeRef createApprovalList(final NodeRef parentRef, final NodeRef contractDocumentRef, final NodeRef bpmPackage) {
		String version = getContractDocumentVersion(contractDocumentRef);
		String localName = "Лист согласования версия " + version;
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, localName);
		properties.put(ContentModel.PROP_TITLE, localName);
		properties.put(PROP_APPROVAL_LIST_APPROVE_START, new Date()); //TODO: брать дату начала согласования из регламента
		properties.put(PROP_APPROVAL_LIST_DOCUMENT_VERSION, version);
		QName assocQName = QName.createQName(APPROVAL_LIST_NAMESPACE, localName);
		NodeRef approvalListRef = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQName, TYPE_APPROVAL_LIST, properties).getChildRef();
		//прикрепляем approval list к списку items у документа
		QName qname = QName.createQName(APPROVAL_LIST_NAMESPACE, localName);
		nodeService.addChild(bpmPackage, approvalListRef, ContentModel.ASSOC_CONTAINS, qname);
		return approvalListRef;
	}

	@Override
	public NodeRef createApprovalList(final NodeRef bpmPackage) {
		//через bpmPackage получить ссылку на документ
		NodeRef approvalListRef = null;
//		List<ChildAssociationRef> children = nodeService.getChildAssocs(bpmPackage, TYPE_CONTRACT_FAKE_DOCUMENT, RegexQNamePattern.MATCH_ALL);
		List<ChildAssociationRef> children = nodeService.getChildAssocs(bpmPackage);
		if (children != null) {
			NodeRef contractDocumentRef = null;
			for (ChildAssociationRef assocRef : children) {
				NodeRef candidateRef = assocRef.getChildRef();
				if (TYPE_CONTRACT_DOCUMENT.isMatch(nodeService.getType(candidateRef))) {
					contractDocumentRef = candidateRef;
					break;
				} else if (TYPE_CONTRACT_FAKE_DOCUMENT.isMatch(nodeService.getType(candidateRef))) {
					contractDocumentRef = candidateRef;
					break;
				}
			}
			if (contractDocumentRef != null) {
				//внутри этого документа получить или создать папку "Согласование/Параллельное согласование"
				NodeRef approvalRef = getOrCreateApprovalFolder(contractDocumentRef);
				NodeRef parallelApprovalRef = getOrCreateParallelApprovalFolder(approvalRef);
				//создаем внутри указанной папки объект "Лист согласования"
				approvalListRef = createApprovalList(parallelApprovalRef, contractDocumentRef, bpmPackage);
			} else {
				logger.error("Attention: bpm:package containing lecm-contract:document is empty");
			}
		} else {
			logger.error("Attention: bpm:package containing lecm-contract:document is null");
		}
		return approvalListRef;
	}

	@Override
	public void logDecision(final NodeRef approvalListRef, final JSONObject taskDecision) {
		Date completionDate = null;
		String comment = null;
		String decision = null;

		String username = "";
		NodeRef employeeRef = null;
		NodeRef commentRef = null;

		try {
//			completionDate = taskDecision.get
			completionDate = new Date();
			comment = taskDecision.getString("comment");
			decision = taskDecision.getString("decision");

			username = taskDecision.getString("userName");
			String commentStrRef = taskDecision.getString("commentRef");

			employeeRef = orgstructureService.getEmployeeByPerson(username);
			if (NodeRef.isNodeRef(commentStrRef)) {
				commentRef = new NodeRef(commentStrRef);
			}
		}catch(JSONException ex) {
			logger.error(ex.getMessage(), ex);
		}

		String itemName = "Согласующий " + username;
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, itemName);
		properties.put(PROP_APPROVAL_ITEM_APPROVE_DATE, completionDate);
		properties.put(PROP_APPROVAL_ITEM_COMMENT, comment);
		properties.put(PROP_APPROVAL_ITEM_DECISION, decision);

		QName assocQName = QName.createQName(APPROVAL_LIST_NAMESPACE, itemName);
		NodeRef approvalListItemRef = nodeService.createNode(approvalListRef, ContentModel.ASSOC_CONTAINS, assocQName, TYPE_APPROVAL_ITEM, properties).getChildRef();
		if (employeeRef != null) {
			List<NodeRef> targetRefs = new ArrayList<NodeRef>();
			targetRefs.add(employeeRef);
			nodeService.setAssociations(approvalListItemRef, ASSOC_APPROVAL_ITEM_EMPLOYEE, targetRefs);
		}
		if (commentRef != null) {
			List<NodeRef> targetRefs = new ArrayList<NodeRef>();
			targetRefs.add(commentRef);
			nodeService.setAssociations(approvalListItemRef, ASSOC_APPROVAL_ITEM_COMMENT, targetRefs);
		}
	}

	@Override
	public void logFinalDecision(final NodeRef approvalListRef, final Map<String, String> decisionMap) {
		Map<QName, Serializable> properties = nodeService.getProperties(approvalListRef);
		if (decisionMap.containsValue("REJECTED")) {
			properties.put(PROP_APPROVAL_LIST_DECISION, "REJECTED");
		} else if (decisionMap.containsValue("APPROVED_WITH_REMARK")) {
			properties.put(PROP_APPROVAL_LIST_DECISION, "APPROVED_WITH_REMARK");
		} else if (decisionMap.containsValue("APPROVED")) {
			properties.put(PROP_APPROVAL_LIST_DECISION, "APPROVED");
		}
		properties.put(PROP_APPROVAL_LIST_APPROVE_DATE, new Date());
		nodeService.setProperties(approvalListRef, properties);
	}
}
