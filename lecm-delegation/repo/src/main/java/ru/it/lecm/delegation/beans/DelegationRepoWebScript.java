package ru.it.lecm.delegation.beans;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 *
 * @author VLadimir Malygin
 * @since 18.10.2012 14:41:08
 * @see <p>mailto: <a href="mailto:vladimir.malygin@aplana.com">vladimir.malygin@aplana.com</a></p>
 */
public class DelegationRepoWebScript extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger (DelegationRepoWebScript.class);

	@Override
	protected Map<String, Object> executeImpl (WebScriptRequest req, Status status, Cache cache) {

		logger.debug ("executing delegation webscript");
		logger.debug ("http session is {}", ServletUtil.getSession ());

		HashMap<String, Object> data = new HashMap<String, Object> ();
		data.put ("id", UUID.randomUUID ());
		data.put ("name", "someData");
		data.put ("title", "this is some data. It is unique by it's id and can be serialized to json");
		data.put ("date", new Date ());
		HashMap<String, Object> model = new HashMap<String, Object> ();
		model.put ("model", data);
		return model;
	}
}
