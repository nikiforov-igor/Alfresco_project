package ru.it.lecm.delegation.utils;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Утилиты для работы с NodeService
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

	/**
	 * Присвоить значения свойств json-объекта в Map, совместимый со свойствами Alfresco.
	 * @param dstProps
	 * @param nsURI префиксное имя целевого типа.
	 * @param srcArgs
	 * @return
	 * @throws JSONException 
	 * @throws InvalidQNameException 
	 */
	public static Map<QName, Serializable> setProps( Map<QName, Serializable> dstProps
			, String nsURI 
			, JSONObject srcArgs
		) throws InvalidQNameException, JSONException
	{
		for(String key: JSONObject.getNames(srcArgs)) {
			dstProps.put( QName.createQName(nsURI, key), (Serializable) srcArgs.get(key));
		}
		return dstProps;
	}
}

