package ru.it.lecm.delegation.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.PropertyCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.TransactionNeededException;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.delegation.DelegationEventCategory;
import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.delegation.IDelegationDescriptor;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.orgstructure.beans.OrgstructureSGNotifierBean;
import ru.it.lecm.secretary.SecretarySecurityService;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.wcalendar.absence.IAbsence;

import java.io.Serializable;
import java.util.*;

public class DelegationBean extends BaseBean implements IDelegation, IDelegationDescriptor {

	final private static Logger logger = LoggerFactory.getLogger(DelegationBean.class);
	private final static String CONTAINER = "DelegationOptionsContainer";
	public final static String DELEGATION_FOLDER = "DELEGATION_FOLDER";
	public final static String TASKS_DELEGATION_FOLDER = "TASKS_DELEGATION_FOLDER";
	private final static String REVIEWER_PERMISSION_GROUP = "LECM_BASIC_PG_Reviewer";
	private final static String READER_PERMISSION_GROUP = "LECM_BASIC_PG_Reader";

	private static final String GRAND_DYNAMIC_ROLE_CODE_INITIATOR = "BR_INITIATOR";

	private OrgstructureBean orgstructureService;
	private PersonService personService;
	private BusinessJournalService businessJournalService;
	private OrgstructureSGNotifierBean sgNotifierService;
	private DictionaryBean dictionaryService;
	private DictionaryService dictionaryServiceAlfresco;
	private IAbsence absenceService;
	private WorkflowService workflowService;
	private NotificationsService notificationsService;
	private DocumentMembersService documentMembersService;
	private LecmPermissionService lecmPermissionService;
	private SecretarySecurityService secretarySecurityService;

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setSgNotifierService(OrgstructureSGNotifierBean sgNotifierService) {
		this.sgNotifierService = sgNotifierService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setDictionaryServiceAlfresco(DictionaryService dictionaryServiceAlfresco) {
		this.dictionaryServiceAlfresco = dictionaryServiceAlfresco;
	}

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}

