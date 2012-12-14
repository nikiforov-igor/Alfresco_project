package ru.it.lecm.delegation.extensions;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
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
}
