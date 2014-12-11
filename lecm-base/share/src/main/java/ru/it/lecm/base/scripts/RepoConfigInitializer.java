package ru.it.lecm.base.scripts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.webscripts.*;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;
import ru.it.lecm.base.config.StringConfigSource;

import java.util.HashMap;
import java.util.Map;
import ru.it.lecm.base.formsConfig.FormsConfig;

/**
 * User: AIvkin
 * Date: 14.11.13
 * Time: 14:55
 */
public class RepoConfigInitializer extends DeclarativeWebScript {
	private final static Log logger = LogFactory.getLog(RepoConfigInitializer.class);

	private ConfigService configService;
	private ScriptRemote scriptRemote;
	private FormsConfig formsConfig;

	private boolean hasBeenInitialized = false;

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public void setScriptRemote(ScriptRemote scriptRemote) {
		this.scriptRemote = scriptRemote;
	}

	public void setFormsConfig(FormsConfig formsConfig) {
		this.formsConfig = formsConfig;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> result = new HashMap<String, Object>();

		String reset = req.getParameter("reset");

		if (!this.hasBeenInitialized || (reset != null && reset.equals("true"))) {
			String url = "/lecm/base/getRepoShareConfig";
			Response response = scriptRemote.connect("alfresco").get(url);
			if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
				StringConfigSource cs = new StringConfigSource(response.getResponse());

				if (reset != null && reset.equals("true")) {
					this.configService.reset();
					formsConfig.init();
				}

				this.configService.appendConfig(cs);

				this.hasBeenInitialized = true;
			} else {
				logger.warn("Cannot get share config from repo");
			}
			result.put("success", true);
		} else {
			result.put("success", false);
		}

		return result;
	}
}
