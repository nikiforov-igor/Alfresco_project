package ru.it.lecm.approval;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.approval.api.ApprovalListService;
import static ru.it.lecm.approval.api.ApprovalListService.PROP_APPROVAL_LIST_APPROVE_DATE;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vlevin
 */
public abstract class ApprovalListServiceAbstract extends BaseBean implements ApprovalListService {

	private final static Logger logger = LoggerFactory.getLogger(ApprovalListServiceAbstract.class);
	private final static String CONTRACT_NAMESPACE = "http://www.it.ru/logicECM/contract/1.0";
	private final static String CONTRACT_FAKE_NAMESPACE = "http://www.it.ru/logicECM/contract/fake/1.0";
	private final static String CONTRACTORS_NAMESPACE = "http://www.it.ru/lecm/contractors/model/contractor/1.0";
	private final static QName TYPE_CONTRACT_DOCUMENT = QName.createQName(CONTRACT_NAMESPACE, "document");
	private final static QName TYPE_CONTRACT_FAKE_DOCUMENT = QName.createQName(CONTRACT_FAKE_NAMESPACE, "document");
	private final static QName TYPE_CONTRACTOR = QName.createQName(CONTRACTORS_NAMESPACE, "contractor-type");
	private final static QName ASSOC_CONTRACT_PARTNER = QName.createQName(CONTRACT_NAMESPACE, "partner-assoc");
	private final static QName PROP_CONTRACTOR_FULLNAME = QName.createQName(CONTRACTORS_NAMESPACE, "fullname");
	private final static QName PROP_CONTRACTOR_SHORTNAME = QName.createQName(CONTRACTORS_NAMESPACE, "shortname");
	private final static String APPROVAL_LIST_NAME = "Лист согласования версия %s";
	private final static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	private OrgstructureBean orgstructureService;
	private DocumentAttachmentsService documentAttachmentsService;
    private DocumentMembersService documentMembersService;
	private NotificationsService notificationsService;

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

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	/**
	 * создаем папку у указанного родителя
	 *
	 * @param parentRef ссылка на родителя
	 * @param folder имя папки без слешей и прочей ерунды
	 * @return NodeRef свежесозданной папки
	 */
	protected NodeRef createFolder(final NodeRef parentRef, final String folder) {
		ParameterCheck.mandatory("parentRef", parentRef);
		ParameterCheck.mandatory("folder", folder);
		NodeRef folderRef = AuthenticationUtil.runAsSystem(new RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
				return transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, folder);
						Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
						properties.put(ContentModel.PROP_NAME, folder);
						ChildAssociationRef childAssoc = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties);
						return childAssoc.getChildRef();
					}
				});
			}
		});
		logger.trace("NodeRef {} was sucessfully created for {} folder", folderRef, folder);
		return folderRef;
	}

	/**
	 * получаем папку у указанного родителя
	 *
	 * @param parentRef ссылка на родителя
	 * @param folder имя папки без слешей и прочей ерунды
	 * @return NodeRef если папка есть, null в противном случае
	 */
	protected NodeRef getFolder(final NodeRef parentRef, final String folder) {
		NodeRef folderRef = null;
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(parentRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		if (childAssocs != null) {
			for (ChildAssociationRef childAssoc : childAssocs) {
				NodeRef childRef = childAssoc.getChildRef();
				if (folder.equals(nodeService.getProperty(childRef, ContentModel.PROP_NAME))) {
					folderRef = childRef;
					logger.trace("Folder {} already exists, it's noderef is {}", folder, folderRef);
					break;
				}
			}
		}
		return folderRef;
	}

	/**
	 * создание папки "Согласование" внутри объекта lecm-contract:document
	 *
	 * @param parentRef
	 * @return
	 */
	protected NodeRef getOrCreateRootApprovalFolder(NodeRef parentRef) {
		NodeRef approvalRef = getFolder(parentRef, "Согласование");
		if (approvalRef == null) {
			approvalRef = createFolder(parentRef, "Согласование");
		}
		return approvalRef;
	}

	/**
	 * Этот метод должен возвращать ссылку на папку, в которорую надо сложить
	 * лист согласования данного конкретного регламента согласования
	 * (последовательное, параллельное, еще какое-то)
	 *
	 * @param parentRef ссылка на папку "Согласование" в рамках вложения
	 * документа.
	 * @return ссылка на папку, куда можно складывать лист согласования
	 */
	protected abstract NodeRef getOrCreateApprovalFolder(NodeRef parentRef);

	/**
	 * Получить из документа версию вложения типа "Договор"
	 *
	 * @param contractDocumentRef ссылка на документ "Договор"
	 * @return последняя версия вложенного файла из категории "Договор"
	 */
	protected String getContractDocumentVersion(final NodeRef contractDocumentRef) {
		NodeRef contractCategory = null;
		List<NodeRef> contractCategories = documentAttachmentsService.getCategories(contractDocumentRef);
		for (NodeRef categoryRef : contractCategories) {
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

	/**
	 * Получить версию листа согласования. ПРоверяется наличие листа
	 * согласования с указанной версией в указанной папке. Если лист с такой
	 * версией уже есть, то версия инкреминируется. Пример: если есть лист
	 * согласования с версией 1.5, то будет создан новый с версией 1.5.1. После
	 * этого будет создан лист с версией 1.5.2 и т. д.
	 *
	 * @param version начальная версия листа согласования.
	 * @param parentRef каталог с листами согласования
	 * @return версия листа согласования, которую можно безбоязненно
	 * использовать для нового листа
	 */
	protected String getApprovalListVersion(String version, NodeRef parentRef) {
		String result;
		String approvalListName = String.format(APPROVAL_LIST_NAME, version);
		NodeRef approvalListNode = nodeService.getChildByName(parentRef, ContentModel.ASSOC_CONTAINS, approvalListName);
		if (approvalListNode == null) {
			return version;
		} else {
			String[] splittedVersion = version.split("\\.");
			if (splittedVersion.length == 2) {
				// В версии две цифры (1.5)
				result = version + ".1";
			} else if (splittedVersion.length == 3) {
				// В версии три цифры (1.5.3)
				String minorVersionStr = splittedVersion[2];
				int minorVersionInt = Integer.parseInt(minorVersionStr);
				minorVersionInt++;
				splittedVersion[2] = String.valueOf(minorVersionInt);
				result = StringUtils.join(splittedVersion, ".");
			} else {
				// Мы не должны сюда попасть
				logger.error("Error in version string: {}", version);
				return null;
			}
			return getApprovalListVersion(result, parentRef);
		}
	}

	protected NodeRef createApprovalList(final NodeRef parentRef, final NodeRef contractDocumentRef, final NodeRef bpmPackage) {
		String contractDocumentVersion = getContractDocumentVersion(contractDocumentRef);
		String approvalListVersion = getApprovalListVersion(contractDocumentVersion, parentRef);
		String localName = String.format(APPROVAL_LIST_NAME, approvalListVersion);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, localName);
		properties.put(ContentModel.PROP_TITLE, localName);
		properties.put(PROP_APPROVAL_LIST_APPROVE_START, new Date()); //TODO: брать дату начала согласования из регламента
		properties.put(PROP_APPROVAL_LIST_DOCUMENT_VERSION, contractDocumentVersion);
		QName assocQName = QName.createQName(APPROVAL_LIST_NAMESPACE, localName);
		NodeRef approvalListRef = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQName, TYPE_APPROVAL_LIST, properties).getChildRef();
		//прикрепляем approval list к списку items у документа
		QName qname = QName.createQName(APPROVAL_LIST_NAMESPACE, localName);
		nodeService.addChild(bpmPackage, approvalListRef, ContentModel.ASSOC_CONTAINS, qname);
		return approvalListRef;
	}

	/**
	 * получение ссылки на документ через переменную регламента bpm_package
	 * @param bpmPackage
	 * @return
	 */
	protected NodeRef getDocumentFromBpmPackage(final NodeRef bpmPackage) {
		NodeRef documentRef = null;
//		List<ChildAssociationRef> children = nodeService.getChildAssocs(bpmPackage, TYPE_CONTRACT_FAKE_DOCUMENT, RegexQNamePattern.MATCH_ALL);
		List<ChildAssociationRef> children = nodeService.getChildAssocs(bpmPackage);
		if (children != null) {
			for (ChildAssociationRef assocRef : children) {
				NodeRef candidateRef = assocRef.getChildRef();
				if (TYPE_CONTRACT_DOCUMENT.isMatch(nodeService.getType(candidateRef))) {
					documentRef = candidateRef;
					break;
				} else if (TYPE_CONTRACT_FAKE_DOCUMENT.isMatch(nodeService.getType(candidateRef))) {
					documentRef = candidateRef;
					break;
				}
			}
		} else {
			logger.error("List of bpm:package children is null");
		}
		return documentRef;
	}

	@Override
	public NodeRef createApprovalList(final NodeRef bpmPackage) {
		//через bpmPackage получить ссылку на документ
		NodeRef approvalListRef = null;
		NodeRef documentRef = getDocumentFromBpmPackage(bpmPackage);
		if (documentRef != null) {
			//внутри этого документа получить или создать папку "Согласование/<Какое-то> согласование"
			NodeRef approvalRef = getOrCreateRootApprovalFolder(documentRef);
			NodeRef parallelApprovalRef = getOrCreateApprovalFolder(approvalRef);
			//создаем внутри указанной папки объект "Лист согласования"
			approvalListRef = createApprovalList(parallelApprovalRef, documentRef, bpmPackage);
		} else {
			logger.error("There is no any lecm-contract:document  in bpm:package");
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
		} catch (JSONException ex) {
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
	public void logFinalDecision(final NodeRef approvalListRef, final String finalDecision) {
		Map<QName, Serializable> properties = nodeService.getProperties(approvalListRef);
		properties.put(PROP_APPROVAL_LIST_DECISION, finalDecision);
		properties.put(PROP_APPROVAL_LIST_APPROVE_DATE, new Date());
		nodeService.setProperties(approvalListRef, properties);
	}

	@Override
	public void grantReviewerPermissions(List<NodeRef> employees, NodeRef bpmPackage) {
		NodeRef documentRef = getDocumentFromBpmPackage(bpmPackage);
		if (documentRef != null) {
			Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
			properties.put(DocumentMembersService.PROP_MEMBER_GROUP, "LECM_BASIC_PG_Reviewer");
			for (NodeRef employee : employees) {
				NodeRef member = documentMembersService.addMember(documentRef, employee, properties);
				if(logger.isTraceEnabled()) {
					String employeeName = (String) nodeService.getProperty(employee, ContentModel.PROP_NAME);
					String docName = (String) nodeService.getProperty(documentRef, ContentModel.PROP_NAME);
					logger.trace("Employee {} has been invited to the document {} with LECM_BASIC_PG_Reviewer permission. MemberRef is {}", new Object[]{employeeName, docName, member});
				}
			}
		} else {
			logger.error("There is no any lecm-contract:document in bpm:package. Permissions won't be granted");
		}
	}

	private String getDocumentPartnerName(final NodeRef documentRef) {
		String partner = "<контрагент отсутствует>";
		NodeRef partnerRef = findNodeByAssociationRef(documentRef, ASSOC_CONTRACT_PARTNER, TYPE_CONTRACTOR, ASSOCIATION_TYPE.TARGET);
		if (partnerRef != null) {
			partner = (String)nodeService.getProperty(partnerRef, PROP_CONTRACTOR_FULLNAME);
		} else {
			logger.warn("The is no partner for document {}", documentRef);
		}
		return partner;
	}

	private NodeRef getDocumentInitiator(final NodeRef documentRef) {
		String creator = (String)nodeService.getProperty(documentRef, ContentModel.PROP_CREATOR);
		return orgstructureService.getEmployeeByPerson(creator);
	}

	@Override
	public void notifyApprovalStarted(NodeRef employeeRef, Date dueDate, NodeRef bpmPackage) {
		NodeRef documentRef = getDocumentFromBpmPackage(bpmPackage);
		String documentLink = "<a href=\"javascript:void(0);\">документ</a>";
		if (documentRef != null) {
			documentLink = wrapperLink(documentRef, "документ", DOCUMENT_LINK_URL);
		} else {
			logger.warn("Can't wrap document as link, because there is no any document in bpm:package.");
		}

		String partner = "<контрагент отсутствует>";
		if (documentRef != null) {
			partner = getDocumentPartnerName(documentRef);
		} else {
			logger.warn("Can't get partner for document, because there is no any document in bpm:package");
		}

		String date = DATE_FORMAT.format(dueDate);

		ArrayList<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(employeeRef);

		String description = String.format("Вам необходимо согласовать %s по договору с %s, срок согласования: %s", documentLink, partner, date);

		Notification notification = new Notification();
		notification.setAutor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
        notification.setObjectRef(documentRef);
		notification.setRecipientEmployeeRefs(recipients);
		notificationsService.sendNotification(notificationChannels, notification);
	}

	@Override
	public void notifyFinalDecision(final String decisionCode, final NodeRef bpmPackage) {
		NodeRef documentRef = getDocumentFromBpmPackage(bpmPackage);
		String documentLink = "<a href=\"javascript:void(0);\">документе</a>";
		if (documentRef != null) {
			documentLink = wrapperLink(documentRef, "документе", DOCUMENT_LINK_URL);
		} else {
			logger.warn("Can't wrap document as link, because there is no any document in bpm:package.");
		}

		String partner = "<контрагент отсутствует>";
		if (documentRef != null) {
			partner = getDocumentPartnerName(documentRef);
		} else {
			logger.warn("Can't get partner for document, because there is no any document in bpm:package");
		}

		NodeRef initiator = null;
		if (documentRef != null) {
			initiator = getDocumentInitiator(documentRef);
		}

		ArrayList<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(initiator);

		String decision;
		if("APPROVED".equals(decisionCode)) {
			decision = "согласовано";
		} else if ("APPROVED_WITH_REMARK".equals(decisionCode)) {
			decision = "согласовано с замечаниями";
		} else if ("REJECTED".equals(decisionCode)) {
			decision = "отклонено";
		} else if ("APPROVED_FORCE".equals(decisionCode)) {
			decision = "принудительно завершено";
		} else if ("REJECTED_FORCE".equals(decisionCode)) {
			decision = "отозвано с согласования";
		} else if ("NO_DECISION".equals(decisionCode)) {
			decision = "решение не принято";
		} else  {
			decision = "";
		}

		//«Принято решение о документе,  по договору с <Наименование контрагента>:  <Результат согласования по документу>»
		String description = String.format("Принято решение о %s, по договору с %s: \"%s\"", documentLink, partner, decision);

		Notification notification = new Notification();
		notification.setAutor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
        notification.setObjectRef(documentRef);
		notification.setRecipientEmployeeRefs(recipients);
		notificationsService.sendNotification(notificationChannels, notification);
	}
}

