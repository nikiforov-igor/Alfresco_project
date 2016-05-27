package ru.it.lecm.notifications.template;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.springframework.context.ApplicationContext;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.TemplateRunException;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author vkuprin
 */
class ObjectMapImpl implements ObjectMap {

	private final Map<String, Object> objects;

	private final Map<String, Object> objectsCache;

	private final ApplicationContext applicationContext;

	private final DictionaryService dictionaryService;
	private final NodeService nodeService;
	private static final Pattern nodeRefPattern = Pattern.compile("^[a-zA-Z]+://[a-zA-Z]+/[a-zA-Z0-9/-]+$");

	public ObjectMapImpl(Map<String, Object> objects, ApplicationContext applicationContext) {
		this.objects = new HashMap<>(objects);
		this.objectsCache = new HashMap<>();
		this.applicationContext = applicationContext;
		this.nodeService = applicationContext.getBean("nodeService", NodeService.class);
		this.dictionaryService = applicationContext.getBean("dictionaryService", DictionaryService.class);
	}

	@Override
	public Object get(String name) throws TemplateRunException {
		if (objectsCache.containsKey(name)) {
			return objectsCache.get(name);
		} else {
			Object result;
			Object obj = objects.get(name);
			if (null == obj) {
				throw new TemplateRunException("No object with id \"" + name + "\"");
			}

			if (nodeRefPattern.matcher(obj.toString()).matches()) {
				NodeRef ref = (NodeRef) obj;
				QName objType = nodeService.getType(ref);
				CMObject cmobj;
				if (dictionaryService.isSubClass(objType, DocumentService.TYPE_BASE_DOCUMENT)) {
					cmobj = new DocumentImpl(ref, applicationContext);
				} else if (dictionaryService.isSubClass(objType, OrgstructureBean.TYPE_EMPLOYEE)) {
					cmobj = new EmployeeImpl(ref, applicationContext);
				} else {
					cmobj = new CMObjectImpl(ref, applicationContext);
				}
				objectsCache.put(name, cmobj);
				result = cmobj;
			} else {
				objectsCache.put(name, obj);
				result = obj;
			}
			return result;
		}
	}

	@Override
	public Map<String, Object> getFullMap() throws TemplateRunException {
		for (String key : objects.keySet()) {
			get(key);
		}
		return objectsCache;
	}
}
