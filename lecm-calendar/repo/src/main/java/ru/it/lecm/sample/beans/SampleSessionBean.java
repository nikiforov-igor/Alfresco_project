package ru.it.lecm.sample.beans;

import net.sf.acegisecurity.Authentication;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author VLadimir Malygin
 * @since 08.10.2012 12:14:58
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class SampleSessionBean implements ISampleSession {

	private final static Log logger = LogFactory.getLog (SampleSessionWrapper.class);

	@Override
	public String getSessionInfo () {
		Authentication auth = AuthenticationUtil.getFullAuthentication ();
		logger.info ("name: " + auth.getName ());
		logger.info ("credentials: " + auth.getCredentials ());
		logger.info ("details: " + auth.getDetails ());
		logger.info ("principals: " + auth.getPrincipal ());
		return auth.toString ();
	}
}