	public void setSecretarySecurityService(SecretarySecurityService secretarySecurityService) {
		this.secretarySecurityService = secretarySecurityService;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);
		PropertyCheck.mandatory(this, "personService", personService);
		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory(this, "sgNotifierService", sgNotifierService);
		PropertyCheck.mandatory(this, "absenceService", absenceService);
		PropertyCheck.mandatory(this, "workflowService", workflowService);
		PropertyCheck.mandatory(this, "notificationsService", notificationsService);
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "dictionaryService", dictionaryServiceAlfresco);
		PropertyCheck.mandatory(this, "documentMembersService", documentMembersService);
		PropertyCheck.mandatory(this, "lecmPermissionService", lecmPermissionService);
		PropertyCheck.mandatory(this, "secretarySecurityService", secretarySecurityService);

		//создание контейнера для хранения параметров делегирования
		AuthenticationUtil.runAsSystem(new RunAsWork<Void>() {

			@Override
			public Void doWork() throws Exception {
				return lecmTransactionHelper.doInRWTransaction(new RetryingTransactionCallback<Void>() {

					@Override
					public Void execute() throws Throwable {
						if (null == getDelegationOptsContainer()) {
							createDelegationOptsContainer();
						}
						if (null == getGlobalSettingsNode()) {
							createGlobalSettingsNode();
						}
						return null;
					}
				});

			}
		});

		//возможно здесь еще будет штука для создания параметров делегирования для уже существующих пользователей
	}

	private NodeRef createDelegationOptsContainer() {
		NodeRef parentRef = getDelegationFolder(); //the parent node
		QName assocTypeQName = ContentModel.ASSOC_CONTAINS; //the type of the association to create. This is used for verification against the data dictionary.
		QName assocQName = QName.createQName(DELEGATION_NAMESPACE, CONTAINER); //the qualified name of the association
		QName nodeTypeQName = TYPE_DELEGATION_OPTS_CONTAINER; //a reference to the node type
		// создание корневого узла для делегирований в Компании ...
		Map<QName, Serializable> properties = new HashMap<>(1); //optional map of properties to keyed by their qualified names
		properties.put(ContentModel.PROP_NAME, CONTAINER);
		ChildAssociationRef associationRef = nodeService.createNode(parentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
		NodeRef delegationRoot = associationRef.getChildRef();
		logger.debug(String.format("container node '%s' created", delegationRoot.toString()));
		return delegationRoot;
	}

	@Override
	public NodeRef getDelegationFolder() {
		return getFolder(DELEGATION_FOLDER);
	}

	@Override
	public NodeRef getDelegationOptsContainer() {
		NodeRef delegationHome = getDelegationFolder();
		NodeRef container = nodeService.getChildByName(delegationHome, ContentModel.ASSOC_CONTAINS, CONTAINER);
		return container;
	}

	@Override
	public QName getDelegationOptsItemType() {
		return TYPE_DELEGATION_OPTS;
	}

	@Override
	public IDelegationDescriptor getDelegationDescriptor() {
		return this;
	}

	@Override
	public NodeRef createDelegationOpts(final NodeRef employeeNodeRef) {
		return AuthenticationUtil.runAsSystem(new RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				Serializable employeeName = nodeService.getProperty(employeeNodeRef, ContentModel.PROP_NAME);
				String delegationOptsName = String.format("параметры делегирования для %s", employeeName);
				//создание ноды и установка ей ассоциации
				NodeRef parentRef = getDelegationOptsContainer(); //the parent node
				QName assocTypeQName = ASSOC_DELEGATION_OPTS_CONTAINER; //the type of the association to create. This is used for verification against the data dictionary.
				QName assocQName = QName.createQName(DELEGATION_NAMESPACE, delegationOptsName); //the qualified name of the association
				QName nodeTypeQName = TYPE_DELEGATION_OPTS; //a reference to the node type
				Map<QName, Serializable> properties = new HashMap<>(); //optional map of properties to keyed by their qualified names
				properties.put(ContentModel.PROP_NAME, delegationOptsName);
				properties.put(IS_ACTIVE, false);//параметры делегирования по умолчанию создаем неактивными
				NodeRef delegationOptsNodeRef = nodeService.createNode(parentRef, assocTypeQName, assocQName, nodeTypeQName, properties).getChildRef();
				nodeService.createAssociation(delegationOptsNodeRef, employeeNodeRef, ASSOC_DELEGATION_OPTS_OWNER);
				return delegationOptsNodeRef;
			}
		});
	}

	@Override
    //TODO Refactoring in progress
	//Было использование getOrCreate в get методе. Так нельзя.
	public NodeRef getDelegationOpts(NodeRef nodeRef) {
		NodeRef delefationOptsRef = null;
		if (isDelegationOpts(nodeRef)) {
			delefationOptsRef = nodeRef;
		} else if (orgstructureService.isEmployee(nodeRef)) {
			delefationOptsRef = findNodeByAssociationRef(nodeRef, ASSOC_DELEGATION_OPTS_OWNER, TYPE_DELEGATION_OPTS, ASSOCIATION_TYPE.SOURCE);
		} else if (isProperType(nodeRef, ContentModel.TYPE_PERSON)) {
			NodeRef employeeRef = orgstructureService.getEmployeeByPerson(nodeRef);
			if (employeeRef != null) {
				delefationOptsRef = findNodeByAssociationRef(employeeRef, ASSOC_DELEGATION_OPTS_OWNER, TYPE_DELEGATION_OPTS, ASSOCIATION_TYPE.SOURCE);
			} else {
				String fistname = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_FIRSTNAME);
				String lastname = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_LASTNAME);
				String username = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_USERNAME);
				logger.warn(String.format("Alfresco user %s (%s %s) does not mapped to lecm-orgstr:employee", username, fistname, lastname));
			}
		} else {
			QName nodeType = nodeService.getType(nodeRef);
			logger.warn(String.format("NodeRef {%s}%s can't have delegation options.", nodeType, nodeRef));
		}
		return delefationOptsRef;
	}

	@Override
	public List<NodeRef> getUniqueBusinessRolesByEmployee(final NodeRef employeeNodeRef, final boolean onlyActive) {
		//получаем все бизнес роли
		Set<NodeRef> uniqueBusinessRoleNodeRefs = new HashSet<>(orgstructureService.getEmployeeRoles(employeeNodeRef));
		final List<NodeRef> dynamicBusinessRoles = dictionaryService.getRecordsByParamValue(OrgstructureBean.BUSINESS_ROLES_DICTIONARY_NAME, OrgstructureBean.PROP_BUSINESS_ROLE_IS_DYNAMIC, true);
		for (NodeRef businessRole : dynamicBusinessRoles) {
			String roleName = nodeService.getProperty(businessRole, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER).toString();
			if (!isArchive(businessRole) && !roleName.equals(GRAND_DYNAMIC_ROLE_CODE_INITIATOR)) {
				uniqueBusinessRoleNodeRefs.add(businessRole);
			}
		}

		return new ArrayList<>(uniqueBusinessRoleNodeRefs);
	}

	@Override
	public List<NodeRef> getUniqueBusinessRolesByPerson(NodeRef personNodeRef, final boolean onlyActive) {
		NodeRef employeeNodeRef = findNodeByAssociationRef(personNodeRef, OrgstructureBean.ASSOC_EMPLOYEE_PERSON, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.SOURCE);
		return getUniqueBusinessRolesByEmployee(employeeNodeRef, onlyActive);
	}

	@Override
	public List<NodeRef> getUniqueBusinessRolesByDelegationOpts(NodeRef delegationOptsNodeRef, final boolean onlyActive) {
		NodeRef employeeNodeRef = findNodeByAssociationRef(delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		return getUniqueBusinessRolesByEmployee(employeeNodeRef, onlyActive);
	}

	@Override
	public List<NodeRef> getProcuracies(final NodeRef nodeRef, final boolean onlyActive) {
		NodeRef delegationOptsNodeRef = getDelegationOpts(nodeRef);
		List<NodeRef> procuracyNodeRefs = new ArrayList<>();
		if (delegationOptsNodeRef != null) {
			List<ChildAssociationRef> childAssociationRefs = nodeService.getChildAssocs(delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_PROCURACY, RegexQNamePattern.MATCH_ALL);
			if (childAssociationRefs != null) {
				for (ChildAssociationRef childAssociationRef : childAssociationRefs) {
					NodeRef procuracyNodeRef = childAssociationRef.getChildRef();
					//по идее еще и бизнес роль на активность надо проверять
					NodeRef businessRoleRef = findNodeByAssociationRef(procuracyNodeRef, ASSOC_PROCURACY_BUSINESS_ROLE, OrgstructureBean.TYPE_BUSINESS_ROLE, ASSOCIATION_TYPE.TARGET);
					NodeRef trusteeRef = findNodeByAssociationRef(procuracyNodeRef, ASSOC_PROCURACY_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
					Boolean hasActiveBusinessRole;
					if (businessRoleRef != null) {
						hasActiveBusinessRole = (Boolean) nodeService.getProperty(businessRoleRef, IS_ACTIVE);
					} else {
						hasActiveBusinessRole = false;
					}

					if (hasActiveBusinessRole && (!onlyActive || (!isArchive(procuracyNodeRef) && trusteeRef != null))) {
						procuracyNodeRefs.add(procuracyNodeRef);
					}
				}
			}
		}
		return procuracyNodeRefs;
	}

	@Override
    //TODO Refactoring in progress
	//Вызывается из вебскрипта, выносим управление транзакцией туда. здесь только проверяем.
	public List<NodeRef> createEmptyProcuracies(final NodeRef delegationOptsNodeRef, final List<NodeRef> businessRoleNodeRefs) {

		try {
			lecmTransactionHelper.checkTransaction();
		} catch (TransactionNeededException ex) {
			throw new WebScriptException("Can't create procuracies", ex);
		}

		List<NodeRef> procuracyNodeRefs = new ArrayList<>();
		for (NodeRef businessRoleNodeRef : businessRoleNodeRefs) {
			NodeRef parentRef = delegationOptsNodeRef; //the parent node
			QName assocTypeQName = ASSOC_DELEGATION_OPTS_PROCURACY; //the type of the association to create. This is used for verification against the data dictionary.
			QName assocQName = QName.createQName(DELEGATION_NAMESPACE, "доверенность_" + UUID.randomUUID().toString()); //the qualified name of the association
			QName nodeTypeQName = TYPE_PROCURACY; //a reference to the node type
			Map<QName, Serializable> properties = new HashMap<>(1); //optional map of properties to keyed by their qualified names
			properties.put(IS_ACTIVE, false);
			ChildAssociationRef associationRef = nodeService.createNode(parentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
			NodeRef procuracyNodeRef = associationRef.getChildRef();
			nodeService.createAssociation(procuracyNodeRef, businessRoleNodeRef, ASSOC_PROCURACY_BUSINESS_ROLE);
			procuracyNodeRefs.add(procuracyNodeRef);
		}
		return procuracyNodeRefs;

	}

	/**
	 * поиск значения проверти или ассоциации в JSON объекте который пришел с формы
	 *
	 * @param options JSON объект по которому искать
	 * @param pattern кусочек имени по которому пытаться искать
	 * @return Object если нашел, null в противном случае
	 */
	private <T> T findInOptions(final JSONObject options, final String pattern, final String typeName) {
		Object result = null;
		try {
			Iterator<Object> itr = options.keys();
			while (itr.hasNext()) {
				String key = itr.next().toString();
				if (key.contains(pattern)) {
					if ("Boolean".equals(typeName)) {
						result = options.getBoolean(key);
					} else if ("Double".equals(typeName)) {
						result = options.getDouble(key);
					} else if ("Integer".equals(typeName)) {
						result = options.getInt(key);
					} else if ("JSONArray".equals(typeName)) {
						result = options.getJSONArray(key);
					} else if ("JSONObject".equals(typeName)) {
						result = options.getJSONObject(key);
					} else if ("Long".equals(typeName)) {
						result = options.getLong(key);
					} else if ("String".equals(typeName)) {
						result = options.getString(key);
					} else {
						result = options.get(key);
					}
				}
			}
		} catch (JSONException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return (T) result;
	}

	private void logSaveDelegationOpts(final NodeRef delegationOptsRef) {
		final NodeRef initiator = orgstructureService.getPersonForEmployee(orgstructureService.getCurrentEmployee());
		NodeRef mainObject = findNodeByAssociationRef(delegationOptsRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		String template = "Сотрудник #mainobject изменил параметры делегирования";
		businessJournalService.log(initiator, mainObject, DelegationEventCategory.CHANGE_DELEGATION_OPTS, template, null);
	}

	@Override
	public String saveDelegationOpts(final NodeRef delegationOptsNodeRef, final JSONObject options) {

        //получаем ссылку на доверенное лицо из options
		//String propTrustee = ASSOC_DELEGATION_OPTS_TRUSTEE.getLocalName ();
		//String propTrustee = "assoc_lecm-d8n_delegation-opts-trustee-assoc";
		String propTrustee = "delegation-opts-part1_assoc_lecm-d8n_delegation-opts-trustee-assoc-cntrl-selectedItems";
		String trusteeRef = findInOptions(options, propTrustee, "String");
		//String trusteeRef = findInOptions (options, propTrustee + "_added", "String");
		if (trusteeRef != null) {
			//переназначем ассоциацию на доверенное лицо
			List<NodeRef> trusteeRefs = NodeRef.getNodeRefs(trusteeRef);
			nodeService.setAssociations(delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_TRUSTEE, trusteeRefs);

		} else { //если поле доверенное лицо пустое
			//по ассоциации получаем ссылку на доверенное лицо, если она есть
			List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_TRUSTEE);
			//если по ассоциациям кто-то есть, берем его и удаляем.
			if (targetAssocs != null && !targetAssocs.isEmpty()) {
				for (AssociationRef targetAssoc : targetAssocs) {
					NodeRef trusteeNodeRef = targetAssoc.getTargetRef();
					nodeService.removeAssociation(delegationOptsNodeRef, trusteeNodeRef, ASSOC_DELEGATION_OPTS_TRUSTEE);
				}
			}
		}
		//пробегаемся по всем доверенностям у которых есть trustee и проставляем им active=true, типа доверенности настроены
		List<NodeRef> procuracies = getProcuracies(delegationOptsNodeRef, false);
		for (NodeRef procuracy : procuracies) {
			NodeRef trustee = findNodeByAssociationRef(procuracy, ASSOC_PROCURACY_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			nodeService.setProperty(procuracy, IS_ACTIVE, trustee != null);
		}
		logSaveDelegationOpts(delegationOptsNodeRef);
		return "";
	}

	@Override
	public void deleteProcuracies(final JSONArray nodeRefs) {
		for (int i = 0; i < nodeRefs.length(); ++i) {
			NodeRef nodeRef = null;
			try {
				nodeRef = new NodeRef(nodeRefs.getJSONObject(i).getString("nodeRef"));
			} catch (JSONException ex) {
				logger.error(ex.getMessage(), ex);
			}
			if (nodeRef != null && nodeService.exists(nodeRef)) {
				nodeService.setProperty(nodeRef, IS_ACTIVE, false);
				List<AssociationRef> associationRefs = nodeService.getTargetAssocs(nodeRef, ASSOC_PROCURACY_TRUSTEE);
				if (associationRefs != null) {
					for (AssociationRef associationRef : associationRefs) {
						nodeService.removeAssociation(nodeRef, associationRef.getTargetRef(), ASSOC_PROCURACY_TRUSTEE);
					}
				}
			}
		}
	}

	private void logStartDelegation(final NodeRef delegationOptsRef) {
		final NodeRef initiator = null; //инициатор события это система

		NodeRef mainObject = findNodeByAssociationRef(delegationOptsRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);

		Set<NodeRef> receivers = new HashSet<>();

		NodeRef optsTrusteeRef = findNodeByAssociationRef(delegationOptsRef, ASSOC_DELEGATION_OPTS_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		if (optsTrusteeRef != null) {
			receivers.add(optsTrusteeRef);
			List<String> objects = new ArrayList<>();
			objects.add((String) nodeService.getProperty(optsTrusteeRef, ContentModel.PROP_NAME));
			String template = "Сотруднику #object1 делегированы все полномочия сотрудника #mainobject";
			businessJournalService.log(initiator, mainObject, DelegationEventCategory.START_DELEGATE_ALL, template, objects);
			//а логгировать ли что были переданы права руководителя???
		} else {
			logger.warn("There is no trustee");
		}

		String template = "Сотруднику #object1 делегированы полномочия сотрудника #mainobject в рамках бизнес роли #object2";
		//получить список активных доверенностей и для каждой залоггировать
		List<NodeRef> procuracyRefs = getProcuracies(delegationOptsRef, true);
		for (NodeRef procuracyRef : procuracyRefs) {
			List<String> objects = new ArrayList<>();
			NodeRef procTrusteeRef = findNodeByAssociationRef(procuracyRef, ASSOC_PROCURACY_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			NodeRef businessRoleRef = findNodeByAssociationRef(procuracyRef, ASSOC_PROCURACY_BUSINESS_ROLE, OrgstructureBean.TYPE_BUSINESS_ROLE, ASSOCIATION_TYPE.TARGET);
			if (procTrusteeRef != null) {
				receivers.add(procTrusteeRef);
				objects.add((String) nodeService.getProperty(procTrusteeRef, ContentModel.PROP_NAME));
				objects.add((String) nodeService.getProperty(businessRoleRef, ContentModel.PROP_NAME));
				businessJournalService.log(initiator, mainObject, DelegationEventCategory.START_DELEGATE, template, objects);
				//а логгировать ли что были переданы права руководителя?
			} else {
				logger.warn("There is no trustee for business role {}", nodeService.getProperty(businessRoleRef, ContentModel.PROP_NAME));
			}
		}

		if (!receivers.isEmpty()) {
			notificationsService.sendNotificationByTemplate(mainObject, new ArrayList<>(receivers), "DELEGATION_DELEGATE_TRUSTEE_START");
		}
	}

	private void logStopDelegation(final NodeRef delegationOptsRef) {
		final NodeRef initiator = null; //инициатор события это система
		NodeRef mainObject = findNodeByAssociationRef(delegationOptsRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		String template = "Делегирование полномочий сотрудника #mainobject прекращено";
		businessJournalService.log(initiator, mainObject, DelegationEventCategory.STOP_DELEGATE, template, null);

		Set<NodeRef> receivers = new HashSet<>();

		NodeRef optsTrusteeRef = findNodeByAssociationRef(delegationOptsRef, ASSOC_DELEGATION_OPTS_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		if (optsTrusteeRef != null) {
			receivers.add(optsTrusteeRef);
		}
		//получить список активных доверенностей и для каждой залоггировать
		List<NodeRef> procuracyRefs = getProcuracies(delegationOptsRef, true);
		for (NodeRef procuracyRef : procuracyRefs) {
			NodeRef procTrusteeRef = findNodeByAssociationRef(procuracyRef, ASSOC_PROCURACY_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			if (procTrusteeRef != null) {
				receivers.add(procTrusteeRef);
			}
		}

		if (!receivers.isEmpty()) {
			notificationsService.sendNotificationByTemplate(mainObject, new ArrayList<>(receivers), "DELEGATION_DELEGATE_TRUSTEE_STOP");
		}
	}

	@Override
	public void startDelegation(final String delegator) {
		if (personService.personExists(delegator)) {
			startDelegation(personService.getPerson(delegator, false));
		} else {
			logger.warn(String.format("there is no any person with specified login '%s'", delegator));
		}
	}

	/**
	 * служебный метод делегирования, нарезает или отбирает права с помощью OrgstructureSGNotifierBean
	 *
	 * @param delegationOptsRef ссылка на параметры делегирования
	 * @param created true - права нарезаются, false - права отбираются
	 */
	private void delegate(final NodeRef delegationOptsRef, final boolean created) {
		List<NodeRef> procuracies = getProcuracies(delegationOptsRef, true);

		NodeRef sourceEmployee = findNodeByAssociationRef(delegationOptsRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		String sourceEmployeeName = (String) nodeService.getProperty(sourceEmployee, ContentModel.PROP_NAME);
		for (NodeRef procuracyRef : procuracies) {
			NodeRef brole = findNodeByAssociationRef(procuracyRef, ASSOC_PROCURACY_BUSINESS_ROLE, OrgstructureBean.TYPE_BUSINESS_ROLE, ASSOCIATION_TYPE.TARGET);
			String broleId = (String) nodeService.getProperty(brole, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
			boolean isDynamicBrole = Boolean.valueOf(nodeService.getProperty(brole, OrgstructureBean.PROP_BUSINESS_ROLE_IS_DYNAMIC).toString());
			NodeRef destEmployee = findNodeByAssociationRef(procuracyRef, ASSOC_PROCURACY_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			//на всякий случай проверим, что делегирующий все еще имеет эту бизнес роль
			//пока проверим без учета делегирования
			if (orgstructureService.isEmployeeHasBusinessRole(sourceEmployee, broleId, false) || isDynamicBrole) {
				if (destEmployee != null) {
					sgNotifierService.notifyBRDelegationChanged(brole, sourceEmployee, destEmployee, created);
				} else {
					logger.warn("dest employee is null, no security groups changed");
				}
			} else {
				logger.warn("source employee {} does not have specified role with id {}", sourceEmployeeName, broleId);
			}
		}

		//полный делегат
		NodeRef bossAssistant = findNodeByAssociationRef(delegationOptsRef, ASSOC_DELEGATION_OPTS_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);

		if (bossAssistant != null) {
			if (orgstructureService.isBoss(sourceEmployee, false)) { // Если делегирующий Начальник, то включаем делегируемого в SV
				sgNotifierService.notifyBossDelegationChanged(sourceEmployee, bossAssistant, created);
				//группу SG_SECRETARY пользователя bossAssistant надо включить в SG_SV группы пользователя sourceEmployee
				secretarySecurityService.notifyChiefDelegationChanged(sourceEmployee, bossAssistant, created);
			}
			final List<NodeRef> businessRolesBySourceEmployee = this.getUniqueBusinessRolesByEmployee(sourceEmployee, true);
			for (NodeRef sourceEmployeeBusinessRole : businessRolesBySourceEmployee) {
				sgNotifierService.notifyBRDelegationChanged(sourceEmployeeBusinessRole, sourceEmployee, bossAssistant, created);
			}

			if (created) {
				delegateTasks(sourceEmployee, bossAssistant);
			}

			//Предаем права на группу "Участник"
			LecmPermissionService.LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(READER_PERMISSION_GROUP);
			sgNotifierService.notifySpecDelegationChanged(pgGranting, sourceEmployee, bossAssistant, created);

	} else {
			logger.warn("boss assistant is null, no security groups changed");
		}
	}

	@Override
	public void startDelegation(final NodeRef delegator) {
		NodeRef delegationOptsRef = getDelegationOpts(delegator);
		if (delegationOptsRef != null) {
			nodeService.setProperty(delegationOptsRef, IS_ACTIVE, true);
			logStartDelegation(delegationOptsRef);
			//нарезка прав согласно сервису Руслана
			delegate(delegationOptsRef, true);
		} else {
			logger.warn(String.format("there is no any delegation-opts for NodeRef '%s'", delegator));
		}
	}

	@Override
	public void stopDelegation(final String delegator) {
		if (personService.personExists(delegator)) {
			stopDelegation(personService.getPerson(delegator, false));
		} else {
			logger.warn(String.format("there is no any person with specified login '%s'", delegator));
		}
	}

	@Override
	public void stopDelegation(final NodeRef delegator) {
		NodeRef delegationOptsRef = getDelegationOpts(delegator);
		if (delegationOptsRef != null) {
			//отбирание ранее нарезанных прав согласно сервису Руслана
			delegate(delegationOptsRef, false);
			nodeService.setProperty(delegationOptsRef, IS_ACTIVE, false);
			logStopDelegation(delegationOptsRef);
		} else {
			logger.warn(String.format("there is no any delegation-opts for NodeRef '%s'", delegator));
		}
	}

	@Override
	public boolean hasSubordinate(final NodeRef nodeRef) {
		boolean result = false;
		if (nodeService.exists(nodeRef)) {
			NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
			NodeRef subordinateEmployee = null;
			if (isDelegationOpts(nodeRef)) {
				subordinateEmployee = findNodeByAssociationRef(nodeRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			} else if (orgstructureService.isEmployee(nodeRef)) {
				subordinateEmployee = nodeRef;
			} else if (isProperType(nodeRef, ContentModel.TYPE_PERSON)) {
				subordinateEmployee = orgstructureService.getEmployeeByPerson(nodeRef);
			} else {
				QName nodeType = nodeService.getType(nodeRef);
				logger.warn(String.format("NodeRef {%s}%s can't have subordinate", nodeType, nodeRef));
			}
			if (currentEmployee != null && subordinateEmployee != null) {
				result = orgstructureService.hasSubordinate(currentEmployee, subordinateEmployee);
			}
		} else {
			logger.warn(String.format("Node %s does not exist", nodeRef));
		}
		return result;
	}

	@Override
	public boolean isDelegationActive(final NodeRef delegationOptsNodeRef) {
		if (null == delegationOptsNodeRef) {
			return false;
		} else {
			return (Boolean) nodeService.getProperty(delegationOptsNodeRef, IS_ACTIVE);
		}
	}

	@Override
	public boolean isDelegationOpts(final NodeRef objectNodeRef) {
		return isProperType(objectNodeRef, TYPE_DELEGATION_OPTS);
	}

	@Override
	public boolean isProcuracy(final NodeRef objectNodeRef) {
		return isProperType(objectNodeRef, TYPE_PROCURACY);
	}

	@Override
	public NodeRef getEmployee(final NodeRef nodeRef) {
		NodeRef employeeRef = null;
		if (isDelegationOpts(nodeRef)) {
			employeeRef = findNodeByAssociationRef(nodeRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET);
		} else if (orgstructureService.isEmployee(nodeRef)) {
			employeeRef = nodeRef;
		} else if (isProperType(nodeRef, ContentModel.TYPE_PERSON)) {
			employeeRef = orgstructureService.getEmployeeByPerson(nodeRef);
		} else {
			QName nodeType = nodeService.getType(nodeRef);
			logger.warn(String.format("NodeRef {%s}%s can't have employee.", nodeType, nodeRef));
		}
		return employeeRef;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getDelegationFolder();
	}

	@Override
	public Map<NodeRef, NodeRef> getBusinessRoleToTrusteeByDelegationOpts(final NodeRef delegationOpts, final boolean activeOnly) {
		Map<NodeRef, NodeRef> result = new HashMap<>();
		List<NodeRef> procuracies = getProcuracies(delegationOpts, activeOnly);
		if (procuracies == null || procuracies.isEmpty()) {
			return result;
		}
		for (NodeRef procuracy : procuracies) {
			NodeRef trustee = getTrusteeByProcuracy(procuracy);
			NodeRef businessRole = getBusinessRoleByProcuracy(procuracy);
			result.put(businessRole, trustee);
		}
		return result;
	}

	@Override
	public NodeRef getTrusteeByProcuracy(final NodeRef procuracy) {
		return findNodeByAssociationRef(procuracy, ASSOC_PROCURACY_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
	}

	@Override
	public NodeRef getBusinessRoleByProcuracy(final NodeRef procuracy) {
		return findNodeByAssociationRef(procuracy, ASSOC_PROCURACY_BUSINESS_ROLE, OrgstructureBean.TYPE_BUSINESS_ROLE, ASSOCIATION_TYPE.TARGET);
	}

	@Override
	public NodeRef getEffectiveExecutor(final NodeRef assumedExecutor) {
		return getEffectiveExecutor(assumedExecutor, BUSINESS_ROLE_OTHER_DESIGNATIONS);
	}

	@Override
	public NodeRef getEffectiveExecutor(final NodeRef assumedExecutor, final String businessRoleStr) {
		boolean employeeAbsentToday = absenceService.isEmployeeAbsentToday(assumedExecutor);

		if (!employeeAbsentToday) {
			return assumedExecutor;
		}

		NodeRef delegationOpts = getDelegationOpts(assumedExecutor);
		if (delegationOpts == null) {
			return assumedExecutor;
		}
		Map<NodeRef, NodeRef> businessRoleToTrustee = getBusinessRoleToTrusteeByDelegationOpts(delegationOpts, true);
		if (businessRoleToTrustee.isEmpty()) {
			return assumedExecutor;
		}

		NodeRef businessRoleNodeRef = orgstructureService.getBusinessRoleByIdentifier(businessRoleStr);
		NodeRef result;
		if (businessRoleToTrustee.containsKey(businessRoleNodeRef) && businessRoleToTrustee.get(businessRoleNodeRef) != null) {
			result = businessRoleToTrustee.get(businessRoleNodeRef);
		} else if (!BUSINESS_ROLE_OTHER_DESIGNATIONS.equals(businessRoleStr)) {
			NodeRef businessRoleOtherDesignations = orgstructureService.getBusinessRoleByIdentifier(BUSINESS_ROLE_OTHER_DESIGNATIONS);

			if (businessRoleToTrustee.containsKey(businessRoleOtherDesignations) && businessRoleToTrustee.get(businessRoleOtherDesignations) != null) {
				result = businessRoleToTrustee.get(businessRoleOtherDesignations);
			} else {
				result = assumedExecutor;
			}
		} else {
			result = assumedExecutor;
		}

		return result;
	}

	@Override
	public NodeRef assignTaskToEffectiveExecutor(final NodeRef assumedExecutor, final String businessRole, final String taskID) {
		NodeRef result = null;
		try {
			NodeRef effectiveExecutor = getEffectiveExecutor(assumedExecutor, businessRole);

			if (!assumedExecutor.equals(effectiveExecutor)) {
				createTaskDelegationItem(assumedExecutor, effectiveExecutor, taskID);
			}

			assignTaskToEmployee(taskID, effectiveExecutor);

			sendNewTaskNotification(effectiveExecutor, taskID);
			result = effectiveExecutor;
		} catch (Exception ex) {
			logger.error("Error assigning task", ex);
		}
		return result;
	}

	@Override
	public NodeRef getTasksDelegationFolder() {
		return getFolder(TASKS_DELEGATION_FOLDER);
	}

	private NodeRef createTaskDelegationItem(final NodeRef assumedExecutor, final NodeRef effectiveExecutor, final String taskID) {
		String itemName = taskID + "-" + UUID.randomUUID().toString();
		QName assocQName = QName.createQName(DELEGATION_NAMESPACE, itemName);
		Map<QName, Serializable> properties = new HashMap<>();
		properties.put(ContentModel.PROP_NAME, itemName);
		properties.put(PROP_TASK_DELEGATION_TASK_ID, taskID);
		ChildAssociationRef taskDelegationItemChildAssoc = nodeService.createNode(getTasksDelegationFolder(), ContentModel.ASSOC_CONTAINS, assocQName, TYPE_TASK_DELEGATION, properties);
		if (taskDelegationItemChildAssoc != null) {
			NodeRef taskDelegationItem = taskDelegationItemChildAssoc.getChildRef();
			nodeService.createAssociation(taskDelegationItem, assumedExecutor, ASSOC_TASK_DELEGATION_ASSUMED_EXECUTOR);
			nodeService.createAssociation(taskDelegationItem, effectiveExecutor, ASSOC_TASK_DELEGATION_EFFECTIVE_EXECUTOR);
			return taskDelegationItem;
		} else {
			return null;
		}
	}

	@Override
	public List<NodeRef> getDelegatedTasksForAssumedExecutor(final NodeRef assumedExecutor, final boolean activeOnly) {
		List<NodeRef> result = new ArrayList<>();
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(getTasksDelegationFolder(), ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		for (ChildAssociationRef childAssoc : childAssocs) {
			NodeRef delegatedTask = childAssoc.getChildRef();
			if (!isArchive(delegatedTask)) {
				if (assumedExecutor.equals(getAssumedExecutorByDelegatedTask(delegatedTask))) {
					if (activeOnly) {
						String taskID = getTaskIDByDelegatedTask(delegatedTask);
						WorkflowTask task = workflowService.getTaskById(taskID);
						if (task != null) {
							Map<QName, Serializable> properties = task.getProperties();
							QName bpmStatus = QName.createQName(NamespaceService.BPM_MODEL_1_0_URI, "status");
							String taskStatus = (String) properties.get(bpmStatus);
							if ("Not Yet Started".equals(taskStatus) || "In Progress".equals(taskStatus) || "On Hold".equals(taskStatus)) {
								result.add(delegatedTask);
							}
						} else {
							logger.error("Task is null.");
						}
					} else {
						result.add(delegatedTask);
					}
				}
			}
		}

		return result;
	}

	@Override
	@Deprecated
	public NodeRef reassignTaskBackToAssumedExecutor(final NodeRef delegatedTask) {
		NodeRef result = null;
		try {
			NodeRef assumedExecutor = getAssumedExecutorByDelegatedTask(delegatedTask);
			NodeRef effectiveExecutor = getEffectiveExecutorByDelegatedTask(delegatedTask);
			String taskID = getTaskIDByDelegatedTask(delegatedTask);
			revokeTaskPermissions(taskID, effectiveExecutor, REVIEWER_PERMISSION_GROUP);
			grantTaskPermissions(taskID, delegatedTask, READER_PERMISSION_GROUP);
			assignTaskToEmployee(taskID, assumedExecutor);
			Map<QName, Serializable> properties = new HashMap<>();
			properties.put(IS_ACTIVE, false);
			nodeService.addAspect(delegatedTask, ASPECT_ACTIVE, properties);

			sendNewTaskNotification(assumedExecutor, taskID);
			result = assumedExecutor;
		} catch (Exception ex) {
			logger.error("Error reassigning task", ex);
		}
		return result;
	}

	private NodeRef getAssumedExecutorByDelegatedTask(final NodeRef delegatedTask) {
		return findNodeByAssociationRef(delegatedTask, ASSOC_TASK_DELEGATION_ASSUMED_EXECUTOR, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET);
	}

	private NodeRef getEffectiveExecutorByDelegatedTask(final NodeRef delegatedTask) {
		return findNodeByAssociationRef(delegatedTask, ASSOC_TASK_DELEGATION_EFFECTIVE_EXECUTOR, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET);
	}

	private String getTaskIDByDelegatedTask(final NodeRef delegatedTask) {
		return (String) nodeService.getProperty(delegatedTask, PROP_TASK_DELEGATION_TASK_ID);
	}

	@Deprecated
	private void assignTaskToEmployee(String taskID, NodeRef employeeRef) throws WriteTransactionNeededException {
		Map<QName, Serializable> properties = new HashMap<>();
		NodeRef personForEmployee = orgstructureService.getPersonForEmployee(employeeRef);
		String userName = (String) nodeService.getProperty(personForEmployee, ContentModel.PROP_USERNAME);
		properties.put(ContentModel.PROP_OWNER, userName);
		workflowService.updateTask(taskID, properties, null, null);
		grantTaskPermissions(taskID, employeeRef, REVIEWER_PERMISSION_GROUP);
	}

	private void sendNewTaskNotification(final NodeRef employeeRef, final String taskID) {
		String url = getUrlService().getLinkWithContext("/page/task-edit?taskId=" + taskID);
		Map<String, Object> templateObjects = new HashMap<>();
		templateObjects.put("url", url);

		notificationsService.sendNotificationByTemplate(employeeRef, Collections.singletonList(employeeRef), "DELEGATION_NEW_TASK", templateObjects);
	}

	private void grantTaskPermissions(String taskID, NodeRef employeeNodeRef, String permissionGroup) throws WriteTransactionNeededException {
		List<NodeRef> packageContents = workflowService.getPackageContents(taskID);
		for (NodeRef node : packageContents) {
			if (dictionaryServiceAlfresco.isSubClass(nodeService.getType(node), DocumentService.TYPE_BASE_DOCUMENT)) {
				NodeRef member = documentMembersService.addMemberWithoutCheckPermission(node, employeeNodeRef, permissionGroup);
				if (member == null) {
					LecmPermissionService.LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(permissionGroup);
					lecmPermissionService.grantAccess(pgGranting, node, employeeNodeRef);
				}
			}
		}
	}

	private void revokeTaskPermissions(String taskID, NodeRef employeeNodeRef, String permissionGroup) {
		List<NodeRef> packageContents = workflowService.getPackageContents(taskID);
		for (NodeRef node : packageContents) {
			if (dictionaryServiceAlfresco.isSubClass(nodeService.getType(node), DocumentService.TYPE_BASE_DOCUMENT)) {
				LecmPermissionService.LecmPermissionGroup pgRevoking = lecmPermissionService.findPermissionGroup(permissionGroup);
				lecmPermissionService.revokeAccess(pgRevoking, node, employeeNodeRef);

			}
		}
	}

	private boolean isEmployeeOwnsTask(NodeRef employee, String taskID) {
		WorkflowTask task = workflowService.getTaskById(taskID);
		Map<QName, Serializable> taskProperties = task.getProperties();
		String taskOwner = (String) taskProperties.get(ContentModel.PROP_OWNER);

		NodeRef personForEmployee = orgstructureService.getPersonForEmployee(employee);
		String employeeUserName = (String) nodeService.getProperty(personForEmployee, ContentModel.PROP_USERNAME);

		return taskOwner.equalsIgnoreCase(employeeUserName);
	}

	@Override
	public NodeRef getGlobalSettingsNode() {
		return nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, DELEGATION_SETTINGS_NODE_NAME);
	}

	private NodeRef createGlobalSettingsNode() throws WriteTransactionNeededException {
		final NodeRef rootFolder = this.getServiceRootFolder();
		final String settingsObjectName = DELEGATION_SETTINGS_NODE_NAME;
		return createNode(rootFolder, TYPE_DELEGATION_GLOBAL_SETTINGS, settingsObjectName, null);
	}

	@Override
	public Set<NodeRef> getDeletionOwnerEmployees(NodeRef employee, Set<String> roles) {
		Set<NodeRef> results = new HashSet<>();
		if (roles != null) {
			List<NodeRef> allInitiatorBusinesRoles = orgstructureService.getEmployeeRoles(employee, false, true);
			for (NodeRef roleRef : allInitiatorBusinesRoles) {
				String roleIdentifier = orgstructureService.getBusinessRoleIdentifier(roleRef);
				if (roleIdentifier != null && roles.contains(roleIdentifier)) {
					return new HashSet<>();
				}
			}

			List<NodeRef> delegationOptsList = findNodesByAssociationRef(employee, IDelegation.ASSOC_DELEGATION_OPTS_TRUSTEE, IDelegation.TYPE_DELEGATION_OPTS, ASSOCIATION_TYPE.SOURCE);
			if (delegationOptsList != null) {
				for (NodeRef delegationOpts : delegationOptsList) {
					if (!isArchive(delegationOpts)) {
						NodeRef owner = findNodeByAssociationRef(delegationOpts, IDelegation.ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
						if (owner != null) {
							results.add(owner);
						}
					}
				}
			}

			List<NodeRef> procuracies = findNodesByAssociationRef(employee, IDelegation.ASSOC_PROCURACY_TRUSTEE, IDelegation.TYPE_PROCURACY, ASSOCIATION_TYPE.SOURCE);
			if (procuracies != null) {
				for (NodeRef procuracy : procuracies) {
					if (!isArchive(procuracy)) {
						NodeRef roleRef = findNodeByAssociationRef(procuracy, IDelegation.ASSOC_PROCURACY_BUSINESS_ROLE, OrgstructureBean.TYPE_BUSINESS_ROLE, ASSOCIATION_TYPE.TARGET);
						String role = orgstructureService.getBusinessRoleIdentifier(roleRef);
						if (role != null && roles.contains(role)) {
							ChildAssociationRef parent = nodeService.getPrimaryParent(procuracy);
							if (parent != null && isDelegationOpts(parent.getParentRef()) && !isArchive(parent.getParentRef())) {
								NodeRef owner = findNodeByAssociationRef(parent.getParentRef(), IDelegation.ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
								if (owner != null) {
									results.add(owner);
								}
							}
						}
					}
				}
			}
		}
		return results;
	}

	@Override
	public boolean getCreateDocumentDelegationSetting() {
		NodeRef globalSettingsNode = getGlobalSettingsNode();
		if (globalSettingsNode != null) {
			return (Boolean) nodeService.getProperty(globalSettingsNode, PROP_CREATE_DOCUMENT_DELEGATION_SETTING);
		} else {
			return false;
		}
	}

	public void delegateTasks(NodeRef sourceEmployeeRef, NodeRef destEmployee) {
		String destUserName = orgstructureService.getEmployeeLogin(destEmployee);
		if (destUserName != null) {
			List<WorkflowTask> tasks = getActiveEmployeeTasks(sourceEmployeeRef);
			if (tasks != null && tasks.size() > 0) {
				for (WorkflowTask task : tasks) {
					Map<QName, Serializable> properties = new HashMap<>();
					properties.put(ContentModel.PROP_OWNER, destUserName);
					workflowService.updateTask(task.getId(), properties, null, null);
				}

				String url = getUrlService().getLinkWithContext("/page/my-profile?path=Мое%20делегирование/Распределение%20задач");
				Map<String, Object> templateObjects = new HashMap<>();
				templateObjects.put("tasksCount", tasks.size());
				templateObjects.put("url", url);
				notificationsService.sendNotificationByTemplate(sourceEmployeeRef, Collections.singletonList(destEmployee), "DELEGATION_DELEGATE_TASKS", templateObjects);
			}
		}
	}

	public List<WorkflowTask> getActiveEmployeeTasks(NodeRef employeeRef) {
		String userName = orgstructureService.getEmployeeLogin(employeeRef);
		if (userName != null) {
			return workflowService.getAssignedTasks(userName, WorkflowTaskState.IN_PROGRESS);
		} else {
			return null;
		}
	}

	@Override
	public NodeRef getEffectiveEmployee(final NodeRef employeeRef, final String workflowDynRole) {
		boolean delegateAll = workflowDynRole == null;
		NodeRef delegationOpts = getDelegationOpts(employeeRef);
		boolean isDelegationActive = isDelegationActive(delegationOpts);
		NodeRef effectiveEmployeeRef = employeeRef;
		if (isDelegationActive) {
			if (delegateAll) {
				effectiveEmployeeRef = findNodeByAssociationRef(delegationOpts, IDelegation.ASSOC_DELEGATION_OPTS_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			} else {
				effectiveEmployeeRef = getEffectiveExecutor(employeeRef, workflowDynRole);
				//если эффективного исполнителя не нашли по бизнес-ролям, то поискать его через параметры делегирования
				if (employeeRef.equals(effectiveEmployeeRef)) {
					effectiveEmployeeRef = findNodeByAssociationRef(delegationOpts, IDelegation.ASSOC_DELEGATION_OPTS_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
				}
			}
		}
		if (effectiveEmployeeRef == null) {
			effectiveEmployeeRef = employeeRef;
		}
		return effectiveEmployeeRef;
	}
}
