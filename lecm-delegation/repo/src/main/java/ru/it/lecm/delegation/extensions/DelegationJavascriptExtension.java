package ru.it.lecm.delegation.extensions;

import java.util.List;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.delegation.IDelegation;

/**
 * javascript root object для модуля делегирования
 * реализует API для проверки прав, создания папок и тд
 *
 * @author VLadimir Malygin
 * @since 12.12.2012 14:28:32
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class DelegationJavascriptExtension extends BaseScopableProcessorExtension {

	private final static Logger logger = LoggerFactory.getLogger (DelegationJavascriptExtension.class);
	private ServiceRegistry serviceRegistry;
	private IDelegation delegationService;

	public void setServiceRegistry (ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

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

	public ScriptNode getDelegationOptsByPerson (final String personRef) {
		NodeRef nodeRef = delegationService.getDelegationOptsByPerson (new NodeRef (personRef));
		if (nodeRef != null) {
			return new ScriptNode (nodeRef, serviceRegistry, getScope ());
		}
		return null;
	}

	public ScriptNode getDelegationOptsByEmployee (final String employeeRef) {
		NodeRef nodeRef = delegationService.getDelegationOptsByEmployee (new NodeRef (employeeRef));
		if (nodeRef != null) {
			return new ScriptNode (nodeRef, serviceRegistry, getScope ());
		}
		return null;
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

	private final static QName ASSOC_PROCURACY_BUSINESS_ROLE = QName.createQName ("http://www.it.ru/logicECM/model/delegation/1.0", "procuracy-business-role-assoc");

	/**
	 * актуализировать список доверенностей по указанному пользователю системы
	 * @param personRef идентификатор пользователя
	 * @return список свежесозданных доверенностей
	 */
	public Scriptable actualizeProcuraciesForPerson (final String personRef) {
		NodeRef delegationOptsNodeRef= delegationService.getDelegationOptsByPerson (new NodeRef (personRef));
		return actualizeProcuraciesForDelegationOpts (delegationOptsNodeRef.toString ());
	}

	/**
	 * актуализировать список доверенностей по указанному сотруднику
	 * @param employeeRef идентификатор сотрудника
	 * @return список свежесозданных доверенностей
	 */
	public Scriptable actualizeProcuraciesForEmployee (final String employeeRef) {
		NodeRef delegationOptsNodeRef = delegationService.getDelegationOptsByEmployee (new NodeRef (employeeRef));
		return actualizeProcuraciesForDelegationOpts (delegationOptsNodeRef.toString ());
	}

	/**
	 * актуализировать список доверенностей по параметрам делегирования
	 * @param delegationOptsRef идентификатор параметров делегирования
	 * @return список свежесозданных доверенностей
	 */
	public Scriptable actualizeProcuraciesForDelegationOpts (final String delegationOptsRef) {
		NodeService nodeService = serviceRegistry.getNodeService ();
		//получаем список активных уникальных бизнес ролей
		List<NodeRef> businessRoleNodeRefs = delegationService.getUniqueBusinessRolesByDelegationOpts (new NodeRef (delegationOptsRef), true);
		//получаем список доверенностей, любых какие есть
		List<NodeRef> procuracyNodeRefs = delegationService.getProcuraciesByDelegationOpts (new NodeRef (delegationOptsRef), false);
		//смотрим есть ли среди настроенных доверенностей бизнес роли из списка уникальных
		for (NodeRef procuracyNodeRef : procuracyNodeRefs) {
			List<AssociationRef> associationRefs = nodeService.getTargetAssocs (procuracyNodeRef, ASSOC_PROCURACY_BUSINESS_ROLE);
			if (associationRefs != null && !associationRefs.isEmpty ()) {
				//у нас ассоциация 1 к много. Т.е. бизнес ролей то много, но в одной доверенности одна бизнес роль
				NodeRef businessRoleNodeRef = associationRefs.get (0).getTargetRef ();
				//если настроенная на доверенность бизнес роль в списке уникальных уже есть, то удалить ее оттуда
				if (businessRoleNodeRefs.contains (businessRoleNodeRef)) {
					businessRoleNodeRefs.remove (businessRoleNodeRef);
				}
			}
		}
		//для оставшихся бизнес ролей создаем доверенности с флагом active=false и возвращаем кол-во доверенностей
		return getAsScriptable (delegationService.createEmptyProcuracies (new NodeRef (delegationOptsRef), businessRoleNodeRefs));
	}

	public String saveDelegationOpts (final String delegationOptsRef, final JSONObject options) {
		return delegationService.saveDelegationOpts (new NodeRef (delegationOptsRef), options);
	}

	public void deleteProcuracies (final JSONArray nodeRefs) {
		delegationService.deleteProcuracies (nodeRefs);
	}
}
