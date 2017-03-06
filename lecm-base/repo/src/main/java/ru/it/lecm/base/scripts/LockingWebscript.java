package ru.it.lecm.base.scripts;

import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ikhalikov
 */
public class LockingWebscript extends DeclarativeWebScript {

	private LockService lockService;
	private final static Logger logger = LoggerFactory.getLogger(LockingWebscript.class);
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", false);
		final NodeRef nodeRef = new NodeRef(req.getParameter("nodeRef"));
		try {
			lockService.unlock(nodeRef);
		} catch (Exception e) {
			logger.error("Error while unlocking node", e);
		}
		result.put("success", true);
		return result;
	}

	public void setLockService(LockService lockService) {
		this.lockService = lockService;
	}
}
