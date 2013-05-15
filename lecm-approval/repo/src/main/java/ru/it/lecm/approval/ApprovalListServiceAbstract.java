package ru.it.lecm.approval;

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
import org.alfresco.util.FileNameValidator;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.approval.api.ApprovalListService;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.apache.commons.lang.time.DateUtils;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.wcalendar.IWorkCalendar;

/**
 *
 * @author vlevin
 */
public abstract class ApprovalListServiceAbstract extends BaseBean implements ApprovalListService {

	private final class DocumentInfo {
		private final NodeRef documentRef;
		private final NodeRef initiatorRef;
		private String documentLink;

		DocumentInfo(final NodeRef bpmPackage) {
			documentRef = getDocumentFromBpmPackage(bpmPackage);
			documentLink = "<a href=\"javascript:void(0);\"></a>";
			if (documentRef != null) {
				String presentString = (String)nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING);
				documentLink = wrapperLink(documentRef, presentString, DOCUMENT_LINK_URL);
			} else {
				logger.warn("Can't wrap document as link, because there is no any document in bpm:package.");
			}

			String creator = (String)nodeService.getProperty(documentRef, ContentModel.PROP_CREATOR);
			initiatorRef = orgstructureService.getEmployeeByPerson(creator);
		}

		NodeRef getDocumentRef() {
			return documentRef;
		}

		String getDocumentLink() {
			return documentLink;
		}

