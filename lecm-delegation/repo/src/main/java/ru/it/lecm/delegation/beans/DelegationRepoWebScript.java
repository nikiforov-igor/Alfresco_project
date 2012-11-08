package ru.it.lecm.delegation.beans;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

import ru.it.lecm.delegation.IWebScriptDelegation;

/**
 *
 * @author VLadimir Malygin
 * @since 18.10.2012 14:41:08
 * @see <p>mailto: <a href="mailto:vladimir.malygin@aplana.com">vladimir.malygin@aplana.com</a></p>
 */
public class DelegationRepoWebScript
		extends DeclarativeWebScript
		implements IWebScriptDelegation
{

	private final static Logger logger = LoggerFactory.getLogger (DelegationRepoWebScript.class);

	private final static String ACTION_DEF = "action";

	/**
	 * Enum с возможными вариантами действий, которые может выполнить вебскрипт
	 * вариант действия указывается в URLе вебскрипта, см DelegationRepoWebScript.post.desc.xml
	 */
	private static enum Action {
		createDummy,
		create,
		get,

		find,
		update,
		delete,

		getrootnode,
		test;

		public boolean equals (String obj) {
			return this.toString().equalsIgnoreCase(obj);
		}
	};

	private IWebScriptDelegation delegationService;


	public IWebScriptDelegation getDelegationService() {
		return this.delegationService;
	}

	public void setDelegationService (IWebScriptDelegation service) {
		this.delegationService = service;
	}

	private String createDummy () {
		final JSONObject dummy = new JSONObject ();
		try {
			dummy.put ("id", UUID.randomUUID ());
			dummy.put ("name", "someData");
			dummy.put ("title", "this is some data. It is unique by it's id and can be serialized to json");
			dummy.put ("date", new Date ());
		} catch (Exception ex) { // JSONException
			logger.error (ex.getMessage (), ex);
			// dummy.put("message", ex.toString());
			return  "{}";
		}
		return dummy.toString();
	}

	@Override
	public String getProcuracyRootNodeRef() {
		return delegationService.getProcuracyRootNodeRef();
	}

	@Override
	public String deleteProcuracy(String argId) {
		return delegationService.deleteProcuracy(argId);
	}

	@Override
	public String createProcuracy(String args) {
		return delegationService.createProcuracy(args);
	}

	@Override
	public String getProcuracy(String argId) {
		return delegationService.getProcuracy(argId);
	}

	@Override
	public String findProcuracyList(String searchArgs) {
		return delegationService.findProcuracyList(searchArgs);
	}

	@Override
	public String updateProcuracy(String args) {
		return delegationService.updateProcuracy(args);
	}

	@Override
	public String test(String args) {
		return delegationService.test(args);
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
		if (Action.getrootnode.equals (action)) {
			model.put ("model", getProcuracyRootNodeRef());

		} else if (Action.createDummy.equals (action)) {
			model.put ("model", createDummy());

		} else if (Action.create.equals (action)) {
			model.put ("model", createProcuracy (jsonContent));

		} else if (Action.get.equals (action)) {
			model.put ("model", getProcuracy (jsonContent));

		} else if (Action.find.equals (action)) {
			model.put ("model", findProcuracyList (jsonContent));

		} else if (Action.update.equals (action)) {
			model.put ("model", updateProcuracy (jsonContent));

		} else if (Action.delete.equals (action)) {
			model.put ("model", deleteProcuracy (jsonContent));

		} else if (Action.test.equals (action)) {
			model.put ("model", test(jsonContent));

		} else {
			throw new WebScriptException (String.format ("DelegationRepoWebScript was invoked with unknown template arg: %s", action));
		}

		return model;
	}


}
