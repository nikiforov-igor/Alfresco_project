package ru.it.lecm.notifications.template;

import java.util.Map;
import ru.it.lecm.notifications.beans.TemplateRunException;

/**
 *
 * @author vkuprin
 */
public interface ObjectMap {

	Object get(String name) throws TemplateRunException;

	Map<String, Object> getFullMap() throws TemplateRunException;
}
