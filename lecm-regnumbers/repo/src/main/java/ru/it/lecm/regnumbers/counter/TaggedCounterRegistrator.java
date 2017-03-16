package ru.it.lecm.regnumbers.counter;

import org.alfresco.util.PropertyCheck;

import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import ru.it.lecm.base.beans.LecmService;
import ru.it.lecm.base.beans.LecmServicesRegistry;

/**
 * Бин используется для задания тегированных счетчиков в сторонних сервисах.
 *
 * @author vlevin
 */
public class TaggedCounterRegistrator implements InitializingBean, LecmService {

	private CounterFactory counterFactory;
	private String documentType;
	private List<String> tags;
	private LecmServicesRegistry lecmServicesRegistry;

	public void setCounterFactory(CounterFactory counterFactory) {
		this.counterFactory = counterFactory;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public void setLecmServicesRegistry(LecmServicesRegistry lecmServicesRegistry) {
		this.lecmServicesRegistry = lecmServicesRegistry;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		lecmServicesRegistry.register(this);
	}

	@Override
	public void initService() {
		PropertyCheck.mandatory(this, "counterFactory", counterFactory);
		counterFactory.initTaggedCounters(documentType, tags);
	}
}
