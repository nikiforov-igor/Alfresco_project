package ru.it.lecm.ord.extensions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentEventService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.workflow.api.WorkflowResultModel;
import ru.it.lecm.workflow.AssigneesList;
import ru.it.lecm.workflow.AssigneesListItem;
import ru.it.lecm.workflow.api.WorkflowAssigneesListService;

/**
 *
 * @author snovikov
 */
public class ORDStatemachineJavascriptExtension extends BaseWebScript {

	private final static Logger logger = LoggerFactory.getLogger(ORDStatemachineJavascriptExtension.class);
	private NodeService nodeService;
	private DocumentService documentService;
	private OrgstructureBean orgstructureService;
	private EDSGlobalSettingsService edsGlobalSettingsService;
	private DocumentConnectionService documentConnectionService;
	private DictionaryBean lecmDictionaryService;
	private WorkflowAssigneesListService workflowAssigneesListService;
	private BusinessJournalService businessJournalService;
	private DocumentEventService documentEventService;

	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentService(final DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setOrgstructureService(final OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setEdsGlobalSettingsService(EDSGlobalSettingsService edsGlobalSettingsService) {
		this.edsGlobalSettingsService = edsGlobalSettingsService;
	}

	public void setWorkflowAssigneesListService(WorkflowAssigneesListService workflowAssigneesListService) {
		this.workflowAssigneesListService = workflowAssigneesListService;
	}

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	public void setLecmDictionaryService(DictionaryBean lecmDictionaryService) {
		this.lecmDictionaryService = lecmDictionaryService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setDocumentEventService(DocumentEventService documentEventService) {
		this.documentEventService = documentEventService;
	}

	private String getOrdURL(final ScriptNode ordRef) {
		NodeRef ordDocumentRef = ordRef.getNodeRef();
		String presentString = (String) nodeService.getProperty(ordDocumentRef, DocumentService.PROP_PRESENT_STRING);
		return wrapperLink(ordDocumentRef.toString(), presentString, BaseBean.DOCUMENT_LINK_URL);
	}

	/**
	 * подговить уведомление Автору ОРД о доработке будет подготовлено
	 * уведомление с сообщением "Проект документа &lt;Вид документа&gt; №
	 * &lt;Номер документа&gt; направлен Вам на доработку"
	 *
	 * @param ordRef ссылка на ОРД документ из машины состояний
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutRework(final ScriptNode ordRef) {
		String ordURL = getOrdURL(ordRef);
		String description = String.format("Проект документа %s направлен Вам на доработку", ordURL);
		return prepareNotificationAboutRework(ordRef, description);
	}

	/**
	 * подговить уведомление Автору ОРД о доработке
	 *
	 * @param ordRef ссылка на ОРД документ из машины состояний
	 * @param description текст сообщения, которое необходимо отправить
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutRework(final ScriptNode ordRef, final String description) {
		NodeRef documentAuthorRef = documentService.getDocumentAuthor(ordRef.getNodeRef());

		ArrayList<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(documentAuthorRef);

		Notification notification = new Notification();
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
		notification.setObjectRef(ordRef.getNodeRef());
		notification.setRecipientEmployeeRefs(recipients);
		return notification;
	}

	/**
	 * подготовить список регистраторов для последующей раздачи им прав
	 *
	 * @param businessRoleId идентификатор бизнес-роли, которая содержит
	 * потенциальных регистраторов
	 * @param ordRef ссылка на ОРД документ из машины состояний
	 * @return объект Scriptable пригодный для работы из яваскрипта машины
	 * состояний
	 */
	public Scriptable getRegistrars(final String businessRoleId, final ScriptNode ordRef) {
		List<NodeRef> registrars;
		//получаем текущего пользователя
		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		// централизованная ли регистрация
		Boolean registrationCenralized = edsGlobalSettingsService.isRegistrationCenralized();

		if (registrationCenralized) {
			registrars = orgstructureService.getEmployeesByBusinessRole(businessRoleId);
		} else {
			registrars = new ArrayList<NodeRef>();
			//получаем основную должностную позицию
			NodeRef primaryStaff = orgstructureService.getEmployeePrimaryStaff(currentEmployee);
			if (primaryStaff != null) {
				NodeRef unit = orgstructureService.getUnitByStaff(primaryStaff);
				registrars.addAll(edsGlobalSettingsService.getPotentialWorkers(businessRoleId, unit));
			}
		}
		return createScriptable(registrars);
	}

	/**
	 * подготовить уведомление регистраторам о том, что надо зарегистрировать
	 * ОРД документ будет подготовлено уведомление с сообщением "Документ
	 * &lt;Вид документа&gt; № &lt;Номер документа&gt; поступил Вам на
	 * регистрацию"
	 *
	 * @param ordRef ссылка на ОРД документ из машины состояний
	 * @param registrars js-массив регистраторов который мы получили из скрипта
	 * машины состояний
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutRegistration(final ScriptNode ordRef, final Scriptable registrars) {
		String ordURL = getOrdURL(ordRef);
		String description = String.format("Документ %s поступил Вам на регистрацию", ordURL);
		return prepareNotificationAboutRegistration(ordRef, registrars, description);
	}

	/**
	 * подготовить уведомление регистраторам о том, что надо зарегистрировать
	 * ОРД документ
	 *
	 * @param Ref ссылка на ОРД документ из машины состояний
	 * @param registrars js-массив регистраторов который мы получили из скрипта
	 * машины состояний
	 * @param description текст сообщения, которое необходимо отправить
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutRegistration(final ScriptNode ordRef, final Scriptable registrars, final String description) {
		Object[] elements = Context.getCurrentContext().getElements(registrars);
		ArrayList<NodeRef> registrarRefs = new ArrayList<NodeRef>();
		for (Object element : elements) {
			if (element instanceof ScriptNode) {
				ScriptNode registrar = (ScriptNode) element;
				registrarRefs.add(registrar.getNodeRef());
			} else if (element instanceof NativeJavaObject) {
				NativeJavaObject object = (NativeJavaObject) element;
				ScriptNode registrar = (ScriptNode) object.unwrap();
				registrarRefs.add(registrar.getNodeRef());
			} else {
				logger.warn("{} {} is not a ScriptNode in registrars array!", element.getClass().getName(), element);
			}
		}

		Notification notification = new Notification();
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
		notification.setObjectRef(ordRef.getNodeRef());
		notification.setRecipientEmployeeRefs(registrarRefs);
		return notification;
	}

	/**
	 * Подготовить уведомление сотруднику о том, что он выбран в качестве
	 * Контролера ОРД документа
	 *
	 * @param ordRef ссылка на ОРД документ из скрипта машины состояний
	 * @param controller ссылка на контролера из скрипта машины состояний
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationToController(final ScriptNode ord, final ScriptNode controller) {
		String ordURL = getOrdURL(ord);
		Date execDate = (Date) nodeService.getProperty(ord.getNodeRef(), EDSDocumentService.PROP_EXECUTION_DATE);
		String formatExecDate = "не указан";
		if (null != execDate) {
			formatExecDate = new SimpleDateFormat("dd.MM.yyyy").format(execDate);
		}
		String description = String.format("Вы назначены Контролером по документу %s. Срок исполнения: %s", ordURL, formatExecDate);

		List<NodeRef> recipients = Arrays.asList(controller.getNodeRef());

		Notification notification = new Notification();
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
		notification.setObjectRef(ord.getNodeRef());
		notification.setRecipientEmployeeRefs(recipients);
		return notification;
	}

	 /**
	 * Сформировать поручения по пунктам ОРД
	 *
	 * @param ordSNode ссылка на ОРД документ из скрипта машины состояний
	 */
	public void formErrands(ScriptNode ordSNode) {
		NodeRef ord = ordSNode.getNodeRef();
		//найдем таблицу с пунктами
		List<AssociationRef> tableAssocs = nodeService.getTargetAssocs(ord, ORDModel.ASSOC_ORD_TABLE_ITEMS);
		if (tableAssocs.size() > 0) {
			NodeRef table = tableAssocs.get(0).getTargetRef();
			Set<QName> pointType = new HashSet<QName>(Arrays.asList(ORDModel.TYPE_ORD_TABLE_ITEM));
			List<ChildAssociationRef> pointAssocs = nodeService.getChildAssocs(table, pointType);
			for (ChildAssociationRef pointAssoc : pointAssocs) {
				NodeRef point = pointAssoc.getChildRef();

				//свойства поручения
				Map<String, String> properties = new HashMap<String, String>();
				//заголовок
				Integer pointNumber = (Integer) nodeService.getProperty(point, DocumentTableService.PROP_INDEX_TABLE_ROW);
				List<AssociationRef> ordDocTypeAssocs = nodeService.getTargetAssocs(ord, EDSDocumentService.ASSOC_DOCUMENT_TYPE);
				String ordDocType = "";
				if (ordDocTypeAssocs.size() > 0) {
					NodeRef ordDocTypeRef = ordDocTypeAssocs.get(0).getTargetRef();
					ordDocType = (String) nodeService.getProperty(ordDocTypeRef, ContentModel.PROP_NAME);
				}
				String ordNumber = (String) nodeService.getProperty(ord, DocumentService.PROP_REG_DATA_DOC_NUMBER);
				Date ordRegDate = (Date) nodeService.getProperty(ord, DocumentService.PROP_REG_DATA_DOC_DATE);
				String ordRegDateStr = new SimpleDateFormat("dd-MM-yyyy").format(ordRegDate);

				StringBuilder errandTtitle = new StringBuilder();
				errandTtitle.append("Пункт № ").append(pointNumber.toString()).append(" документа ").append(ordDocType);
				errandTtitle.append(" №").append(ordNumber).append(" от ").append(ordRegDateStr);
				properties.put("lecm-errands:title", errandTtitle.toString());
				//содержание
				String content = (String) nodeService.getProperty(point, ORDModel.PROP_ORD_TABLE_ITEM_CONTENT);
				properties.put("lecm-errands:content", content);
				//важность
				properties.put("lecm-errands:is-important", "true");

				//ассоциации поручения
				Map<String, String> associations = new HashMap<String, String>();
				//инициатор поручения
				List<AssociationRef> controllerAssocs = nodeService.getTargetAssocs(ord, ORDModel.ASSOC_ORD_CONTROLLER);
				if (controllerAssocs.size() > 0) {
					NodeRef controller = controllerAssocs.get(0).getTargetRef();
					associations.put("lecm-errands:initiator-assoc", controller.toString());
				}
				//исполнитель
				List<AssociationRef> pointExecutorAssocs = nodeService.getTargetAssocs(point, ORDModel.ASSOC_ORD_TABLE_EXECUTOR);
				if (pointExecutorAssocs.size() > 0) {
					NodeRef executor = pointExecutorAssocs.get(0).getTargetRef();
					associations.put("lecm-errands:executor-assoc", executor.toString());
				}
				//тематика поручения
				List<AssociationRef> subjectAssocs = nodeService.getTargetAssocs(ord, DocumentService.ASSOC_SUBJECT);
				if (subjectAssocs.size() > 0) {
					NodeRef subject = subjectAssocs.get(0).getTargetRef();
					associations.put("lecm-document:subject-assoc", subject.toString());
				}

				NodeRef errand = documentService.createDocument("lecm-errands:document", properties, associations);

				// срок поручения
				Date limitationDate = (Date) nodeService.getProperty(point, ORDModel.PROP_ORD_TABLE_EXECUTION_DATE);
				nodeService.setProperty(errand, ErrandsService.PROP_ERRANDS_LIMITATION_DATE, limitationDate);

				//дата начала
				NodeRef singFolder = nodeService.getChildByName(ord, ContentModel.ASSOC_CONTAINS, "Подписание");
				if (null != singFolder) {
					List<ChildAssociationRef> signListAssocs = nodeService.getChildAssocs(singFolder);
					if (signListAssocs.size() > 0) {
						NodeRef signList = signListAssocs.get(0).getChildRef();
						Date signCompleteDate = (Date) nodeService.getProperty(signList, WorkflowResultModel.PROP_WORKFLOW_RESULT_LIST_COMPLETE_DATE);
						nodeService.setProperty(errand, ErrandsService.PROP_ERRANDS_START_DATE, signCompleteDate);
					}
				}

				// установим системную связь между ОРД и созданным поручением
				documentConnectionService.createConnection(ord, errand, DocumentConnectionService.DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE, true, true);
				// создадим ассоциацию пункта с поручением
				nodeService.createAssociation(point, errand, ORDModel.ASSOC_ORD_TABLE_ERRAND);
				// переведем пункт в статус "на исполнениии"
				NodeRef pointPerformanceStatus = lecmDictionaryService.getDictionaryValueByParam(ORDModel.ORD_POINT_DICTIONARY_NAME, ContentModel.PROP_NAME, ORDModel.ORD_POINT_PERFORMANCE_STATUS);
				List<NodeRef> targetStatus = Arrays.asList(pointPerformanceStatus);
				nodeService.setAssociations(point, ORDModel.ASSOC_ORD_TABLE_ITEM_STATUS, targetStatus);
			}
		}
	}


	/**
	 * Обновление ассоциаций с подписантами на основании данных в результирующем списке подписантов
	 *
	 * @param documentNode документ
	 * @param signersList список подписантов
	 */
	public void updateDocumentToSignersAssocs(final ScriptNode documentNode, final ActivitiScriptNodeList signersList) {
		NodeRef documentRef = documentNode.getNodeRef();
		List<NodeRef> signersRefs = signersList.getNodeReferences();

		updateDocumentToSignersAssocs(documentRef, signersRefs);
	}

	/**
	 * Обновление ассоциаций с подписантами на основании данных в результирующем списке подписантов
	 *
	 * @param documentNode документ
	 * @param listNode нода списка подписантов
	 */
	public void updateDocumentToSignersAssocs(final ScriptNode documentNode, final ScriptNode listNode) {
		NodeRef documentRef = documentNode.getNodeRef();
		NodeRef listRef = listNode.getNodeRef();
		List<NodeRef> itemsList = new ArrayList<NodeRef>();
 		List<ChildAssociationRef> itemsAssocs = nodeService.getChildAssocs(listRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		for (ChildAssociationRef itemAssoc : itemsAssocs) {
			NodeRef itemRef = itemAssoc.getChildRef();
			if (itemRef != null) {
				List<AssociationRef> employeesAssocs = nodeService.getTargetAssocs(itemRef, WorkflowResultModel.ASSOC_WORKFLOW_RESULT_ITEM_EMPLOYEE);
				for (AssociationRef employeeAssoc : employeesAssocs) {
					NodeRef employeeRef = employeeAssoc.getTargetRef();
					itemsList.add(employeeRef);
				}
			}
		}
		updateDocumentToSignersAssocs(documentRef, itemsList);
	}

	/**
	 * Обновление ассоциаций с подписантами на основании данных в результирующем списке подписантов
	 *
	 * @param documentRef документ
	 * @param signersRefs список NodeRef-ов подписантов 
	 */
	public void updateDocumentToSignersAssocs(final NodeRef documentRef, final List<NodeRef> signersRefs) {
		List<NodeRef> documentSignersRefs = getDocumentToSignersAssocs(documentRef);
		Set<NodeRef> signersForDeleteRefs = new HashSet<NodeRef>(documentSignersRefs);

		for (NodeRef signerRef : signersRefs) {
			if (!signersForDeleteRefs.remove(signerRef)) {
				nodeService.createAssociation(documentRef, signerRef, ORDModel.ASSOC_ORD_SIGNERS);
			}
		}
		for (NodeRef signerRef : signersForDeleteRefs) {
			nodeService.removeAssociation(documentRef, signerRef, ORDModel.ASSOC_ORD_SIGNERS);
		}
	}

	private List<NodeRef> getDocumentToSignersAssocs(NodeRef documentRef) {
		List<NodeRef> result = new ArrayList<NodeRef>();
		List<AssociationRef> signersAssocs = nodeService.getTargetAssocs(documentRef, ORDModel.ASSOC_ORD_SIGNERS);
		for (AssociationRef signerAssoc : signersAssocs) {
			NodeRef signerRef = signerAssoc.getTargetRef();
			if (signerRef != null) result.add(signerRef);
		}

		return result;
	}

	public void repealDocuments(ScriptNode ordSNode){
		NodeRef ord = ordSNode.getNodeRef();
		List<AssociationRef> repealProjAssocs = nodeService.getTargetAssocs(ord, ORDModel.ASSOC_ORD_CANCELED);
		for (AssociationRef repealProjAssoc:repealProjAssocs){
			NodeRef repealProj = repealProjAssoc.getTargetRef();
			nodeService.setProperty(repealProj, StatemachineModel.PROP_STATUS, "Отменен");

			//запись в бизнес журнал о том, что документ отменен
			String ordPresentStr = getOrdURL(ordSNode);
	 		String bjMessage = String.format("Документ #mainobject отменен документом %s", ordPresentStr);
			String registrarLogin = orgstructureService.getEmployeeLogin(orgstructureService.getCurrentEmployee());
			businessJournalService.log("System", repealProj, "CANCEL_DOCUMENT", bjMessage, null);

			//создадим связь
			documentConnectionService.createConnection(ord, repealProj, "cancel", true, true);
		}
	}

	public void changePointStatus(ScriptNode ordSNode){
		NodeRef ord = ordSNode.getNodeRef();
        Set<NodeRef> senders = documentEventService.getEventSenders(ord);
		for (NodeRef sender : senders){
			if (ErrandsService.TYPE_ERRANDS.equals(nodeService.getType(sender))){
				String errandStatus = (String) nodeService.getProperty(sender, StatemachineModel.PROP_STATUS);
				if ("Исполнено".equals(errandStatus)){
					List<AssociationRef> pointAssocs = nodeService.getSourceAssocs(sender, ORDModel.ASSOC_ORD_TABLE_ERRAND);
					for (AssociationRef pointAssoc : pointAssocs){
						NodeRef point = pointAssoc.getSourceRef();
						// переведем пункт в статус "Исполнен"
						NodeRef pointExecutedStatus = lecmDictionaryService.getDictionaryValueByParam(ORDModel.ORD_POINT_DICTIONARY_NAME, ContentModel.PROP_NAME, ORDModel.ORD_POINT_EXECUTED_STATUS);
						List<NodeRef> targetStatus = Arrays.asList(pointExecutedStatus);
						nodeService.setAssociations(point, ORDModel.ASSOC_ORD_TABLE_ITEM_STATUS, targetStatus);
					}
				}
			}
		}
	}
}
