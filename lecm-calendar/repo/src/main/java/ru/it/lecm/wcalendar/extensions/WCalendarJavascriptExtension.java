/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.wcalendar.extensions;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.wcalendar.IWCalCommon;

/**
 *
 * @author vlevin
 */
public class WCalendarJavascriptExtension extends BaseScopableProcessorExtension {

	private ServiceRegistry serviceRegistry;
	private IWCalCommon wCalendarService;
	private final static Logger logger = LoggerFactory.getLogger(WCalendarJavascriptExtension.class);

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setWCalService(IWCalCommon wCalendarService) {
		this.wCalendarService = wCalendarService;
	}

	public ScriptNode getWCalendarContainer() {
		NodeRef container = wCalendarService.getWCalendarDescriptor().getWCalendarContainer();
		if (container != null) {
			return new ScriptNode(container, serviceRegistry, getScope());
		}
		return null;
	}

	public String getItemType() {
		QName itemType = wCalendarService.getWCalendarDescriptor().getWCalendarItemType();
		if (itemType != null) {
			NamespacePrefixResolver namespacePrefixResolver = serviceRegistry.getNamespaceService();
			return itemType.toPrefixString(namespacePrefixResolver);
		}
		return null;
	}
}
