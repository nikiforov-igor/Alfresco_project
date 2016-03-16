package ru.it.lecm.notifications.template;

import java.util.Map;
import ru.it.lecm.notifications.beans.TemplateRunException;

/**
 *
 * @author vkuprin
 */
public interface ObjectMap {

	CMObject get(String name) throws TemplateRunException;

	Map<String, CMObject> getFullMap() throws TemplateRunException;
}
