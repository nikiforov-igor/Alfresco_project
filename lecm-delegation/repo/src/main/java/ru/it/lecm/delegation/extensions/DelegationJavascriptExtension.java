package ru.it.lecm.delegation.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.delegation.beans.DelegationBean;

import java.util.List;
import org.alfresco.model.ContentModel;

/**
 * javascript root object для модуля делегирования
 * реализует API для проверки прав, создания папок и тд
 *
 * @author VLadimir Malygin
 * @since 12.12.2012 14:28:32
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class DelegationJavascriptExtension extends BaseWebScript {

	private IDelegation delegationService;

	public void setDelegationService (IDelegation delegationService) {
		this.delegationService = delegationService;
	}

	public ScriptNode getDelegationOptsContainer () {
		NodeRef container = delegationService.getDelegationDescriptor ().getDelegationOptsContainer ();
		if (container != null) {
			return new ScriptNode (container, serviceRegistry, getScope ());
		}
		return null;
	}

	public String getItemType () {
		QName itemType = delegationService.getDelegationDescriptor ().getDelegationOptsItemType ();
		if (itemType != null) {
			NamespacePrefixResolver namespacePrefixResolver = serviceRegistry.getNamespaceService ();
			return itemType.toPrefixString (namespacePrefixResolver);
		}
		return null;
	}

	public ScriptNode getDelegationOpts (final String ref) {
		ScriptNode scriptNode = null;
		if (NodeRef.isNodeRef (ref)) {
//			TODO: Потенциально может так получится, что настройки делегирования ещё не созданы,
//			(из-за разделения метода getOrCreate, поэтому попробуем создать.
//			Вызывается как минимум из одного не транзакционнного скрипта, поэтому обернём
//                      delegationOpts проверяется/создаётся при создании/изменении сотрудника, так что здесь проверять особой необходимости нет.
			final NodeRef employee = new NodeRef(ref);
			NodeRef nodeRef = delegationService.getDelegationOpts(employee);
//			if(delegationService.getDelegationOpts(new NodeRef(ref)) == null) {
//				nodeRef = lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>(){
//					@Override
//					public NodeRef execute() throws Throwable {
//						return delegationService.createDelegationOpts(employee);
//					}
//				});
//			}
			if (nodeRef != null) {
				scriptNode = new ScriptNode (nodeRef, serviceRegistry, getScope ());
			}
		}
		return scriptNode;
	}

	/**
	 * обернуть список NodeRef-ов в объект типа Scriptable
	 * @param nodeRefs список NodeRef-ов
	 * @return специальный объект доступный для работы из JS
	 */
	private Scriptable getAsScriptable (List<NodeRef> nodeRefs) {
		Scriptable scope = getScope ();
		int size = nodeRefs.size ();
		Object[] nodes = new Object[size];
		for (int i = 0; i < size; ++i) {
			nodes[i] = new ScriptNode (nodeRefs.get (i), serviceRegistry, scope);
		}
		return Context.getCurrentContext ().newArray (scope, nodes);
	}

	/**
	 * вебскриптовый массив бизнес ролей уникальных для этого сотрудника
	 * @param employeeRef идентификатор сотрудника
	 * @return
	 */
	public Scriptable getUniqueBusinessRolesByEmployee (final String employeeRef, final boolean onlyActive) {
		List<NodeRef> uniqueBusinessRoleNodeRefs = delegationService.getUniqueBusinessRolesByEmployee (new NodeRef (employeeRef), onlyActive);
		return getAsScriptable (uniqueBusinessRoleNodeRefs);
	}

	public Scriptable getUniqueBusinessRolesByPerson (final String personRef, final boolean onlyActive) {
		List<NodeRef> uniqueBusinessRoleNodeRefs = delegationService.getUniqueBusinessRolesByPerson (new NodeRef (personRef), onlyActive);
		return getAsScriptable (uniqueBusinessRoleNodeRefs);
	}

	public Scriptable getUniqueBusinessRolesByDelegationOpts (final String delegationOptsRef, final boolean onlyActive) {
		List<NodeRef> uniqueBusinessRoleNodeRefs = delegationService.getUniqueBusinessRolesByDelegationOpts (new NodeRef (delegationOptsRef), onlyActive);
		return getAsScriptable (uniqueBusinessRoleNodeRefs);
	}

	/**
	 * актуализовать список доверенностей по указанному объекту системы
	 * @param ref объект системы, можно передать следующие типы объектов cm:person, lecm-orgstr:employee, lecm-d8n:delegation-opts
	 * @return  список свежесозданных доверенностей
	 */
	public Scriptable actualizeProcuracies (final String ref) {
		Scriptable scriptable = Context.getCurrentContext ().newArray (getScope (), new Object[] {});
		if (NodeRef.isNodeRef (ref)) {
			final NodeRef employee = new NodeRef (ref);
			NodeRef delegationOptsNodeRef = delegationService.getDelegationOpts (employee);
//			TODO: Потенциально может так получится, что настройки делегирования ещё не созданы,
//			(из-за разделения метода getOrCreate, поэтому попробуем создать.
//			Вызывается как минимум из одного не транзакционнного скрипта, поэтому обернём
//                      delegationOpts проверяется/создаётся при создании/изменении сотрудника, так что здесь проверять особой необходимости нет.                        
//			if(delegationOptsNodeRef == null) {
//				delegationOptsNodeRef = lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>(){
//					@Override
//					public NodeRef execute() throws Throwable {
//						return delegationService.createDelegationOpts(employee);
//					}
//				});
//			}
			if (delegationOptsNodeRef != null) {
				NodeService nodeService = serviceRegistry.getNodeService ();
				//получаем список активных уникальных бизнес ролей
				List<NodeRef> businessRoleNodeRefs = delegationService.getUniqueBusinessRolesByDelegationOpts (delegationOptsNodeRef, true);
				//получаем список доверенностей, любых какие есть
				List<NodeRef> procuracyNodeRefs = delegationService.getProcuracies (delegationOptsNodeRef, false);
				//смотрим есть ли среди настроенных доверенностей бизнес роли из списка уникальных
				for (NodeRef procuracyNodeRef : procuracyNodeRefs) {
					List<AssociationRef> associationRefs = nodeService.getTargetAssocs (procuracyNodeRef, DelegationBean.ASSOC_PROCURACY_BUSINESS_ROLE);
					if (associationRefs != null && !associationRefs.isEmpty ()) {
						//у нас ассоциация 1 к много. Т.е. бизнес ролей то много, но в одной доверенности одна бизнес роль
						NodeRef businessRoleNodeRef = associationRefs.get (0).getTargetRef ();
						//если настроенная на доверенность бизнес роль в списке уникальных уже есть, то удалить ее оттуда
						if (businessRoleNodeRefs.contains (businessRoleNodeRef)) {
							businessRoleNodeRefs.remove (businessRoleNodeRef);
						} else {
							//удалить доверенность с бизнес ролью, в которую пользователь уже не входит
							nodeService.addAspect(procuracyNodeRef, ContentModel.ASPECT_TEMPORARY, null);
							nodeService.deleteNode(procuracyNodeRef);
						}
					}
				}
				//для оставшихся бизнес ролей создаем доверенности с флагом active=false и возвращаем кол-во доверенностей
				scriptable = getAsScriptable (delegationService.createEmptyProcuracies (delegationOptsNodeRef, businessRoleNodeRefs));
			}
		}
		return scriptable;
	}

	public String saveDelegationOpts (final String delegationOptsRef, final JSONObject options) {
		return delegationService.saveDelegationOpts (new NodeRef (delegationOptsRef), options);
	}

	public void deleteProcuracies (final JSONArray nodeRefs) {
		delegationService.deleteProcuracies (nodeRefs);
	}

	public boolean hasSubordinate (final String ref) {
		return NodeRef.isNodeRef (ref) && delegationService.hasSubordinate (new NodeRef (ref));
	}

	public boolean isDelegationActive (final String delegationOptsRef) {
		return NodeRef.isNodeRef (delegationOptsRef) && delegationService.isDelegationActive (new NodeRef (delegationOptsRef));
	}

	/**
	 * запустить процесс делегирования для указанного объекта
	 * @param delegator объект для которого запускается делегирование.
	 * Валидными объектами являются: логин пользователя, nodeRef person, nodeRef employee, nodeRef delegation-opts
	 */
	public void startDelegation (final String delegator) {
		/*
		 * проверим что delegator это nodeRef,
		 * а дальше delegationService сам разберется что он получил на вход
		 * и запустит процесс делегирования
		 */
		if (NodeRef.isNodeRef (delegator)) {
			delegationService.startDelegation (new NodeRef (delegator));
		} else {
			delegationService.startDelegation (delegator);
		}
	}

	/**
	 * завершить делегирование для указанного объекта
	 * @param delegator объект для которого запускается делегирование.
	 * Валидными объектами являются: логин пользователя, nodeRef person, nodeRef employee, nodeRef delegation-opts
	 */
	public void stopDelegation (final String delegator) {
		/*
		 * проверим что delegator это nodeRef,
		 * а дальше delegationService сам разберется что он получил на вход
		 * и завершит процесс делегирования
		 */
		if (NodeRef.isNodeRef (delegator)) {
			delegationService.stopDelegation (new NodeRef (delegator));
		} else {
			delegationService.stopDelegation (delegator);
		}
	}

	/**
	 * получение сотрудника для объекта системы
	 * В качестве объекта системы можно передать cm:person, lecm-orgstr:employee, lecm-d8n:delegation-opts
	 * @param ref объект системы, можно передать следующие типы объектов cm:person, lecm-orgstr:employee, lecm-d8n:delegation-opts
	 * @return ScriptNode идентификатор сотрудника или null если ничего не нашел.
	 */
	public ScriptNode getEmployee (final String ref) {
		/*
		 * проверим что ref это NodeRef,
		 * а дальше delegationService сам разберется что он получил на вход
		 */
		ScriptNode result = null;
		if (NodeRef.isNodeRef (ref)) {
			NodeRef employeeRef = delegationService.getEmployee (new NodeRef (ref));
			if (employeeRef != null) {
				result = new ScriptNode (employeeRef, serviceRegistry, getScope ());
			}
		}
		return result;
	}

	/**
	 * получение сотрудника для объекта системы
	 * В качестве объекта системы можно передать cm:person, lecm-orgstr:employee, lecm-d8n:delegation-opts
	 * @param scriptNode объект системы, можно передать следующие типы объектов cm:person, lecm-orgstr:employee, lecm-d8n:delegation-opts
	 * @return ScriptNode идентификатор сотрудника или null если ничего не нашел.
	 */
	public ScriptNode getEmployee (final ScriptNode scriptNode) {
		ScriptNode result = null;
		if (scriptNode != null && scriptNode.getNodeRef () != null) {
			NodeRef employeeRef = delegationService.getEmployee (scriptNode.getNodeRef ());
			if (employeeRef != null) {
				result = new ScriptNode (employeeRef, serviceRegistry, getScope ());
			}
		}
		return result;
	}

	public ScriptNode getEffectiveExecutor(String assumedExecutorRef) {
//		TODO: Метод getEffectiveExecutor через несколько уровней вызывает getDelegationOpts,
//		который ранее был getOrCreate, поэтому необходимо сделать проверку на существование
//		и при необходимости создать
//              delegationOpts проверяется/создаётся при создании/изменении сотрудника, так что здесь проверять особой необходимости нет.            
//		NodeRef employeeRef = new NodeRef(assumedExecutorRef);
//		if(delegationService.getDelegationOpts(employeeRef) == null) {
//			delegationService.createDelegationOpts(employeeRef);
//		}
		NodeRef effectiveExecutor = delegationService.getEffectiveExecutor(new NodeRef(assumedExecutorRef));
		if (effectiveExecutor != null) {
			return new ScriptNode(effectiveExecutor, serviceRegistry, getScope());
		} else {
			return null;
		}
	}

	public ScriptNode getEffectiveExecutor(String assumedExecutorRef, String businessRoleStr) {
		NodeRef effectiveExecutor = delegationService.getEffectiveExecutor(new NodeRef(assumedExecutorRef), businessRoleStr);
		if (effectiveExecutor != null) {
			return new ScriptNode(effectiveExecutor, serviceRegistry, getScope());
		} else {
			return null;
		}
	}

	public ScriptNode assignTaskToEffectiveExecutor(String assumedExecutor, String businessRole, String taskID) {
//		TODO: Метод assignTaskToEffectiveExecutor через несколько уровней вызывает getDelegationOpts,
//		который ранее был getOrCreate, поэтому необходимо сделать проверку на существование
//		и при необходимости создать
//              delegationOpts проверяется/создаётся при создании/изменении сотрудника, так что здесь проверять особой необходимости нет.            
//		NodeRef employeeRef = new NodeRef(assumedExecutor);
//		if(delegationService.getDelegationOpts(employeeRef) == null) {
//			delegationService.createDelegationOpts(employeeRef);
//		}
		NodeRef result = delegationService.assignTaskToEffectiveExecutor(new NodeRef(assumedExecutor), businessRole, taskID);
		if (result != null) {
			return new ScriptNode(result, serviceRegistry, getScope());
		} else {
			return null;
		}
	}

	public ScriptNode getGlobalSettingsNode() {
		return new ScriptNode(delegationService.getGlobalSettingsNode(), serviceRegistry, getScope());
	}
}
