package ru.it.lecm.delegation.extensions;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
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
	private NamespacePrefixResolver namespacePrefixResolver;
	private IDelegation serviceDelegation;

	public void setServiceRegistry (ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setNamespacePrefixResolver (NamespacePrefixResolver namespacePrefixResolver) {
		this.namespacePrefixResolver = namespacePrefixResolver;
	}

	public void setServiceDelegation (IDelegation serviceDelegation) {
		this.serviceDelegation = serviceDelegation;
	}

	public ScriptNode getDelegationOptsContainer () {
		NodeRef container = serviceDelegation.getDelegationOptsContainer ();
		if (container != null) {
			return new ScriptNode (container, serviceRegistry, getScope ());
		}
		return null;
	}

	public String getItemType () {
		QName itemType = serviceDelegation.getItemType ();
		if (itemType != null) {
			PropertyCheck.mandatory (this, "namespacePrefixResolver", namespacePrefixResolver);
			return itemType.toPrefixString (namespacePrefixResolver);
		}
		return null;
	}
}
