package ru.it.lecm.notifications.template;

import ru.it.lecm.notifications.beans.TemplateRunException;

/**
 *
 * @author vkuprin
 */
public interface ObjectMap {

	CMObject get(String name) throws TemplateRunException;
	
}
