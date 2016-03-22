package ru.it.lecm.notifications.template;

import java.util.Map;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author vmalygin
 */
public abstract class AbstractParserImpl implements Parser {

	private ObjectMapImpl notificationObjects;
	protected final ApplicationContext applicationContext;

	public AbstractParserImpl(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Метод для получения объекта 'objects' в контексте SpEL. По сути, SpEL вызов
	 * интерпретирует 'objects.method()' как 'this.getObjects().method()'
	 * @return objects
	 */
	public ObjectMap getObjects() {
		return notificationObjects;
	}

	/**
	 * Создание объекта objects для SpEL'а.
	 */
	protected void setObjects(Map<String, Object> objects) {
		this.notificationObjects = new ObjectMapImpl(objects, applicationContext);
	}
}