		NodeRef getInitiatorRef() {
			return initiatorRef;
		}
	}

	private final static Logger logger = LoggerFactory.getLogger(ApprovalListServiceAbstract.class);
	private final static QName FAKE_PROP_COMINGSOON = QName.createQName(NamespaceService.ALFRESCO_URI, "comingSoonNotified");
	private final static QName FAKE_PROP_OVERDUE = QName.createQName(NamespaceService.ALFRESCO_URI, "overdueNotified");
	private final static String APPROVAL_LIST_NAME = "Лист согласования версия %s";
	private final static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private final static String BUSINESS_ROLE_CONTRACT_CURATOR_ID = "CONTRACT_CURATOR";

	private OrgstructureBean orgstructureService;
	private DocumentAttachmentsService documentAttachmentsService;
    private DocumentMembersService documentMembersService;
	private NotificationsService notificationsService;
	private WorkflowService workflowService;
	private IWorkCalendar workCalendar;
	private DictionaryService dictionaryService;

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

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setWorkCalendar(IWorkCalendar workCalendar) {
		this.workCalendar = workCalendar;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
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
	protected String getContractDocumentVersion(final NodeRef contractDocumentRef, final String documentAttachmentCategoryName) {
		NodeRef contractCategory = null;
		List<NodeRef> contractCategories = documentAttachmentsService.getCategories(contractDocumentRef);
		for (NodeRef categoryRef : contractCategories) {
			String categoryName = (String) nodeService.getProperty(categoryRef, ContentModel.PROP_NAME);
			if (documentAttachmentCategoryName.equals(categoryName)) {
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

	protected NodeRef createApprovalList(final NodeRef parentRef, final NodeRef contractDocumentRef, final NodeRef bpmPackage, final String documentAttachmentCategoryName) {
		String contractDocumentVersion = getContractDocumentVersion(contractDocumentRef, documentAttachmentCategoryName);
		String approvalListVersion = getApprovalListVersion(contractDocumentVersion, parentRef);
		String localName = String.format(APPROVAL_LIST_NAME, approvalListVersion);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, localName);
		properties.put(ContentModel.PROP_TITLE, localName);
		properties.put(PROP_APPROVAL_LIST_APPROVE_START, DateUtils.truncate(new Date(), Calendar.DATE));
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
	@Override
	public NodeRef getDocumentFromBpmPackage(final NodeRef bpmPackage) {
		NodeRef documentRef = null;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(bpmPackage);
		if (children != null) {
			for (ChildAssociationRef assocRef : children) {
				NodeRef candidateRef = assocRef.getChildRef();
				if(dictionaryService.isSubClass(nodeService.getType(candidateRef), DocumentService.TYPE_BASE_DOCUMENT)) {
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
	public NodeRef createApprovalList(final NodeRef bpmPackage, final String documentAttachmentCategoryName) {
		//через bpmPackage получить ссылку на документ
		NodeRef approvalListRef = null;
		NodeRef documentRef = getDocumentFromBpmPackage(bpmPackage);
		if (documentRef != null) {
			//внутри этого документа получить или создать папку "Согласование/<Какое-то> согласование"
			NodeRef approvalRef = getOrCreateRootApprovalFolder(documentRef);
			NodeRef parallelApprovalRef = getOrCreateApprovalFolder(approvalRef);
			//создаем внутри указанной папки объект "Лист согласования"
			approvalListRef = createApprovalList(parallelApprovalRef, documentRef, bpmPackage, documentAttachmentCategoryName);
		} else {
			logger.error("There is no any lecm-contract:document  in bpm:package");
		}
		return approvalListRef;
	}

	@Override
	public void logDecision(final NodeRef approvalListRef, final JSONObject taskDecision) {
		Date startDate = null;
		Date dueDate= null;
		Date completionDate = null;
		String comment = null;
		String decision = null;

		String username = "";
		NodeRef employeeRef = null;
		NodeRef commentRef = null;
		NodeRef documentRef = null;
		String commentFileAttachmentCategoryName = null;
		String documentProjectNumber = null;

		try {
			startDate =  DateUtils.truncate(new Date(taskDecision.getLong("startDate")), Calendar.DATE);
            if (dueDate != null) {
			    dueDate = DateUtils.truncate(new Date(taskDecision.getLong("dueDate")), Calendar.DATE);
            }
			completionDate = DateUtils.truncate(new Date(), Calendar.DATE);
			comment = taskDecision.getString("comment");
			decision = taskDecision.getString("decision");

			username = taskDecision.getString("userName");
			String commentStrRef = taskDecision.getString("commentRef");

			employeeRef = orgstructureService.getEmployeeByPerson(username);
			if (NodeRef.isNodeRef(commentStrRef)) {
				commentRef = new NodeRef(commentStrRef);
			}

			String documentStrRef = taskDecision.getString("documentRef");
			if (NodeRef.isNodeRef(documentStrRef)) {
				documentRef = new NodeRef(documentStrRef);
			}

            if (taskDecision.has("commentFileAttachmentCategoryName")) {
			    commentFileAttachmentCategoryName = taskDecision.getString("commentFileAttachmentCategoryName");
            }

            if (taskDecision.has("documentProjectNumber")) {
                documentProjectNumber = taskDecision.getString("documentProjectNumber");
            }
		} catch (JSONException ex) {
			logger.error(ex.getMessage(), ex);
		}

		String itemName = "Согласующий " + username;
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, itemName);
		properties.put(PROP_APPROVAL_ITEM_START_DATE, startDate);
		properties.put(PROP_APPROVAL_ITEM_DUE_DATE, dueDate);
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
		if (commentRef != null && documentRef != null && commentFileAttachmentCategoryName != null && documentProjectNumber != null) {
			NodeRef attachmentCategoryRef = documentAttachmentsService.getCategory(commentFileAttachmentCategoryName, documentRef);
			if (attachmentCategoryRef != null) {
				StringBuilder commentFileName = new StringBuilder();
				commentFileName.append(nodeService.getProperty(documentRef, QName.createQName(documentProjectNumber, serviceRegistry.getNamespaceService())));
				commentFileName.append(", ");

				commentFileName.append(DATE_FORMAT.format(new Date())).append(" + ");
				commentFileName.append("Согласование сотрудником ");

				if (employeeRef != null) {
					commentFileName.append(nodeService.getProperty(employeeRef, ContentModel.PROP_NAME));
				}

				String commentFileNameStr = FileNameValidator.getValidFileName(commentFileName.toString());

				nodeService.setProperty(commentRef, ContentModel.PROP_NAME, commentFileNameStr);

				QName commentAssocQName = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, commentFileNameStr);
				nodeService.moveNode(commentRef, attachmentCategoryRef, ContentModel.ASSOC_CONTAINS, commentAssocQName);
			}

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
	public void grantReviewerPermissions(final NodeRef employeeRef, final NodeRef bpmPackage) {
		NodeRef documentRef = getDocumentFromBpmPackage(bpmPackage);
		if (documentRef != null) {
			NodeRef member = documentMembersService.addMemberWithoutCheckPermission(documentRef, employeeRef, "LECM_BASIC_PG_Reviewer");
			if(logger.isTraceEnabled()) {
				String employeeName = (String) nodeService.getProperty(employeeRef, ContentModel.PROP_NAME);
				String docName = (String) nodeService.getProperty(documentRef, ContentModel.PROP_NAME);
				logger.trace("Employee {} has been invited to the document {} with LECM_BASIC_PG_Reviewer permission. MemberRef is {}", new Object[]{employeeName, docName, member});
			}
		} else {
			logger.error("There is no any lecm-contract:document in bpm:package. Permissions won't be granted");
		}
	}

	@Override
	public void notifyApprovalStarted(NodeRef employeeRef, Date dueDate, NodeRef bpmPackage) {
		DocumentInfo docInfo = new DocumentInfo(bpmPackage);

		ArrayList<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(employeeRef);

        String dueDatemessage = dueDate == null ? "(нет)" : DATE_FORMAT.format(dueDate);
        String description = String.format("Вам необходимо согласовать документ %s, срок согласования %s", docInfo.getDocumentLink(), dueDatemessage);

		Notification notification = new Notification();
		notification.setAutor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
        notification.setObjectRef(docInfo.getDocumentRef());
		notification.setRecipientEmployeeRefs(recipients);
		notificationsService.sendNotification(notificationChannels, notification);
	}

	@Override
	public void notifyFinalDecision(final String decisionCode, final NodeRef bpmPackage) {
		DocumentInfo docInfo = new DocumentInfo(bpmPackage);

		ArrayList<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(docInfo.getInitiatorRef());

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

		String description = String.format("Принято решение о документе %s: \"%s\"", docInfo.getDocumentLink(), decision);

		Notification notification = new Notification();
		notification.setAutor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
        notification.setObjectRef(docInfo.getDocumentRef());
		notification.setRecipientEmployeeRefs(recipients);
		notificationsService.sendNotification(notificationChannels, notification);
	}

	private void notifyAssigneeDeadline(WorkflowTask userTask, final DocumentInfo docInfo) {
		Map<QName, Serializable> props = userTask.getProperties();
		Date dueDate = (Date)props.get(WorkflowModel.PROP_DUE_DATE);
		String owner = (String)props.get(ContentModel.PROP_OWNER);
		NodeRef employee = orgstructureService.getEmployeeByPerson(owner);
		List<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(employee);
		Date comingSoonDate = workCalendar.getEmployeePreviousWorkingDay(employee, dueDate, -1);
		Date currentDate = new Date();
		int comingSoon = DateUtils.truncatedCompareTo(currentDate, comingSoonDate, Calendar.DATE);
		int overdue = DateUtils.truncatedCompareTo(currentDate, dueDate, Calendar.DATE);
		Map<QName, Serializable> fakeProps = new HashMap<QName, Serializable>();
		if (!props.containsKey(FAKE_PROP_COMINGSOON) && comingSoon >= 0) {
			fakeProps.put(FAKE_PROP_COMINGSOON, "");
			Notification notification = new Notification();
			notification.setAutor(AuthenticationUtil.getSystemUserName());
			String description = String.format("Напоминание: Вам необходимо согласовать проект документа %s, срок согласования %s", docInfo.getDocumentLink(), DATE_FORMAT.format(dueDate));
			notification.setDescription(description);
			notification.setObjectRef(docInfo.getDocumentRef());
			notification.setRecipientEmployeeRefs(recipients);
			notificationsService.sendNotification(notificationChannels, notification);
		}
		if (!props.containsKey(FAKE_PROP_OVERDUE) && overdue > 0) {
			fakeProps.put(FAKE_PROP_OVERDUE, "");
			Notification notification = new Notification();
			notification.setAutor(AuthenticationUtil.getSystemUserName());
			String description = String.format("Внимание: Вы не согласовали документ %s, срок согласования %s", docInfo.getDocumentLink(), DATE_FORMAT.format(dueDate));
			notification.setDescription(description);
			notification.setObjectRef(docInfo.getDocumentRef());
			notification.setRecipientEmployeeRefs(recipients);
			notificationsService.sendNotification(notificationChannels, notification);
		}
		if (!fakeProps.isEmpty()) {
			workflowService.updateTask(userTask.getId(), fakeProps, null, null);
		}
	}

	@Override
	public void notifyAssigneesDeadline(final String processInstanceId, final NodeRef bpmPackage) {
		try {
			DocumentInfo docInfo = new DocumentInfo(bpmPackage);

			WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
			taskQuery.setProcessId(processInstanceId);
			taskQuery.setTaskState(WorkflowTaskState.IN_PROGRESS);
			List<WorkflowTask> tasks = workflowService.queryTasks(taskQuery);
			for (WorkflowTask task : tasks) {
				logger.trace(task.toString());
				notifyAssigneeDeadline(task, docInfo);
			}
		} catch (Exception ex) {
			logger.error("Internal error while notifying Assignees", ex);
		}
	}

	private String getIncompleteAssignees(final String processInstanceId) {
		WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
		taskQuery.setProcessId(processInstanceId);
		taskQuery.setTaskState(WorkflowTaskState.IN_PROGRESS);
		List<WorkflowTask> tasks = workflowService.queryTasks(taskQuery);
		StringBuilder builder = new StringBuilder();
		for (WorkflowTask task : tasks) {
			String owner = (String)task.getProperties().get(ContentModel.PROP_OWNER);
			NodeRef employee = orgstructureService.getEmployeeByPerson(owner);
			String name = (String)nodeService.getProperty(employee, ContentModel.PROP_NAME);
			builder.append(name).append(", ");
		}
		int length = builder.length();
		if (length > 0) {
			builder.delete(length-2, length); //удалить последний ", "
		}
		return builder.toString();
	}

    @Override
    public List<NodeRef> getCurators() {
		List<NodeRef> curators = orgstructureService.getEmployeesByBusinessRole(BUSINESS_ROLE_CONTRACT_CURATOR_ID);
		return curators != null ? curators : new ArrayList<NodeRef>();
	}

	@Override
	public void notifyInitiatorDeadline(final String processInstanceId, final NodeRef bpmPackage, final VariableScope variableScope) {
		try {
			DocumentInfo docInfo = new DocumentInfo(bpmPackage);
			Set<NodeRef> recipients = new HashSet<NodeRef>();
			recipients.add(docInfo.getInitiatorRef());
			WorkflowInstance workflowInstance = workflowService.getWorkflowById(processInstanceId);
			Date dueDate = workflowInstance.getDueDate();
			Date comingSoonDate = workCalendar.getEmployeePreviousWorkingDay(docInfo.getInitiatorRef(), dueDate, -1);
			Date currentDate = new Date();
			int comingSoon = DateUtils.truncatedCompareTo(currentDate, comingSoonDate, Calendar.DATE);
			int overdue = DateUtils.truncatedCompareTo(currentDate, dueDate, Calendar.DATE);
			if(!variableScope.hasVariable("initiatorComingSoon") && comingSoon >= 0) {
				variableScope.setVariable("initiatorComingSoon", "");
				Notification notification = new Notification();
				notification.setAutor(AuthenticationUtil.getSystemUserName());
				String description = String.format("Напоминание: Вы направили на согласование проект документа %s, срок согласования %s", docInfo.getDocumentLink(), DATE_FORMAT.format(dueDate));
				notification.setDescription(description);
				notification.setObjectRef(docInfo.getDocumentRef());
				notification.setRecipientEmployeeRefs(new ArrayList<NodeRef>(recipients));
				notificationsService.sendNotification(notificationChannels, notification);
			}
			if(!variableScope.hasVariable("initiatorOverdue") && overdue > 0) {
				variableScope.setVariable("initiatorOverdue", "");
				Notification notification = new Notification();
				notification.setAutor(AuthenticationUtil.getSystemUserName());
				String people = getIncompleteAssignees(processInstanceId);
				String description = String.format("Внимание: проект документа %s не согласован в срок %s. Следующие сотрудники не приняли решение: %s", docInfo.getDocumentLink(), DATE_FORMAT.format(dueDate), people);
				//получить список кураторов и добавить его в recipients
				recipients.addAll(getCurators());
				notification.setDescription(description);
				notification.setObjectRef(docInfo.getDocumentRef());
				notification.setRecipientEmployeeRefs(new ArrayList<NodeRef>(recipients));
				notificationsService.sendNotification(notificationChannels, notification);
			}
		} catch(Exception ex) {
			logger.error("Internal error while notifying initiator and curators", ex);
		}
	}

    /**
     * return boss login or self employee login if boss is null
     * @param executorPersonName
     * @return
     */
    @Override
    public String getExecutorBoss(String executorPersonName) {
        NodeRef executorEmployee = orgstructureService.getEmployeeByPerson(executorPersonName);
        NodeRef boss = orgstructureService.findEmployeeBoss(executorEmployee);
        return boss == null ? executorPersonName : orgstructureService.getEmployeeLogin(boss);
    }
}

