package ru.it.lecm.delegation.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.PropertyCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;

import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.delegation.IDelegationDescriptor;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

public class DelegationBean extends BaseBean implements IDelegation, AuthenticationUtil.RunAsWork<NodeRef>, IDelegationDescriptor {

	final private static Logger logger = LoggerFactory.getLogger (DelegationBean.class);

	private Repository repository;
	private OrgstructureBean orgstructureService;

	public void setRepositoryHelper (Repository repository) {
		this.repository = repository;
	}

	public void setOrgstructureService (OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public final void bootstrap () {
		PropertyCheck.mandatory (this, "repository", repository);
		PropertyCheck.mandatory (this, "nodeService", nodeService);
		PropertyCheck.mandatory (this, "transactionService", transactionService);

		repository.init ();
		//создание контейнера для хранения параметров делегирования
		AuthenticationUtil.runAsSystem (this);

		//возможно здесь еще будет штука для создания параметров делегирования для уже существующих пользователей
	}

	@Override
	public NodeRef doWork () throws Exception {
		final NodeRef companyHome = repository.getCompanyHome ();
		NodeRef container = nodeService.getChildByName (companyHome, ContentModel.ASSOC_CONTAINS, CONTAINER);
		if (container == null) {
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
			container = transactionHelper.doInTransaction (new RetryingTransactionCallback<NodeRef> () {
				@Override
				public NodeRef execute () throws Throwable {
					NodeRef parentRef = companyHome; //the parent node
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
	public NodeRef getOrCreateDelegationOpts (NodeRef employeeNodeRef) {
		//делаем поиск по всем delegation-opts, если не нашли то создаем новую
		NodeRef delegationOptsNodeRef = findNodeByAssociationRef (employeeNodeRef, ASSOC_DELEGATION_OPTS_OWNER, TYPE_DELEGATION_OPTS, ASSOCIATION_TYPE.SOURCE);

		//создаем новый delegation-opts так как его нет
		if (delegationOptsNodeRef == null) {
			delegationOptsNodeRef = createDelegationOpts (employeeNodeRef);
		}
		return delegationOptsNodeRef;
	}

	private NodeRef createDelegationOpts (final NodeRef employeeNodeRef) {
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

	@Override
	public NodeRef getDelegationOptsByPerson (final NodeRef personNodeRef) {
		//смотрим у person ассоциацию на employee если нету то null, иначе ищем по employee
		NodeRef employeeRef = AuthenticationUtil.runAsSystem (new RunAsWork<NodeRef> () {

			@Override
			public NodeRef doWork () throws Exception {
				return findNodeByAssociationRef (personNodeRef, OrgstructureBean.ASSOC_EMPLOYEE_PERSON, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.SOURCE);
			}
		});
		return (employeeRef != null) ? getDelegationOptsByEmployee (employeeRef) : null;
	}

	@Override
	public NodeRef getDelegationOptsByEmployee (final NodeRef employeeNodeRef) {
		//смотрим у employee ассоциацию на delegation-opts если нету то null, иначе возвращаем delegation-opts
		return AuthenticationUtil.runAsSystem (new RunAsWork<NodeRef> () {

			@Override
			public NodeRef doWork () throws Exception {
				return getOrCreateDelegationOpts (employeeNodeRef);
			}
		});
	}

	@Override
	public List<NodeRef> getUniqueBusinessRolesByEmployee (final NodeRef employeeNodeRef, final boolean onlyActive) {
		//получаем все бизнес роли
		Set<NodeRef> uniqueBusinessRoleNodeRefs = new HashSet<NodeRef> ();
		List<NodeRef> result = new ArrayList<NodeRef> ();
		List<NodeRef> businessRoleNodeRefs = orgstructureService.getBusinesRoles (onlyActive);
		if (businessRoleNodeRefs != null) {
			//пробегаемся по всем бизнес ролям которые есть в системе и находим employees которые с ними связаны
			for (NodeRef businessRoleNodeRef : businessRoleNodeRefs) {
				List<NodeRef> employeeNodeRefs = orgstructureService.getEmployeesByBusinessRole (businessRoleNodeRef);
				//если текущая бизнес роль связана с одним и только одним пользователем, то мы ее рассматриваем
				//остальные пропускаем
				if (employeeNodeRefs != null && employeeNodeRefs.size () == 1 && employeeNodeRef.equals (employeeNodeRefs.get (0))) {
					uniqueBusinessRoleNodeRefs.add (businessRoleNodeRef);
				}
			}
			result.addAll (uniqueBusinessRoleNodeRefs);
		}
		return result;
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
	public List<NodeRef> getProcuraciesByPerson (NodeRef personNodeRef, boolean onlyActive) {
		NodeRef delegationOptsNodeRef = getDelegationOptsByPerson (personNodeRef);
		return getProcuraciesByDelegationOpts (delegationOptsNodeRef, onlyActive);
	}

	@Override
	public List<NodeRef> getProcuraciesByEmployee (final NodeRef employeeNodeRef, final boolean onlyActive) {
		//получаем параметры делегирования для сотрудника
		NodeRef delegationOptsNodeRef = getDelegationOptsByEmployee (employeeNodeRef);
		return getProcuraciesByDelegationOpts (delegationOptsNodeRef, onlyActive);
	}

	@Override
	public List<NodeRef> getProcuraciesByDelegationOpts (NodeRef delegationOptsNodeRef, boolean onlyActive) {
		List<NodeRef> procuracyNodeRefs = new ArrayList<NodeRef> ();
		List<ChildAssociationRef> childAssociationRefs = nodeService.getChildAssocs (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_PROCURACY, RegexQNamePattern.MATCH_ALL);
		if (childAssociationRefs != null) {
			for (ChildAssociationRef childAssociationRef : childAssociationRefs) {
				NodeRef procuracyNodeRef = childAssociationRef.getChildRef ();
				//по идее еще и бизнес роль на активность надо проверять
				if (!onlyActive || !isArchive (procuracyNodeRef)) {
					procuracyNodeRefs.add (procuracyNodeRef);
				} else if ((Boolean) nodeService.getProperty (procuracyNodeRef, IS_ACTIVE)) {
					procuracyNodeRefs.add (procuracyNodeRef);
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

	@Override
	public String saveDelegationOpts (final NodeRef delegationOptsNodeRef, final JSONObject options) {
		//получаем все properties
		Map<QName, Serializable> properties = nodeService.getProperties (delegationOptsNodeRef);
		//делегировать все функции
		String propCanDelegate = PROP_DELEGATION_OPTS_CAN_DELEGATE_ALL.getLocalName ();
		Boolean canDelegate = findInOptions (options, propCanDelegate, "Boolean");
		if (canDelegate != null) {
			nodeService.setProperty (delegationOptsNodeRef, PROP_DELEGATION_OPTS_CAN_DELEGATE_ALL, canDelegate);
		}
		//ссылка на доверенное лицо
		if (canDelegate != null && canDelegate) {
			//передавать права на документы подчиненных
			String propCanTransfer = PROP_DELEGATION_OPTS_CAN_TRANSFER_RIGHTS.getLocalName ();
			Boolean canTransfer = findInOptions (options, propCanTransfer, "Boolean");
			if (canTransfer != null) {
				nodeService.setProperty (delegationOptsNodeRef, PROP_DELEGATION_OPTS_CAN_TRANSFER_RIGHTS, canTransfer);
			}
			//получаем ссылку на доверенное лицо из options
			String propTrustee = ASSOC_DELEGATION_OPTS_TRUSTEE.getLocalName ();
			String trusteeRef = findInOptions (options, propTrustee + "_added", "String");
			if (trusteeRef != null) {
				//переназначем ассоциацию на доверенное лицо
				List<NodeRef> trusteeRefs = NodeRef.getNodeRefs (trusteeRef);
				nodeService.setAssociations (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_TRUSTEE, trusteeRefs);
				//получить список доверенностей таких что бизнес роли у них active = true
				List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_PROCURACY, RegexQNamePattern.MATCH_ALL);
				if (childAssocs != null) {
					for (ChildAssociationRef childAssoc : childAssocs) {
						NodeRef procuracyNodeRef = childAssoc.getChildRef ();
						nodeService.setAssociations (procuracyNodeRef, ASSOC_PROCURACY_TRUSTEE, trusteeRefs);
					}
				}
			}
		} else { //если галка "делегировать все функции" снята то удаляем ассоциацию
			//по ассоциации получаем ссылку на доверенное лицо, если она есть
			List<AssociationRef> targetAssocs = nodeService.getTargetAssocs (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_TRUSTEE);
			//если по ассоциациям кто-то есть, берем его и удаляем.
			if (targetAssocs != null && !targetAssocs.isEmpty ()) {
				for (AssociationRef targetAssoc : targetAssocs) {
					NodeRef trusteeNodeRef = targetAssoc.getTargetRef ();
					nodeService.removeAssociation (delegationOptsNodeRef, trusteeNodeRef, ASSOC_DELEGATION_OPTS_TRUSTEE);
					//все доверенности которые участвуют с этим доверенным лицом переводим в статус active = false
					//ассоциацию с доверенным лицом также разрываем
					List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_PROCURACY, RegexQNamePattern.MATCH_ALL);
					if (childAssocs != null) {
						for (ChildAssociationRef childAssoc : childAssocs) {
							NodeRef procuracyNodeRef = childAssoc.getChildRef ();
							List<AssociationRef> procuracyTargetRefs = nodeService.getTargetAssocs (procuracyNodeRef, ASSOC_PROCURACY_TRUSTEE);
							if (procuracyTargetRefs != null) {
								for (AssociationRef targetRef : procuracyTargetRefs) {
									if (trusteeNodeRef.equals (targetRef.getTargetRef ())) {
										nodeService.removeAssociation (procuracyNodeRef, trusteeNodeRef, ASSOC_PROCURACY_TRUSTEE);
									}
								}
							}
						}
					}
				}
			}
		}
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
			if (nodeRef != null) {
				List<AssociationRef> associationRefs = nodeService.getTargetAssocs (nodeRef, ASSOC_PROCURACY_TRUSTEE);
				if (associationRefs != null) {
					for (AssociationRef associationRef : associationRefs) {
						nodeService.removeAssociation (nodeRef, associationRef.getTargetRef (), ASSOC_PROCURACY_TRUSTEE);
					}
				}
			}
		}
	}

	private void setDelegationOptsActivity (final NodeRef employeeRef, boolean active) {
		NodeRef delegationOptsRef = getDelegationOptsByEmployee (employeeRef);
		Boolean isActive = (Boolean) nodeService.getProperty (delegationOptsRef, IS_ACTIVE);
		//если значения разные то изменяем значение проперти
		if (!isActive.equals (active)) {
			nodeService.setProperty (employeeRef, IS_ACTIVE, active);
		}
	}

	@Override
	public void activateDelegationForEmployee (final NodeRef employeeRef) {
		setDelegationOptsActivity (employeeRef, true);
	}

	@Override
	public void deactivateDelegationForEmployee (final NodeRef employeeRef) {
		setDelegationOptsActivity (employeeRef, false);
	}

	@Override
	public boolean hasSubordinate (final NodeRef delegationOptsNodeRef) {
		NodeRef currentEmployee = orgstructureService.getCurrentEmployee ();
		NodeRef subordinateEmployee = findNodeByAssociationRef (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_OWNER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		return orgstructureService.hasSubordinate (currentEmployee, subordinateEmployee);
	}

	@Override
	public boolean isDelegationActive (NodeRef delegationOptsNodeRef) {
		return (Boolean) nodeService.getProperty (delegationOptsNodeRef, IS_ACTIVE);
	}
}
