package ru.it.lecm.delegation.beans;

import java.util.Map;

import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 *
 * @author VLadimir Malygin
 * @since 19.10.2012 10:49:00
 * @see <p>mailto: <a href="mailto:vladimir.malygin@aplana.com">vladimir.malygin@aplana.com</a></p>
 */
public class ListTestUnits extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger (ListTestUnits.class);

	private Repository repository;
	private ServiceRegistry serviceRegistry;

	@Override
	protected Map<String, Object> executeImpl (WebScriptRequest req, Status status, Cache cache) {
		return super.executeImpl (req, status, cache);
	}

	public void setRepository (Repository repository) {
		this.repository = repository;
	}

	public void setServiceRegistry (ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

}
