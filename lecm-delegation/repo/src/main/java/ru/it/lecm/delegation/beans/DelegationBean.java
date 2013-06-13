package ru.it.lecm.delegation.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.PropertyCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.delegation.DelegationEventCategory;
import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.delegation.IDelegationDescriptor;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.orgstructure.beans.OrgstructureSGNotifierBean;

import java.io.Serializable;
import java.util.*;

public class DelegationBean extends BaseBean implements IDelegation, AuthenticationUtil.RunAsWork<NodeRef>, IDelegationDescriptor {

	final private static Logger logger = LoggerFactory.getLogger (DelegationBean.class);
	private final static String CONTAINER = "DelegationOptionsContainer";
	public final static String DELEGATION_FOLDER = "DELEGATION_FOLDER";

	private OrgstructureBean orgstructureService;
	private PersonService personService;
	private BusinessJournalService businessJournalService;
	private OrgstructureSGNotifierBean sgNotifierService;
    private DictionaryBean dictionaryService;

	public void setOrgstructureService (OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setPersonService (PersonService personService) {
		this.personService = personService;
	}

	public void setBusinessJournalService (BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setSgNotifierService (OrgstructureSGNotifierBean sgNotifierService) {
		this.sgNotifierService = sgNotifierService;
	}

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

	public final void init () {
		PropertyCheck.mandatory (this, "nodeService", nodeService);
		PropertyCheck.mandatory (this, "transactionService", transactionService);
		PropertyCheck.mandatory (this, "personService", personService);
		PropertyCheck.mandatory (this, "businessJournalService", businessJournalService);
		PropertyCheck.mandatory (this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory (this, "sgNotifierService", sgNotifierService);

		//создание контейнера для хранения параметров делегирования
		AuthenticationUtil.runAsSystem (this);

		NodeRef delegationFolderRef = getDelegationFolder ();
		nodeService.getProperty (delegationFolderRef, ContentModel.PROP_NAME);
		logger.debug (String.format ("Delegation service root directory is"));

		//возможно здесь еще будет штука для создания параметров делегирования для уже существующих пользователей
	}

	@Override
	public NodeRef doWork () throws Exception {
		final NodeRef delegationHome = getDelegationFolder ();
		NodeRef container = nodeService.getChildByName (delegationHome, ContentModel.ASSOC_CONTAINS, CONTAINER);
		if (container == null) {
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
			container = transactionHelper.doInTransaction (new RetryingTransactionCallback<NodeRef> () {
				@Override
				public NodeRef execute () throws Throwable {
					NodeRef parentRef = delegationHome; //the parent node
					QName assocTypeQName = ContentModel.ASSOC_CONTAINS; //the type of the association to create. This is used for verification against the data dictionary.
					QName assocQName = QName.createQName (DELEGATION_NAMESPACE, CONTAINER); //the qualified name of the association
					QName nodeTypeQName = TYPE_DELEGATION_OPTS_CONTAINER; //a reference to the node type
					// создание корневого узла для делегирований в Компании ...
					Map<QName, Serializable> properties = new HashMap<QName, Serializable> (1); //optional map of properties to keyed by their qualified names
					properties.put (ContentModel.PROP_NAME, CONTAINER);
					ChildAssociationRef associationRef = nodeService.createNode (parentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
					NodeRef delegationRoot = associationRef.getChildRef ();
					logger.debug (String.format ("container node '%s' created", delegationRoot.toString ()));
					return delegationRoot;
				}
			});
		}
		return container;
	}

	@Override
	public NodeRef getDelegationFolder () {
		return getFolder (DELEGATION_FOLDER);
	}

	@Override
	public NodeRef getDelegationOptsContainer () {
		NodeRef delegationOptsContainer = null;
		try {
			delegationOptsContainer = doWork ();
		} catch (Exception ex) {
			logger.warn (ex.getMessage (), ex);
		}
		return delegationOptsContainer;
	}

	@Override
	public QName getDelegationOptsItemType () {
		return TYPE_DELEGATION_OPTS;
	}

	@Override
	public IDelegationDescriptor getDelegationDescriptor () {
		return this;
	}

	@Override
	public NodeRef getOrCreateDelegationOpts (final NodeRef employeeNodeRef) {
		//делаем поиск по всем delegation-opts, если не нашли то создаем новую
		NodeRef delegationOptsNodeRef = findNodeByAssociationRef (employeeNodeRef, ASSOC_DELEGATION_OPTS_OWNER, TYPE_DELEGATION_OPTS, ASSOCIATION_TYPE.SOURCE);

		//создаем новый delegation-opts так как его нет
		if (delegationOptsNodeRef == null) {
			delegationOptsNodeRef = createDelegationOpts (employeeNodeRef);
		}
		return delegationOptsNodeRef;
	}

	private NodeRef createDelegationOpts (final NodeRef employeeNodeRef) {
		return AuthenticationUtil.runAsSystem (new RunAsWork<NodeRef> () {
			@Override
			public NodeRef doWork () throws Exception {
				Serializable employeeName = nodeService.getProperty (employeeNodeRef, ContentModel.PROP_NAME);
				String delegationOptsName = String.format ("параметры делегирования для %s", employeeName);
				//создание ноды и установка ей ассоциации
				NodeRef parentRef = getDelegationOptsContainer (); //the parent node
				QName assocTypeQName = ASSOC_DELEGATION_OPTS_CONTAINER; //the type of the association to create. This is used for verification against the data dictionary.
				QName assocQName = QName.createQName (DELEGATION_NAMESPACE, delegationOptsName); //the qualified name of the association
				QName nodeTypeQName = TYPE_DELEGATION_OPTS; //a reference to the node type
				Map<QName, Serializable> properties = new HashMap<QName, Serializable> (); //optional map of properties to keyed by their qualified names
				properties.put (ContentModel.PROP_NAME, delegationOptsName);
				properties.put (IS_ACTIVE, false);//параметры делегирования по умолчанию создаем неактивными
				NodeRef delegationOptsNodeRef = nodeService.createNode (parentRef, assocTypeQName, assocQName, nodeTypeQName, properties).getChildRef ();
				nodeService.createAssociation (delegationOptsNodeRef, employeeNodeRef, ASSOC_DELEGATION_OPTS_OWNER);
				return delegationOptsNodeRef;
			}
		});
	}

	@Override
	public NodeRef getDelegationOpts (final NodeRef nodeRef) {
		NodeRef delefationOptsRef = null;
		if (isDelegationOpts (nodeRef)) {
			delefationOptsRef = nodeRef;
		} else if (orgstructureService.isEmployee (nodeRef)) {
			delefationOptsRef = getOrCreateDelegationOpts (nodeRef);
		} else if (isProperType (nodeRef, ContentModel.TYPE_PERSON)) {
			NodeRef employeeRef = orgstructureService.getEmployeeByPerson (nodeRef);
			if (employeeRef != null) {
				delefationOptsRef = getOrCreateDelegationOpts (employeeRef);
			} else {
				String fistname = (String) nodeService.getProperty (nodeRef, ContentModel.PROP_FIRSTNAME);
				String lastname = (String) nodeService.getProperty (nodeRef, ContentModel.PROP_LASTNAME);
				String username = (String) nodeService.getProperty (nodeRef, ContentModel.PROP_USERNAME);
				logger.warn (String.format ("Alfresco user %s (%s %s) does not mapped to lecm-orgstr:employee", username, fistname, lastname));
			}
		} else {
			QName nodeType = nodeService.getType (nodeRef);
			logger.warn (String.format ("NodeRef {%s}%s can't have delegation options.", nodeType, nodeRef));
		}
		return delefationOptsRef;
	}

	@Override
	public List<NodeRef> getUniqueBusinessRolesByEmployee (final NodeRef employeeNodeRef, final boolean onlyActive) {
		//получаем все бизнес роли
		Set<NodeRef> uniqueBusinessRoleNodeRefs = new HashSet<NodeRef> (orgstructureService.getEmployeeRoles(employeeNodeRef));
        final List<NodeRef> dynamicBusinessRoles = dictionaryService.getRecordsByParamValue(OrgstructureBean.BUSINESS_ROLES_DICTIONARY_NAME, OrgstructureBean.PROP_BUSINESS_ROLE_IS_DYNAMIC, true);
        for (NodeRef businessRole : dynamicBusinessRoles) {
            if (!isArchive(businessRole)) {
                uniqueBusinessRoleNodeRefs.add(businessRole);
            }
        }

        return new ArrayList<NodeRef>(uniqueBusinessRoleNodeRefs);
	}

	@Override
	public List<NodeRef> getUniqueBusinessRolesByPerson (NodeRef personNodeRef, final boolean onlyActive) {
		NodeRef employeeNodeRef = findNodeByAssociationRef (personNodeRef, OrgstructureBean.ASSOC_EMPLOYEE_PERSON, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.SOURCE);
		return getUniqueBusinessRolesByEmployee (employeeNodeRef, onlyActive);
	}

	@Override
	public List<NodeRef> getUniqueBusinessRolesByDelegationOpts (NodeRef delegationOptsNodeRef, final boolean onlyActive) {
		NodeRef employeeNodeRef = findNodeByAssociationRef (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		return getUniqueBusinessRolesByEmployee (employeeNodeRef, onlyActive);
	}

	@Override
	public List<NodeRef> getProcuracies (final NodeRef nodeRef, final boolean onlyActive) {
		NodeRef delegationOptsNodeRef = getDelegationOpts (nodeRef);
		List<NodeRef> procuracyNodeRefs = new ArrayList<NodeRef> ();
		if (delegationOptsNodeRef != null) {
			List<ChildAssociationRef> childAssociationRefs = nodeService.getChildAssocs (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_PROCURACY, RegexQNamePattern.MATCH_ALL);
			if (childAssociationRefs != null) {
				for (ChildAssociationRef childAssociationRef : childAssociationRefs) {
					NodeRef procuracyNodeRef = childAssociationRef.getChildRef ();
					//по идее еще и бизнес роль на активность надо проверять
					NodeRef businessRoleRef = findNodeByAssociationRef (procuracyNodeRef, ASSOC_PROCURACY_BUSINESS_ROLE, OrgstructureBean.TYPE_BUSINESS_ROLE, ASSOCIATION_TYPE.TARGET);
					Boolean hasActiveBusinessRole;
					if (businessRoleRef != null) {
						hasActiveBusinessRole = (Boolean) nodeService.getProperty (businessRoleRef, IS_ACTIVE);
					} else {
						hasActiveBusinessRole = true;
					}

					if (hasActiveBusinessRole && (!onlyActive || !isArchive (procuracyNodeRef))) {
						procuracyNodeRefs.add (procuracyNodeRef);
					} else if (hasActiveBusinessRole && (Boolean) nodeService.getProperty (procuracyNodeRef, IS_ACTIVE)) {
						procuracyNodeRefs.add (procuracyNodeRef);
					}
				}
			}
		}
		return procuracyNodeRefs;
	}

	@Override
	public List<NodeRef> createEmptyProcuracies (final NodeRef delegationOptsNodeRef, final List<NodeRef> businessRoleNodeRefs) {
		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
		return transactionHelper.doInTransaction (new RetryingTransactionCallback<List<NodeRef>> () {

			@Override
			public List<NodeRef> execute () throws Throwable {
				List<NodeRef> procuracyNodeRefs = new ArrayList<NodeRef> ();
				for (NodeRef businessRoleNodeRef : businessRoleNodeRefs) {
					NodeRef parentRef = delegationOptsNodeRef; //the parent node
					QName assocTypeQName = ASSOC_DELEGATION_OPTS_PROCURACY; //the type of the association to create. This is used for verification against the data dictionary.
					QName assocQName = QName.createQName (DELEGATION_NAMESPACE, "доверенность_" + UUID.randomUUID ().toString ()); //the qualified name of the association
					QName nodeTypeQName = TYPE_PROCURACY; //a reference to the node type
					Map<QName, Serializable> properties = new HashMap<QName, Serializable> (1); //optional map of properties to keyed by their qualified names
					properties.put (IS_ACTIVE, false);
					ChildAssociationRef associationRef = nodeService.createNode (parentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
					NodeRef procuracyNodeRef = associationRef.getChildRef ();
					nodeService.createAssociation (procuracyNodeRef, businessRoleNodeRef, ASSOC_PROCURACY_BUSINESS_ROLE);
					procuracyNodeRefs.add (procuracyNodeRef);
				}
				return procuracyNodeRefs;
			}
		});
	}

	/**
	 * поиск значения проверти или ассоциации в JSON объекте который пришел с формы
	 * @param options JSON объект по которому искать
	 * @param pattern кусочек имени по которому пытаться искать
	 * @return Object если нашел, null в противном случае
	 */
	private <T>T findInOptions (final JSONObject options, final String pattern, final String typeName) {
		Object result = null;
		try {
			Iterator<Object> itr = options.keys ();
			while (itr.hasNext ()) {
				String key = itr.next ().toString ();
				if (key.contains (pattern)) {
					if ("Boolean".equals (typeName)) {
						result = options.getBoolean (key);
					} else if ("Double".equals (typeName)) {
						result = options.getDouble (key);
					} else if ("Integer".equals (typeName)) {
						result = options.getInt (key);
					} else if ("JSONArray".equals (typeName)) {
						result = options.getJSONArray (key);
					} else if ("JSONObject".equals (typeName)) {
						result = options.getJSONObject (key);
					} else if ("Long".equals (typeName)) {
						result = options.getLong (key);
					} else if ("String".equals (typeName)) {
						result = options.getString (key);
					} else {
						result = options.get (key);
					}
				}
			}
		} catch (JSONException ex) {
			logger.error (ex.getMessage (), ex);
		}
		return (T) result;
	}

	private void logSaveDelegationOpts (final NodeRef delegationOptsRef) {
		final NodeRef initiator = orgstructureService.getPersonForEmployee (orgstructureService.getCurrentEmployee ());
		NodeRef mainObject = findNodeByAssociationRef (delegationOptsRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		String template = "Сотрудник #mainobject изменил параметры делегирования";
		businessJournalService.log (initiator, mainObject, DelegationEventCategory.CHANGE_DELEGATION_OPTS, template, null);
	}

	@Override
	public String saveDelegationOpts (final NodeRef delegationOptsNodeRef, final JSONObject options) {

        //получаем ссылку на доверенное лицо из options
        //String propTrustee = ASSOC_DELEGATION_OPTS_TRUSTEE.getLocalName ();
        //String propTrustee = "assoc_lecm-d8n_delegation-opts-trustee-assoc";
        String propTrustee = "delegation-opts-part1_assoc_lecm-d8n_delegation-opts-trustee-assoc-cntrl-selectedItems";
        String trusteeRef = findInOptions (options, propTrustee, "String");
        //String trusteeRef = findInOptions (options, propTrustee + "_added", "String");
        if (trusteeRef != null) {
            //переназначем ассоциацию на доверенное лицо
            List<NodeRef> trusteeRefs = NodeRef.getNodeRefs (trusteeRef);
            nodeService.setAssociations (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_TRUSTEE, trusteeRefs);

        } else { //если поле доверенное лицо пустое
			//по ассоциации получаем ссылку на доверенное лицо, если она есть
			List<AssociationRef> targetAssocs = nodeService.getTargetAssocs (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_TRUSTEE);
			//если по ассоциациям кто-то есть, берем его и удаляем.
			if (targetAssocs != null && !targetAssocs.isEmpty ()) {
				for (AssociationRef targetAssoc : targetAssocs) {
					NodeRef trusteeNodeRef = targetAssoc.getTargetRef ();
					nodeService.removeAssociation (delegationOptsNodeRef, trusteeNodeRef, ASSOC_DELEGATION_OPTS_TRUSTEE);
				}
			}
		}
		logSaveDelegationOpts (delegationOptsNodeRef);
		return "";
	}

	@Override
	public void actualizeProcuracyActivity (final NodeRef procuracyNodeRef) {
		List<AssociationRef> associationRefs = nodeService.getTargetAssocs (procuracyNodeRef, ASSOC_PROCURACY_TRUSTEE);
		if (associationRefs == null || associationRefs.isEmpty ()) {
			nodeService.setProperty (procuracyNodeRef, IS_ACTIVE, false);
		} else {
			nodeService.setProperty (procuracyNodeRef, IS_ACTIVE, true);
		}
	}

	@Override
	public void deleteProcuracies (final JSONArray nodeRefs) {
		for (int i = 0; i < nodeRefs.length (); ++i) {
			NodeRef nodeRef = null;
			try {
				nodeRef = new NodeRef (nodeRefs.getJSONObject (i).getString ("nodeRef"));
			} catch (JSONException ex) {
				logger.error (ex.getMessage (), ex);
			}
			if (nodeRef != null && nodeService.exists(nodeRef)) {
				nodeService.setProperty(nodeRef, IS_ACTIVE, false);
				List<AssociationRef> associationRefs = nodeService.getTargetAssocs (nodeRef, ASSOC_PROCURACY_TRUSTEE);
				if (associationRefs != null) {
					for (AssociationRef associationRef : associationRefs) {
						nodeService.removeAssociation (nodeRef, associationRef.getTargetRef (), ASSOC_PROCURACY_TRUSTEE);
					}
				}
			}
		}
	}

	private void logStartDelegation (final NodeRef delegationOptsRef) {
		final NodeRef initiator = null; //инициатор события это система

		NodeRef mainObject = findNodeByAssociationRef (delegationOptsRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);

		NodeRef optsTrusteeRef = findNodeByAssociationRef (delegationOptsRef, ASSOC_DELEGATION_OPTS_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		if (optsTrusteeRef != null) {
			List<String> objects = new ArrayList<String> ();
			objects.add ((String) nodeService.getProperty (optsTrusteeRef, ContentModel.PROP_NAME));
			String template = "Сотруднику #object1 делегированы все полномочия сотрудника #mainobject";
			businessJournalService.log (initiator, mainObject, DelegationEventCategory.START_DELEGATE_ALL, template, objects);
			//а логгировать ли что были переданы права руководителя???
		} else {
			logger.warn ("There is no trustee");
           }

		String template  = "Сотруднику #object1 делегированы полномочия сотрудника #mainobject в рамках бизнес роли #object2";
		//получить список активных доверенностей и для каждой залоггировать
		List<NodeRef> procuracyRefs = getProcuracies (delegationOptsRef, true);
		for (NodeRef procuracyRef : procuracyRefs) {
			List<String> objects = new ArrayList<String> ();
			NodeRef procTrusteeRef = findNodeByAssociationRef (procuracyRef, ASSOC_PROCURACY_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			NodeRef businessRoleRef = findNodeByAssociationRef (procuracyRef, ASSOC_PROCURACY_BUSINESS_ROLE, OrgstructureBean.TYPE_BUSINESS_ROLE, ASSOCIATION_TYPE.TARGET);
			if (procTrusteeRef != null) {
				objects.add ((String) nodeService.getProperty (procTrusteeRef, ContentModel.PROP_NAME));
				objects.add ((String) nodeService.getProperty (businessRoleRef, ContentModel.PROP_NAME));
				businessJournalService.log (initiator, mainObject, DelegationEventCategory.START_DELEGATE, template, objects);
				//а логгировать ли что были переданы права руководителя?
			} else {
				logger.warn ("There is no trustee for business role {}", nodeService.getProperty (businessRoleRef, ContentModel.PROP_NAME));
			}
		}

	}

	private void logStopDelegation (final NodeRef delegationOptsRef) {
		final NodeRef initiator = null; //инициатор события это система
		NodeRef mainObject = findNodeByAssociationRef (delegationOptsRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		String template = "Делегирование полномочий сотрудника #mainobject прекращено";
		businessJournalService.log (initiator, mainObject, DelegationEventCategory.STOP_DELEGATE, template, null);
	}

	@Override
	public void startDelegation (final String delegator) {
		if (personService.personExists (delegator)) {
			startDelegation (personService.getPerson (delegator, false));
		} else {
			logger.warn (String.format ("there is no any person with specified login '%s'", delegator));
		}
	}

	/**
	 * служебный метод делегирования, нарезает или отбирает права с помощью OrgstructureSGNotifierBean
	 * @param delegationOptsRef ссылка на параметры делегирования
	 * @param created true - права нарезаются, false - права отбираются
	 */
	private void delegate (final NodeRef delegationOptsRef, final boolean created) {
		List<NodeRef> procuracies = getProcuracies (delegationOptsRef, true);

		NodeRef sourceEmployee = findNodeByAssociationRef (delegationOptsRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		String sourceEmployeeName = (String) nodeService.getProperty(sourceEmployee, ContentModel.PROP_NAME);
		for (NodeRef procuracyRef : procuracies) {
			NodeRef brole = findNodeByAssociationRef (procuracyRef, ASSOC_PROCURACY_BUSINESS_ROLE, OrgstructureBean.TYPE_BUSINESS_ROLE, ASSOCIATION_TYPE.TARGET);
			String broleId = (String) nodeService.getProperty(brole, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
			NodeRef destEmployee = findNodeByAssociationRef (procuracyRef, ASSOC_PROCURACY_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			//на всякий случай проверим, что делегирующий все еще имеет эту бизнес роль
			//пока проверим без учета делегирования
			if (orgstructureService.isEmployeeHasBusinessRole(sourceEmployee, broleId, false)) {
				if (destEmployee != null) {
					sgNotifierService.notifyBRDelegationChanged (brole, sourceEmployee, destEmployee, created);
				} else {
					logger.warn ("dest employee is null, no security groups changed");
				}
			} else {
				logger.warn ("source employee {} does not have specified role with id {}", sourceEmployeeName, broleId);
			}
		}

        //заместитель
        NodeRef bossAssistant = findNodeByAssociationRef (delegationOptsRef, ASSOC_DELEGATION_OPTS_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);

		if (bossAssistant != null) {
            if (orgstructureService.isBoss(sourceEmployee, false)) { // Если делегирующий Начальник, то включаем делегируемого в SV
                sgNotifierService.notifyBossDelegationChanged (sourceEmployee, bossAssistant, created);
            }
            final List<NodeRef> businessRolesBySourceEmployee = this.getUniqueBusinessRolesByEmployee(sourceEmployee, true);
            for (NodeRef sourceEmployeeBusinessRole : businessRolesBySourceEmployee){
                sgNotifierService.notifyBRDelegationChanged (sourceEmployeeBusinessRole, sourceEmployee, bossAssistant, created);
            }
        } else {
			logger.warn ("boss assistant is null, no security groups changed");
		}
	}

	@Override
	public void startDelegation (final NodeRef delegator) {
		NodeRef delegationOptsRef = getDelegationOpts (delegator);
		if (delegationOptsRef != null) {
			nodeService.setProperty (delegationOptsRef, IS_ACTIVE, true);
			logStartDelegation (delegationOptsRef);
			//нарезка прав согласно сервису Руслана
			delegate (delegationOptsRef, true);
		} else {
			logger.warn (String.format ("there is no any delegation-opts for NodeRef '%s'", delegator));
		}
	}

	@Override
	public void stopDelegation (final String delegator) {
		if (personService.personExists (delegator)) {
			stopDelegation (personService.getPerson (delegator, false));
		} else {
			logger.warn (String.format ("there is no any person with specified login '%s'", delegator));
		}
	}

	@Override
	public void stopDelegation (final NodeRef delegator) {
		NodeRef delegationOptsRef = getDelegationOpts (delegator);
		if (delegationOptsRef != null) {
			nodeService.setProperty (delegationOptsRef, IS_ACTIVE, false);
			logStopDelegation (delegationOptsRef);
			//отбирание ранее нарезанных прав согласно сервису Руслана
			delegate (delegationOptsRef, false);
		} else {
			logger.warn (String.format ("there is no any delegation-opts for NodeRef '%s'", delegator));
		}
	}

	@Override
	public boolean hasSubordinate (final NodeRef nodeRef) {
		boolean result = false;
		if (nodeService.exists (nodeRef)) {
			NodeRef currentEmployee = orgstructureService.getCurrentEmployee ();
			NodeRef subordinateEmployee = null;
			if (isDelegationOpts (nodeRef)) {
				subordinateEmployee = findNodeByAssociationRef (nodeRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			} else if (orgstructureService.isEmployee (nodeRef)) {
				subordinateEmployee = nodeRef;
			} else if (isProperType (nodeRef, ContentModel.TYPE_PERSON)) {
				subordinateEmployee = orgstructureService.getEmployeeByPerson (nodeRef);
			} else {
				QName nodeType = nodeService.getType (nodeRef);
				logger.warn (String.format ("NodeRef {%s}%s can't have subordinate", nodeType, nodeRef));
			}
			if (currentEmployee != null && subordinateEmployee != null) {
				result = orgstructureService.hasSubordinate (currentEmployee, subordinateEmployee);
			}
		} else {
			logger.warn (String.format ("Node %s does not exist", nodeRef));
		}
		return result;
	}

	@Override
	public boolean isDelegationActive (final NodeRef delegationOptsNodeRef) {
		return (Boolean) nodeService.getProperty (delegationOptsNodeRef, IS_ACTIVE);
	}

	@Override
	public boolean isDelegationOpts (final NodeRef objectNodeRef) {
		return isProperType (objectNodeRef, TYPE_DELEGATION_OPTS);
	}

	@Override
	public boolean isProcuracy (final NodeRef objectNodeRef) {
		return isProperType (objectNodeRef, TYPE_PROCURACY);
	}

	@Override
	public NodeRef getEmployee (final NodeRef nodeRef) {
		NodeRef employeeRef = null;
		if (isDelegationOpts (nodeRef)) {
			employeeRef = findNodeByAssociationRef (nodeRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET);
		} else if (orgstructureService.isEmployee (nodeRef)) {
			employeeRef = nodeRef;
		} else if (isProperType (nodeRef, ContentModel.TYPE_PERSON)) {
			employeeRef = orgstructureService.getEmployeeByPerson (nodeRef);
		} else {
			QName nodeType = nodeService.getType (nodeRef);
			logger.warn (String.format ("NodeRef {%s}%s can't have employee.", nodeType, nodeRef));
		}
		return employeeRef;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getDelegationFolder();
	}


}
