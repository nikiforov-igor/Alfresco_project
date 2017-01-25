package ru.it.lecm.regnumbers.counter;

import org.alfresco.util.PropertyCheck;

import java.util.List;

/**
 * Бин используется для задания тегированных счетчиков в сторонних сервисах.
 *
 * @author vlevin
 */
public class TaggedCounterRegistrator {

	private CounterFactory counterFactory;
	private String documentType;
	private List<String> tags;

	public void setCounterFactory(CounterFactory counterFactory) {
		this.counterFactory = counterFactory;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public void init() {
		PropertyCheck.mandatory(this, "counterFactory", counterFactory);
//		counterFactory.initTaggedCounters(documentType, tags);
	}
}
