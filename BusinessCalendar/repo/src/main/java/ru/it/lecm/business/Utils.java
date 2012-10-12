package ru.it.lecm.business;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * Утилиты
 * 
 * @author rabdullin
 *
 */
public class Utils {

	private Utils() {
	}

	public static String getElementName(final NodeService service, final NodeRef ref, QName property, QName defaultProperty) {
		final String value = (property == null) ? null : (String) service.getProperty(ref, property);
		return (value != null) ? value : (String) service.getProperty(ref, defaultProperty);
	}

	public static String getElementName(final NodeService service, final NodeRef ref, QName property) {
		return getElementName(service, ref, property, ContentModel.PROP_NAME);
	}

	public static String getElementName(final NodeService service, final NodeRef ref) {
		return getElementName(service, ref, null, ContentModel.PROP_NAME);
	}
	
}
