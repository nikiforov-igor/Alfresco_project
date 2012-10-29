package ru.it.lecm.delegation.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
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


	/**
	 * Сформировать список из id-шников 
	 * @param listChild список дочерних ссылок
	 * @return всегда не null список из Id-шников ссылок (при listChild == null или 
	 * пустом ListChild воз-ся пустой результат)
	 */
	public static List<String> makeIdList(Collection<ChildAssociationRef> listChild) {
		return (List<String>) makeIdList( new ArrayList<String>(), listChild);
	}

	/**
	 * Сформировать список из id-шников ссылок
	 * @param dest целевой проинициализированный список
	 * @param refs
	 * @return dest с включенными id из refs
	 */
	public static Collection<String> makeIdList(final Collection<String> dest, Collection<ChildAssociationRef> refs) {
		if (refs != null)
			for(ChildAssociationRef item: refs)
				dest.add( item.getChildRef().getId() );
		return dest;
	}


	/**
	 * Получить список значений, которые есть в списке listA, но отсутствуют в listB.
	 * @param oldIds
	 * @param newIds
	 * @return не null список значений условно соот-щий выражению "listA-listB" 
	 * (только из значений списка listA).
	 */
	public static List<String> getDiffer(Collection<String> listA, Collection<String> listB) {
		final List<String> result = new ArrayList<String>();
		if (listA != null && !listA.isEmpty()) {
			if (listB == null || listB.isEmpty()) {
				// если нет списка B -> оставить целиком список A
				result.addAll(listA);
			} else {
				final Set<String> setB = new HashSet<String>(listB); // для быстрого поиска ...
				// включаем всё из A, чего нет в B ...
				for(String item: listA) 
					if (!setB.contains(item))
						result.add(item);
			}
		}
		return result;
	}


	/**
	 * Получить список значений, которые есть в списке listA, но отсутствуют в listB.
	 * @param oldIds
	 * @param newIds
	 * @return не null список значений условно соот-щий выражению "listA-listB" 
	 * (только из значений списка listA).
	 */
	public static List<ChildAssociationRef> getDifferRefs(Collection<ChildAssociationRef> listA, Collection<ChildAssociationRef> listB) {
		final List<ChildAssociationRef> result = new ArrayList<ChildAssociationRef>();
		if (listA != null && !listA.isEmpty()) {
			if (listB == null || listB.isEmpty()) {
				// если нет списка B -> оставить целиком список A
				result.addAll(listA);
			} else {
				final Set<String> setB = new HashSet<String>(); // для быстрого поиска ...
				makeIdList(setB, listB);
				// включаем всё из A, чего нет в B ...
				for(ChildAssociationRef item: listA) 
					if (!setB.contains(item.getChildRef().getId()))
						result.add(item);
			}
		}
		return result;
	}
}

