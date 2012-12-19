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
	 * вебскриптовый массив бизнес ролей уникальных для этого сотрудника
	 * @param employeeRef идентификатор сотрудника
	 * @return
	 */
	public Scriptable getUniqueBusinessRolesByEmployee (final String employeeRef) {
		Scriptable scope = getScope ();
		List<NodeRef> uniqueBusinessRoleNodeRefs = delegationService.getUniqueBusinessRolesByEmployee (new NodeRef (employeeRef));
		Object[] nodes = new Object[uniqueBusinessRoleNodeRefs.size ()];
		for (int i = 0; i < uniqueBusinessRoleNodeRefs.size (); ++i) {
			nodes[i] = new ScriptNode (uniqueBusinessRoleNodeRefs.get (i), serviceRegistry, scope);
		}
		return Context.getCurrentContext ().newArray (scope, nodes);
	}

	public Scriptable getUniqueBusinessRolesByPerson (final String personRef) {
		Scriptable scope = getScope ();
		List<NodeRef> uniqueBusinessRoleNodeRefs = delegationService.getUniqueBusinessRolesByPerson (new NodeRef (personRef));
		Object[] nodes = new Object[uniqueBusinessRoleNodeRefs.size ()];
		for (int i = 0; i < uniqueBusinessRoleNodeRefs.size (); ++i) {
			nodes[i] = new ScriptNode (uniqueBusinessRoleNodeRefs.get (i), serviceRegistry, scope);
		}
		return Context.getCurrentContext ().newArray (scope, nodes);
	}

	public Scriptable getUniqueBusinessRolesByDelegationOpts (final String delegationOptsRef) {
		Scriptable scope = getScope ();
		List<NodeRef> uniqueBusinessRoleNodeRefs = delegationService.getUniqueBusinessRolesByDelegationOpts (new NodeRef (delegationOptsRef));
		Object[] nodes = new Object[uniqueBusinessRoleNodeRefs.size ()];
		for (int i = 0; i < uniqueBusinessRoleNodeRefs.size (); ++i) {
			nodes[i] = new ScriptNode (uniqueBusinessRoleNodeRefs.get (i), serviceRegistry, scope);
		}
		return Context.getCurrentContext ().newArray (scope, nodes);
	}

	/**
	 * вебскриптовый массив доверенностей для этого сотрудника
	 * @param employeeRef идентификатор сотрудника
	 * @return
	 */
	public Scriptable getProcuraciesByEmployee (final String employeeRef) {
		List<NodeRef> procuracyNodeRefs = delegationService.getProcuraciesByEmployee (new NodeRef (employeeRef));
		Scriptable scope = getScope ();
		Object[] nodes = new Object[procuracyNodeRefs.size ()];
		for (int i = 0; i < procuracyNodeRefs.size (); ++i) {
			nodes[i] = new ScriptNode (procuracyNodeRefs.get (i), serviceRegistry, scope);
		}
		return Context.getCurrentContext ().newArray (scope, nodes);
	}

	private final static QName ASSOC_PROCURACY_BUSINESS_ROLE = QName.createQName ("http://www.it.ru/logicECM/model/delegation/1.0", "procuracy-business-role-assoc");

	public String getProcuracies (final String employeeRef) {
		NodeService nodeService = serviceRegistry.getNodeService ();
		NodeRef employeeNodeRef = new NodeRef (employeeRef);
		List<NodeRef> procuracyNodeRefs = delegationService.getProcuraciesByEmployee (employeeNodeRef);
		List<NodeRef> uniqueBusinessRoleNodeRefs = delegationService.getUniqueBusinessRolesByEmployee (employeeNodeRef);
		//смотрим есть ли среди настроенных доверенностей бизнес роли из списка уникальных
		for (NodeRef procuracyNodeRef : procuracyNodeRefs) {
			List<AssociationRef> associationRefs = nodeService.getTargetAssocs (procuracyNodeRef, ASSOC_PROCURACY_BUSINESS_ROLE);
			if (associationRefs != null && !associationRefs.isEmpty ()) {
				//у нас ассоциация 1 к много. Т.е. бизнес ролей то много, но в одной доверенности одна бизнес роль
				NodeRef businessRoleNodeRef = associationRefs.get (0).getTargetRef ();
				//если настроенная на доверенность бизнес роль в списке уникальных уже есть, то удалить ее оттуда
				if (uniqueBusinessRoleNodeRefs.contains (businessRoleNodeRef)) {
					uniqueBusinessRoleNodeRefs.remove (businessRoleNodeRef);
				}
			}
		}
		//строим json-объект
		JSONObject result = new JSONObject ();
		try {
			result.put ("versionable", false);
			result.put ("totalRecords", procuracyNodeRefs.size () + uniqueBusinessRoleNodeRefs.size ());
			result.put ("metadata", new JSONObject ("\"permissions\": {\"userAccess\": {\"create\": true}}"));
		} catch (JSONException ex) {
			logger.error (ex.getMessage (), ex);
		}
		//складываем в него теперь items

		return result.toString ();
	}
}
