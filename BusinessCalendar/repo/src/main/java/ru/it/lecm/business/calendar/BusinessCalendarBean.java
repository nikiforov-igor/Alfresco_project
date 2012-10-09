package ru.it.lecm.business.calendar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.processor.Processor;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author raa
 * Date: 2012/10/05
 */
public class BusinessCalendarBean extends BaseProcessorExtension {

	protected static Log logger = LogFactory.getLog(BusinessCalendarBean.class);
	private ServiceRegistry serviceRegistry;

	@Override
	public String toString() {
		// return super.toString();
		return this.getClass().getName();
	}


	@Override
	public void register() {
		super.register();
		logger.info("registered");
	}

/*
	@Override
	public void setProcessor(Processor processor) {
		super.setProcessor(processor);
	}

	@Override
	public String getExtensionName() {
		return super.getExtensionName();
	}
 */

	@Override
	public void setExtensionName(String extension) {
		super.setExtensionName(extension);
		logger.info( String.format("extension name changed to '%s'", extension) );
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
}