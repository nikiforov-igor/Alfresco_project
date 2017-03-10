package ru.it.lecm.regnumbers.counter;

import org.alfresco.util.PropertyCheck;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;

/**
 * Бин используется для задания тегированных счетчиков в сторонних сервисах.
 *
 * @author vlevin
 */
public class TaggedCounterRegistrator extends BaseBean {

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

	@Override
	protected void initServiceImpl() {
		PropertyCheck.mandatory(this, "counterFactory", counterFactory);
		counterFactory.initTaggedCounters(documentType, tags);
	}
	
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
}
