package ru.it.lecm.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.sf.acegisecurity.Authentication;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptSession;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author VLadimir Malygin
 * @since 03.10.2012 17:27:25
 * @see <p>mailto: <a href="mailto:vladimir.malygin@aplana.com">vladimir.malygin@aplana.com</a></p>
 */
public class SampleWebScript extends DeclarativeWebScript implements ApplicationContextAware {

	private final static Log logger = LogFactory.getLog (SampleWebScript.class);
	private Repository repository;

	public void setRepository (Repository repository) {
		this.repository = repository;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

		WebScriptSession webScriptSession = req.getRuntime ().getSession ();
		logger.info (webScriptSession.getClass ().getName ());
		logger.info (webScriptSession.toString ());

//		if (!(req instanceof WebScriptServletRequest)) {
//			throw new WebScriptException ("Content retrieval must be executed in HTTP Servlet environment");
//		}


		HttpServletRequest httpReq = WebScriptServletRuntime.getHttpServletRequest (req);

		HttpSession session = httpReq.getSession ();
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext (session.getServletContext ());
		logger.info ("ctx id: " + ctx.getId ());
		logger.info ("ctx displayName: " + ctx.getDisplayName ());
		String[] beanDefNames = context.getBeanDefinitionNames ();
		for (String beanDefName : beanDefNames) {
			logger.info (beanDefName);
		}

		logger.info ("http session is " + session.getId ());
		List<String> attributeNames = EnumerationUtils.toList (session.getAttributeNames ());
		for (String attributeName : attributeNames) {
			Object attribute = session.getAttribute (attributeName);
			String className = attribute == null ? " " : attribute.getClass ().getName ();
			logger.info (String.format ("%s=[%s]%s", attributeName, className, attribute));
		}


//		HttpSession session = ServletUtil.getSession ();
//		logger.info ("http session is " + session.getId ());
//		List<String> attributeNames = EnumerationUtils.toList (session.getAttributeNames ());
//		for (String attributeName : attributeNames) {
//			Object attribute = session.getAttribute (attributeName);
//			String className = attribute == null ? " " : attribute.getClass ().getName ();
//			logger.info (String.format ("%s=[%s]%s", attributeName, className, attribute));
//		}

		Authentication auth = AuthenticationUtil.getFullAuthentication ();
		Object details = auth.getDetails ();
		logger.info (details.getClass ().getName ());
		logger.info (details.toString ());
		//org.alfresco.service.cmr.security.AuthenticationUtil.


		// extract folder listing arguments from URI
		String verboseArg = req.getParameter ("verbose");
		Boolean verbose = Boolean.parseBoolean (verboseArg);
		Map<String, String> templateArgs = req.getServiceMatch ().getTemplateVars ();
		String folderPath = templateArgs.get ("folderpath");
		NodeRef rootHome = repository.getRootHome ();
		NodeRef companyHome = repository.getCompanyHome ();
		NodeRef person = repository.getPerson ();
		NodeRef userHome = repository.getUserHome (person);
		logger.info ("Person is: " + person.toString ());
		logger.info ("Root Home is: " + rootHome.toString ());
		logger.info ("Company Home is: " + companyHome.toString ());
		logger.info ("User Home is: " + userHome.toString ());
		String nodePath = "workspace/SpacesStore/" + folderPath;
		//app:company_home
		NodeRef folder = repository.findNodeRef ("path", nodePath.split ("/"));
		// validate that folder has been found
		if (folder == null) {
			throw new WebScriptException (Status.STATUS_NOT_FOUND, "Folder " + folderPath + " not found");
		}

		// construct model for response template to render
		Map<String, Object> model = new HashMap<String, Object> ();
		model.put ("verbose", verbose);
		model.put ("folder", folder);
		return model;
	}
	private ApplicationContext context;

	@Override
	public void setApplicationContext (ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;

//		String[] beanDefNames = context.getBeanDefinitionNames ();
//		for (String beanDefName : beanDefNames) {
//			logger.info (beanDefName);
//		}

	}
	private AuthenticationService authService;

	public void setAuthenticationService (AuthenticationService authService) {
		this.authService = authService;
	}
}
