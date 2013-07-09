package ru.it.lecm.delegation.beans;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

import ru.it.lecm.delegation.ITestSearch;
import ru.it.lecm.delegation.IWebScriptDelegation;

/**
 *
 * @author VLadimir Malygin
 * @since 18.10.2012 14:41:08
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class DelegationRepoWebScript extends DeclarativeWebScript implements IWebScriptDelegation {

	private final static Logger logger = LoggerFactory.getLogger (DelegationRepoWebScript.class);

	private final static String ACTION_DEF = "action";

	/**
	 * Enum с возможными вариантами действий, которые может выполнить вебскрипт
	 * вариант действия указывается в URLе вебскрипта, см DelegationRepoWebScript.post.desc.xml
	 */
	private static enum Action {
		test;

		public boolean equals (String obj) {
			return this.toString().equalsIgnoreCase(obj);
		}
	}

	private static RuntimeException createAndRegException (final Throwable t, final String info) {
		logger.error (info, t);
		return new WebScriptException (info, t);
	}

	private ITestSearch tester;

	public void setTester(ITestSearch tester) {
		this.tester = tester;
	}

	@Override
	// TODO: possibly NOT temporary method
	public String test(String /*JSONObject*/ args) {
		try {
			final JSONObject jargs = new JSONObject(args);
			final JSONObject result = tester.test( jargs);
			return result.toString();
		} catch (JSONException ex) {
			throw createAndRegException( ex, "error processing test with args:\n"+ args);
		}
	}

	@Override
	protected Map<String, Object> executeImpl (WebScriptRequest req, Status status, Cache cache) {

		logger.debug ("executing delegation webscript");
		logger.debug ( String.format( "http session is %s", ServletUtil.getSession ()));

		final Map<String, String> templateArgs = req.getServiceMatch ().getTemplateVars ();
		final String action = templateArgs.get (ACTION_DEF);

		final Content content = req.getContent();
		if (content == null) {
			throw new WebScriptException (String.format ("DelegationRepoWebScript was invoked with empty json content, action= %s", action));
		}

		String jsonContent;
		try {
			jsonContent = content.getContent ();
		} catch (IOException ex) {
			jsonContent = "{}";
			logger.warn ("can't get content as json string", ex);
		}


		HashMap<String, Object> model = new HashMap<String, Object> ();
		if (Action.test.equals (action)) {
			model.put ("model", test(jsonContent));

		} else {
			throw new WebScriptException (String.format ("DelegationRepoWebScript was invoked with unknown template arg: %s", action));
		}

		return model;
	}


}
