package ru.it.lecm.ord.extensions;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.I18NUtil;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.*;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.ord.api.ORDDocumentService;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.ord.api.ORDReportsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.workflow.api.WorkflowResultModel;

import java.text.SimpleDateFormat;
import java.util.*;

import static ru.it.lecm.eds.api.EDSDocumentService.getFromMessagesOrDefaultValue;

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
	private BusinessJournalService businessJournalService;
	private DocumentEventService documentEventService;
	private ORDDocumentService ordDocumentService;
	private DictionaryBean dictionaryService;
	private NamespaceService namespaceService;
	private AuthenticationService authenticationService;
	private NotificationsService notificationsService;
	private ErrandsService errandsService;
	private StateMachineServiceBean stateMachineService;
    private EDSDocumentService edsService;
	private DocumentMembersService documentMembersService;


	private ORDReportsService ordReportsService;

	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentService(final DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setOrgstructureService(final OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

    public void setEdsGlobalSettingsService(EDSGlobalSettingsService edsGlobalSettingsService) {
		this.edsGlobalSettingsService = edsGlobalSettingsService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setDocumentEventService(DocumentEventService documentEventService) {
		this.documentEventService = documentEventService;
	}

	public void setOrdReportsService(ORDReportsService ordReportsService) {
		this.ordReportsService = ordReportsService;
	}

	public void setOrdDocumentService(ORDDocumentService ordDocumentService) {
		this.ordDocumentService = ordDocumentService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setErrandsService(ErrandsService errandsService) {
		this.errandsService = errandsService;
	}

	public void setStateMachineService(StateMachineServiceBean stateMachineService) {
		this.stateMachineService = stateMachineService;
	}

    public void setEdsService(EDSDocumentService edsService) {
        this.edsService = edsService;
    }

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}

	private String getOrdURL(final ScriptNode ordRef) {
		NodeRef ordDocumentRef = ordRef.getNodeRef();
		String presentString = (String) nodeService.getProperty(ordDocumentRef, DocumentService.PROP_PRESENT_STRING);
		return wrapperLink(ordDocumentRef.toString(), presentString, documentService.getDocumentUrl(ordDocumentRef));
	}

	/**
	 * ?????????????????? ?????????????????????? ???????????? ?????? ?? ?????????????????? ?????????? ????????????????????????
	 * ?????????????????????? ?? ???????????????????? "???????????? ?????????????????? &lt;?????? ??????????????????&gt; ???
	 * &lt;?????????? ??????????????????&gt; ?????????????????? ?????? ???? ??????????????????"
	 *
	 * @param ordRef ???????????? ???? ?????? ???????????????? ???? ???????????? ??????????????????
	 * @return ?????????????? ?? ???????????????? ??????????????????????
	 */
	@Deprecated
	public Notification prepareNotificationAboutRework(final ScriptNode ordRef) {
		String ordURL = getOrdURL(ordRef);
		String description = String.format("???????????? ?????????????????? %s ?????????????????? ?????? ???? ??????????????????", ordURL);
		return prepareNotificationAboutRework(ordRef, description);
	}

	/**
	 * ?????????????????? ?????????????????????? ???????????? ?????? ?? ??????????????????
	 *
	 * @param ordRef ???????????? ???? ?????? ???????????????? ???? ???????????? ??????????????????
	 * @param description ?????????? ??????????????????, ?????????????? ???????????????????? ??????????????????
	 * @return ?????????????? ?? ???????????????? ??????????????????????
	 */
	@Deprecated
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
	 * ?????????????????????? ???????????? ?????????????????????????? ?????? ?????????????????????? ?????????????? ???? ????????
	 *
	 * @param businessRoleId ?????????????????????????? ????????????-????????, ?????????????? ????????????????
	 * ?????????????????????????? ??????????????????????????
	 * @param ordRef ???????????? ???? ?????? ???????????????? ???? ???????????? ??????????????????
	 * @return ???????????? Scriptable ?????????????????? ?????? ???????????? ???? ???????????????????? ????????????
	 * ??????????????????
	 */
	public Scriptable getRegistrars(final String businessRoleId, final ScriptNode ordRef) {
		List<NodeRef> registrars;
		//???????????????? ???????????????? ????????????????????????
		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		// ???????????????????????????????? ???? ??????????????????????
		boolean registrationCenralized = edsGlobalSettingsService.isRegistrationCenralized();

		if (registrationCenralized) {
			registrars = orgstructureService.getEmployeesByBusinessRole(businessRoleId);
		} else {
			registrars = new ArrayList<NodeRef>();
			//???????????????? ???????????????? ?????????????????????? ??????????????
			NodeRef primaryStaff = orgstructureService.getEmployeePrimaryStaff(currentEmployee);
			if (primaryStaff != null) {
				NodeRef unit = orgstructureService.getUnitByStaff(primaryStaff);
				registrars.addAll(edsGlobalSettingsService.getPotentialWorkers(businessRoleId, unit));
			}
		}
		return createScriptable(registrars);
	}

	/**
	 * ?????????????????????? ?????????????????????? ?????????????????????????? ?? ??????, ?????? ???????? ????????????????????????????????
	 * ?????? ???????????????? ?????????? ???????????????????????? ?????????????????????? ?? ???????????????????? "????????????????
	 * &lt;?????? ??????????????????&gt; ??? &lt;?????????? ??????????????????&gt; ???????????????? ?????? ????
	 * ??????????????????????"
	 *
	 * @param ordRef ???????????? ???? ?????? ???????????????? ???? ???????????? ??????????????????
	 * @param registrars js-???????????? ?????????????????????????? ?????????????? ???? ???????????????? ???? ??????????????
	 * ???????????? ??????????????????
	 * @return ?????????????? ?? ???????????????? ??????????????????????
	 */
	@Deprecated
	public Notification prepareNotificationAboutRegistration(final ScriptNode ordRef, final Scriptable registrars) {
		String ordURL = getOrdURL(ordRef);
		String description = String.format("???????????????? %s ???????????????? ?????? ???? ??????????????????????", ordURL);
		return prepareNotificationAboutRegistration(ordRef, registrars, description);
	}

	/**
	 * ?????????????????????? ?????????????????????? ?????????????????????????? ?? ??????, ?????? ???????? ????????????????????????????????
	 * ?????? ????????????????
	 *
	 * @param ordRef ???????????? ???? ?????? ???????????????? ???? ???????????? ??????????????????
	 * @param registrars js-???????????? ?????????????????????????? ?????????????? ???? ???????????????? ???? ??????????????
	 * ???????????? ??????????????????
	 * @param description ?????????? ??????????????????, ?????????????? ???????????????????? ??????????????????
	 * @return ?????????????? ?? ???????????????? ??????????????????????
	 */
	@Deprecated
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
	 * ?????????????????????? ?????????????????????? ???????????????????? ?? ??????, ?????? ???? ???????????? ?? ????????????????
	 * ???????????????????? ?????? ??????????????????
	 *
	 * @param ord ???????????? ???? ?????? ???????????????? ???? ?????????????? ???????????? ??????????????????
	 * @param controller ???????????? ???? ???????????????????? ???? ?????????????? ???????????? ??????????????????
	 * @return ?????????????? ?? ???????????????? ??????????????????????
	 */
	@Deprecated
	public Notification prepareNotificationToController(final ScriptNode ord, final ScriptNode controller) {
		String ordURL = getOrdURL(ord);
		Date execDate = (Date) nodeService.getProperty(ord.getNodeRef(), EDSDocumentService.PROP_EXECUTION_DATE);
		String formatExecDate = "???? ????????????";
		if (null != execDate) {
			formatExecDate = new SimpleDateFormat("dd.MM.yyyy").format(execDate);
		}
		String description = String.format("???? ?????????????????? ?????????????????????? ???? ?????????????????? %s. ???????? ????????????????????: %s", ordURL, formatExecDate);

		List<NodeRef> recipients = Arrays.asList(controller.getNodeRef());

		Notification notification = new Notification();
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
		notification.setObjectRef(ord.getNodeRef());
		notification.setRecipientEmployeeRefs(recipients);
		return notification;
	}

	 /**
	 * ???????????????????????? ?????????????????? ???? ?????????????? ??????
	 *
	 * @param ordSNode ???????????? ???? ?????? ???????????????? ???? ?????????????? ???????????? ??????????????????
	 */
	public void formErrands(ScriptNode ordSNode) {
		NodeRef ord = ordSNode.getNodeRef();

		List<NodeRef> childErrands = errandsService.getChildErrands(ord);
		Integer currentChildIndex = 0;
		if (childErrands != null && childErrands.size() != 0) {
			for(NodeRef child: childErrands){
				if(!stateMachineService.isDraft(child)){
					currentChildIndex++;
				}
			}
		}
		//???????????? ?????????????? ?? ????????????????
		List<AssociationRef> tableAssocs = nodeService.getTargetAssocs(ord, ORDModel.ASSOC_ORD_TABLE_ITEMS);
		if (tableAssocs.size() > 0) {
			NodeRef table = tableAssocs.get(0).getTargetRef();
			Set<QName> pointType = new HashSet<QName>(Arrays.asList(ORDModel.TYPE_ORD_TABLE_ITEM));
			List<ChildAssociationRef> pointAssocs = nodeService.getChildAssocs(table, pointType);
			for (ChildAssociationRef pointAssoc : pointAssocs) {
				NodeRef point = pointAssoc.getChildRef();

                String pointDateText = (String) nodeService.getProperty(point, ORDModel.PROP_ORD_TABLE_ITEM_DATE_TEXT);
                String pointDateDays = nodeService.getProperty(point, ORDModel.PROP_ORD_TABLE_ITEM_DATE_DAYS).toString();
                String pointDateRadio = (String) nodeService.getProperty(point, ORDModel.PROP_ORD_TABLE_ITEM_DATE_RADIO);
                String pointDateType = (String) nodeService.getProperty(point, ORDModel.PROP_ORD_TABLE_ITEM_DATE_TYPE);
                Date limitationDate = (Date) nodeService.getProperty(point, ORDModel.PROP_ORD_TABLE_EXECUTION_DATE);

                //???????????????? ???????? ???????????? ??????
                if (EDSDocumentService.COMPLEX_DATE_RADIO_DAYS.equals(pointDateRadio)) {
                    Integer daysInt = StringUtils.isNotEmpty(pointDateDays) ? Integer.parseInt(pointDateDays) : null;
                    Date computedDate = edsService.convertComplexDate(pointDateRadio, new Date(), pointDateType, daysInt);

                    if (computedDate != null) {
                        pointDateRadio = EDSDocumentService.COMPLEX_DATE_RADIO_DATE;
                        limitationDate = computedDate;
                        pointDateText = computedDate.toString();

                        nodeService.setProperty(point, ORDModel.PROP_ORD_TABLE_ITEM_DATE_RADIO, pointDateRadio);
                        nodeService.setProperty(point, ORDModel.PROP_ORD_TABLE_EXECUTION_DATE, limitationDate);
                        nodeService.setProperty(point, ORDModel.PROP_ORD_TABLE_ITEM_DATE_TEXT, pointDateText);
                    }
                }
                //???????????????? ??????????????????
				Map<String, String> properties = new HashMap<String, String>();
				//??????????????????
				Integer pointNumber = (Integer) nodeService.getProperty(point, DocumentTableService.PROP_INDEX_TABLE_ROW);
				String pointTitle = (String) nodeService.getProperty(point, ORDModel.PROP_ORD_TABLE_ITEM_TITLE);
				List<AssociationRef> ordDocTypeAssocs = nodeService.getTargetAssocs(ord, EDSDocumentService.ASSOC_DOCUMENT_TYPE);
				String ordDocType = "";
				if (ordDocTypeAssocs.size() > 0) {
					NodeRef ordDocTypeRef = ordDocTypeAssocs.get(0).getTargetRef();
					ordDocType = (String) nodeService.getProperty(ordDocTypeRef, ContentModel.PROP_NAME);
				}
				String ordNumber = (String) nodeService.getProperty(ord, DocumentService.PROP_REG_DATA_DOC_NUMBER);
				Date ordRegDate = (Date) nodeService.getProperty(ord, DocumentService.PROP_REG_DATA_DOC_DATE);
				String ordRegDateStr = new SimpleDateFormat("dd.MM.yyyy").format(ordRegDate);

				String errandTitle;
                String presentString = I18NUtil.getMessage("lecm.ord.point.present-string", I18NUtil.getLocale());
				if (presentString != null) {
					 errandTitle = presentString.replace("{pointNumber}", pointNumber.toString())
							.replace("{pointTitle}", pointTitle);
				} else {
					errandTitle = "?????????????????? ???? ???????????? ??? " + pointNumber.toString() + " " + pointTitle;
				}

                properties.put(ErrandsService.PROP_ERRANDS_TITLE.toPrefixString(namespaceService), errandTitle);
				//????????????????????
				String content = (String) nodeService.getProperty(point, ORDModel.PROP_ORD_TABLE_ITEM_CONTENT);
				properties.put(ErrandsService.PROP_ERRANDS_CONTENT.toPrefixString(namespaceService), content);
				//????????????????
				properties.put(ErrandsService.PROP_ERRANDS_IS_IMPORTANT.toPrefixString(namespaceService), "false");
				properties.put(ErrandsService.PROP_ERRANDS_JUST_IN_TIME.toPrefixString(namespaceService), "false");
				properties.put(ErrandsService.PROP_ERRANDS_IS_PERIODICALLY.toPrefixString(namespaceService), "false");
				if (Boolean.TRUE.equals(nodeService.getProperty(point, ORDModel.PROP_ORD_TABLE_ITEM_REPORT_REQUIRED))){
					properties.put(ErrandsService.PROP_ERRANDS_REPORT_RECIPIENT_TYPE.toPrefixString(namespaceService), "CONTROLLER");
				} else {
					properties.put(ErrandsService.PROP_ERRANDS_REPORT_RECIPIENT_TYPE.toPrefixString(namespaceService), "NOT_REQUIRED");
				}
				//???????? ????????????????????
				properties.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TEXT.toPrefixString(namespaceService), pointDateText);
				properties.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_DAYS.toPrefixString(namespaceService),  pointDateDays);
				properties.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO.toPrefixString(namespaceService), pointDateRadio);
				properties.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TYPE.toPrefixString(namespaceService), pointDateType);
				properties.put(ErrandsService.PROP_ERRANDS_REPORT_REQUIRED.toPrefixString(namespaceService), nodeService.getProperty(point, ORDModel.PROP_ORD_TABLE_ITEM_REPORT_REQUIRED).toString());

				//????????????
				Integer errandIndex = currentChildIndex + pointNumber;
				properties.put(ErrandsService.PROP_ERRANDS_CHILD_INDEX.toPrefixString(namespaceService), errandIndex.toString());

				//???????????????????? ??????????????????
				Map<String, String> associations = new HashMap<String, String>();
				//?????? ??????????????????
				String errandTypeOnORDPointName = getFromMessagesOrDefaultValue("lecm.ord.point.errand.type.name", ErrandsService.ERRAND_TYPE_ON_POINT_ORD);
				NodeRef type = dictionaryService.getRecordByParamValue(getFromMessagesOrDefaultValue("ru.it.lecm.dictionaries.errandTypes.name", ErrandsService.ERRANDS_TYPE_DICTIONARY_NAME), ContentModel.PROP_NAME, errandTypeOnORDPointName);
				associations.put(ErrandsService.ASSOC_ERRANDS_TYPE.toPrefixString(namespaceService), type.toString());

				// ?????????? ??????????????????
				List<AssociationRef> authorAssocs = nodeService.getTargetAssocs(point, ORDModel.ASSOC_ORD_TABLE_ITEM_AUTHOR);
				if (authorAssocs != null && authorAssocs.size() > 0) {
					NodeRef author = authorAssocs.get(0).getTargetRef();
					associations.put(ErrandsService.ASSOC_ERRANDS_INITIATOR.toPrefixString(namespaceService), author.toString());
					if (errandsService.isTransferRightToBaseDocument()) {
						documentMembersService.addMemberWithoutCheckPermission(ord, author, true);
					}
				}
				//??????????????????????
				List<AssociationRef> pointExecutorAssocs = nodeService.getTargetAssocs(point, ORDModel.ASSOC_ORD_TABLE_EXECUTOR);
				if (pointExecutorAssocs != null && pointExecutorAssocs.size() > 0) {
					NodeRef executor = pointExecutorAssocs.get(0).getTargetRef();
					associations.put(ErrandsService.ASSOC_ERRANDS_EXECUTOR.toPrefixString(namespaceService), executor.toString());
					if (errandsService.isTransferRightToBaseDocument()) {
						documentMembersService.addMemberWithoutCheckPermission(ord, executor, true);
					}
                }
				//??????????????????
				List<AssociationRef> pointControllerAssocs = nodeService.getTargetAssocs(point, ORDModel.ASSOC_ORD_TABLE_CONTROLLER);
				if (pointControllerAssocs !=null && pointControllerAssocs.size() > 0) {
					NodeRef controller = pointControllerAssocs.get(0).getTargetRef();
					associations.put(ErrandsService.ASSOC_ERRANDS_CONTROLLER.toPrefixString(namespaceService), controller.toString());
					if (errandsService.isTransferRightToBaseDocument()) {
						documentMembersService.addMemberWithoutCheckPermission(ord, controller, true);
					}
                }
				//??????????????????????????
				List<AssociationRef> pointCoExecutorsAssocs = nodeService.getTargetAssocs(point, ORDModel.ASSOC_ORD_TABLE_COEXECUTORS);
				if (pointCoExecutorsAssocs != null && pointCoExecutorsAssocs.size() > 0) {
                    ArrayList<NodeRef> coexecutorsList = new ArrayList<>();
					for (AssociationRef coexecutors : pointCoExecutorsAssocs) {
						coexecutorsList.add(coexecutors.getTargetRef());
						if (errandsService.isTransferRightToBaseDocument()) {
							documentMembersService.addMemberWithoutCheckPermission(ord, coexecutors.getTargetRef(), true);
						}
                    }
					associations.put(ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS.toPrefixString(namespaceService), StringUtils.join(coexecutorsList, ","));
				}
				//???????????????? ??????????????????
				List<AssociationRef> subjectAssocs = nodeService.getTargetAssocs(point, ORDModel.ASSOC_ORD_TABLE_SUBJECT);
				if (!subjectAssocs.isEmpty()) {
					List<String> subjects = new ArrayList<>();
					for (AssociationRef subjectAssoc : subjectAssocs) {
						subjects.add(subjectAssoc.getTargetRef().toString());
					}
					associations.put("lecm-document:subject-assoc", StringUtils.join(subjects, ","));
				}

				NodeRef errand = documentService.createDocument(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService), properties, associations);
				nodeService.addAspect(errand, ErrandsService.ASPECT_SKIP_TRANSFER_RIGHT_TO_PARENT_ASPECT, null);
				// ???????? ??????????????????
				if (limitationDate != null) {
					nodeService.setProperty(errand, ErrandsService.PROP_ERRANDS_LIMITATION_DATE, limitationDate);
				}
				//???????? ????????????
				NodeRef singFolder = nodeService.getChildByName(ord, ContentModel.ASSOC_CONTAINS, "????????????????????");
				if (null != singFolder) {
                    List<ChildAssociationRef> signListAssocs = nodeService.getChildAssocs(singFolder);
                    if (signListAssocs.size() > 0) {
                        NodeRef signList = signListAssocs.get(0).getChildRef();
                        Date signCompleteDate = (Date) nodeService.getProperty(signList, WorkflowResultModel.PROP_WORKFLOW_RESULT_LIST_COMPLETE_DATE);
                        nodeService.setProperty(errand, ErrandsService.PROP_ERRANDS_START_DATE, signCompleteDate);
                    }
                }

				//???????????????? ???????????????????? ?????????? ?????????? ?????? ?? ?????????????????? ????????????????????, ?????????????????? ?????????? ?????????????????? ??????????????????????????
				nodeService.createAssociation(errand, ord, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
				// ???????????????? ???????????????????? ???????????? ?? ????????????????????
				nodeService.createAssociation(point, errand, ORDModel.ASSOC_ORD_TABLE_ERRAND);
				// ?????????????????? ?????????? ?? ???????????? "???? ??????????????????????"
				ordDocumentService.changePointStatus(point, ORDModel.P_STATUSES.PERFORMANCE_STATUS.toString());

			}
			//???????????????????? ?????????????????????? ??????
			List<AssociationRef> ordControllerAssoc = nodeService.getTargetAssocs(ord, ORDModel.ASSOC_ORD_CONTROLLER);
			if (ordControllerAssoc != null && ordControllerAssoc.size() != 0) {
				String ntAuthor = authenticationService.getCurrentUserName();
				NodeRef ntInitiator = orgstructureService.getCurrentEmployee();
				List<NodeRef> recipients = Collections.singletonList(ordControllerAssoc.get(0).getTargetRef());
				Map<String, Object> templateConfig = new HashMap<>();
				templateConfig.put("mainObject", ord);
				templateConfig.put("eventExecutor", ntInitiator);
				notificationsService.sendNotification(ntAuthor, ntInitiator, recipients, "ORD_ITEM_CREATE_ERRAND", templateConfig, true);
			}
		}
	}

    @Deprecated
	public void changePointStatusByErrand(ScriptNode ordSNode){
		NodeRef ord = ordSNode.getNodeRef();
        Set<NodeRef> senders = documentEventService.getEventSenders(ord);
		for (NodeRef sender : senders){
			if (ErrandsService.TYPE_ERRANDS.equals(nodeService.getType(sender))){
				String errandStatus = (String) nodeService.getProperty(sender, StatemachineModel.PROP_STATUS);
				NodeRef point = ordDocumentService.getErrandLinkedPoint(sender);
				if (null!=point){

					if (ErrandsService.ERRANDS_STATUS.EXECUTED.isStatusEquals(errandStatus, errandsService)){
						// ?????????????????? ?????????? ?? ???????????? "????????????????"
						ordDocumentService.changePointStatus(point, ORDModel.P_STATUSES.EXECUTED_STATUS.toString());
						//?????????????????? ?????????????? ???????? ??????????????????
						nodeService.setProperty(point, ORDModel.PROP_ORD_TABLE_EXECUTION_DATE_REAL, new Date());
						//???????????? ?? ???????????? ???????????? ?? ??????, ?????? ?????????? ?????????????? ?? ???????????? ????????????????
						Integer pointNumber = (Integer) nodeService.getProperty(point, DocumentTableService.PROP_INDEX_TABLE_ROW);
						String statusName = ordDocumentService.getPointStatus(point);
						String bjMessage = String.format(EDSDocumentService.getFromMessagesOrDefaultValue("ru.it.lecm.ord.bjMessages.pointChangeStatus", "?????????? ?????????? %s ?????????????????? #mainobject ?????????????? ?? ???????????? %s"), pointNumber, statusName);
						List<String> secondaryObj = Arrays.asList(point.toString());
						businessJournalService.log("System", ord, "POINT_STATUS_CHANGE", bjMessage, secondaryObj);
					}
					if (ErrandsService.ERRANDS_STATUS.NOT_EXECUTED.isStatusEquals(errandStatus, errandsService)){
						// ?????????????????? ?????????? ?? ???????????? "???? ????????????????"
						ordDocumentService.changePointStatus(point, ORDModel.P_STATUSES.NOT_EXECUTED_STATUS.toString());
						//?????????????????? ?????????????? ???????? ??????????????????
						nodeService.setProperty(point, ORDModel.PROP_ORD_TABLE_EXECUTION_DATE_REAL, new Date());
						//???????????? ?? ???????????? ???????????? ?? ??????, ?????? ?????????? ?????????????? ?? ???????????? ???? ????????????????
						Integer pointNumber = (Integer) nodeService.getProperty(point, DocumentTableService.PROP_INDEX_TABLE_ROW);
						String statusName = ordDocumentService.getPointStatus(point);
						String bjMessage = String.format(EDSDocumentService.getFromMessagesOrDefaultValue("ru.it.lecm.ord.bjMessages.pointChangeStatus", "?????????? ?????????? %s ?????????????????? #mainobject ?????????????? ?? ???????????? %s"), pointNumber, statusName);
						List<String> secondaryObj = Arrays.asList(point.toString());
						businessJournalService.log("System", ord, "POINT_STATUS_CHANGE", bjMessage, secondaryObj);
					}

					Boolean is_expired = (Boolean) nodeService.getProperty(sender,ErrandsService.PROP_ERRANDS_IS_EXPIRED);
					if (!ErrandsService.ERRANDS_STATUS.EXECUTED.isStatusEquals(errandStatus, errandsService) && is_expired){
						// ?????????????????? ?????????? ?? ???????????? "??????????????????"
						ordDocumentService.changePointStatus(point, ORDModel.P_STATUSES.EXPIRED_STATUS.toString());
						//???????????? ?? ???????????? ???????????? ?? ??????, ?????? ?????????? ?????????????? ?? ???????????? ??????????????????
						Integer pointNumber = (Integer) nodeService.getProperty(point, DocumentTableService.PROP_INDEX_TABLE_ROW);
						String bjMessage = String.format(EDSDocumentService.getFromMessagesOrDefaultValue("ru.it.lecm.ord.bjMessages.pointExpired", "???????????????????? ???????????? ??? %s ?????????????????? #mainobject ????????????????????"), pointNumber);
						List<String> secondaryObj = Arrays.asList(point.toString());
						businessJournalService.log("System", ord, "POINT_STATUS_CHANGE", bjMessage, secondaryObj);
					}
				}
			}
			// ???????????? ???????????????????????? ???? ????????????, ?????????? ?? ?????????????????? ?????? ???????? ???????????? ??????????
			documentEventService.removeEventSender(ord, sender);
		}
	}

    public ScriptNode getErrandLinkedPoint(ScriptNode errand) {
        if (errand != null) {
            NodeRef errandRef = errand.getNodeRef();
            if (errandRef != null) {
                NodeRef point = ordDocumentService.getErrandLinkedPoint(errandRef);
                if (point != null) {
                    return new ScriptNode(point, serviceRegistry, getScope());
                }
            }
        }
        return null;
    }

	public void changePointStatus(String sPointRef, String statusKey){
		if (null!=sPointRef && !sPointRef.isEmpty()){
			NodeRef point = new NodeRef(sPointRef);
			if (nodeService.exists(point)){
				ordDocumentService.changePointStatus(point, statusKey);
			}
		}
	}

	public String getPointStatusTextByCode(String statusCode){
        NodeRef statusRef = dictionaryService.getDictionaryValueByParam(getFromMessagesOrDefaultValue("ru.it.lecm.dictionaries.ordPoints.name", ORDModel.ORD_POINT_DICTIONARY_NAME), ORDModel.PROP_ORD_DIC_POINT_STATUS_CODE, statusCode);
        if (statusRef != null) {
            return (String) nodeService.getProperty(statusRef, ContentModel.PROP_NAME);
        }
		return "";
	}

	public Boolean checkPointExecutedStatus(String sPointRef){
		if (null!=sPointRef && !sPointRef.isEmpty()){
			NodeRef point = new NodeRef(sPointRef);
			if (nodeService.exists(point)){
				return ordDocumentService.checkPointStatus(point, ORDModel.P_STATUSES.EXECUTED_STATUS.toString());
			}
		}
		return false;
	}

	@Deprecated
	public ScriptNode generateDocumentReport(final String reportCode, final String templateCode, final String documentRef) {
		NodeRef reportNodeRef = ordReportsService.generateDocumentReport(reportCode, templateCode, documentRef);

		return new ScriptNode(reportNodeRef, serviceRegistry, getScope());
	}
}
