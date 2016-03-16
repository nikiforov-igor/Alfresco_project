package ru.it.lecm.notifications.template;

import ru.it.lecm.notifications.beans.TemplateRunException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.springframework.context.ApplicationContext;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vkuprin
 */
class ObjectMapImpl implements ObjectMap {

	private final Map<String, NodeRef> objects;

	private final Map<String, CMObject> objectsCache;

	private final ApplicationContext applicationContext;

	private final DictionaryService dictionaryService;
	private final NodeService nodeService;

	public ObjectMapImpl(Map<String, NodeRef> objects, ApplicationContext applicationContext) {
		this.objects = new HashMap<>(objects);
		this.objectsCache = new HashMap<>();
		this.applicationContext = applicationContext;
		this.nodeService = applicationContext.getBean("nodeService", NodeService.class);
		this.dictionaryService = applicationContext.getBean("dictionaryService", DictionaryService.class);
	}

	@Override
	public CMObject get(String name) throws TemplateRunException {
		if (objectsCache.containsKey(name)) {
			return objectsCache.get(name);
		} else {
			NodeRef ref = objects.get(name);
			if (null == ref) {
				throw new TemplateRunException("No object with id \"" + name + "\"");
			}

			QName objType = nodeService.getType(ref);
			CMObject obj;
			if (dictionaryService.isSubClass(objType, DocumentService.TYPE_BASE_DOCUMENT)) {
				obj = new DocumentImpl(ref, applicationContext);
			} else if (dictionaryService.isSubClass(objType, OrgstructureBean.TYPE_EMPLOYEE)) {
				obj = new EmployeeImpl(ref, applicationContext);
			} else {
				obj = new CMObjectImpl(ref, applicationContext);
			}

			objectsCache.put(name, obj);
			return obj;
		}
	}

	@Override
	public Map<String, CMObject> getFullMap() throws TemplateRunException {
		for (String key : objects.keySet()) {
			get(key);
		}
		return objectsCache;
	}
}
