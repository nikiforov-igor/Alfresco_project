package ru.it.lecm.delegation.beans;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.delegation.IDelegation;

/**
 *
 * @author VLadimir Malygin
 * @since 18.10.2012 14:41:08
 * @see <p>mailto: <a href="mailto:vladimir.malygin@aplana.com">vladimir.malygin@aplana.com</a></p>
 */
public class DelegationRepoWebScript extends DeclarativeWebScript implements IDelegation {

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
		delete;

		public boolean equals (String obj) {
			return this.toString ().equals (obj);
		}
	};

	private IDelegation delegationService;

	private JSONObject createDummy () {
		try {
			JSONObject dummy = new JSONObject ();
			dummy.put ("id", UUID.randomUUID ());
			dummy.put ("name", "someData");
			dummy.put ("title", "this is some data. It is unique by it's id and can be serialized to json");
			dummy.put ("date", new Date ());
			return dummy;
		} catch (JSONException ex) {
			logger.error (ex.getMessage (), ex);
			return  null;
		}
	}

	@Override
	public String createProcuracy (JSONObject args) {
		return delegationService.createProcuracy (args);
	}

	@Override
	public JSONObject getProcuracy (String procuracyId) {
		return delegationService.getProcuracy (procuracyId);
	}

	@Override
	public JSONArray findProcuracyList (JSONObject searchArgs) {
		return delegationService.findProcuracyList (searchArgs);
	}

	@Override
	public void updateProcuracy (String procuracyId, JSONObject args) {
		delegationService.updateProcuracy (procuracyId, args);
	}

	@Override
	public void deleteProcuracy (String procuracyId) {
		delegationService.deleteProcuracy (procuracyId);
	}

	@Override
	protected Map<String, Object> executeImpl (WebScriptRequest req, Status status, Cache cache) {

		logger.debug ("executing delegation webscript");
		logger.debug ("http session is {}", ServletUtil.getSession ());

		Map<String, String> templateArgs = req.getServiceMatch ().getTemplateVars ();
		String action = templateArgs.get (ACTION_DEF);

//		String content = req.getContent ().getContent ();

		HashMap<String, Object> model = new HashMap<String, Object> ();
		if (Action.createDummy.equals (action)) {
			model.put ("model", createDummy ());
		} else if (Action.create.equals (action)) {
			model.put ("model", createProcuracy (null));
		} else if (Action.get.equals (action)) {
			model.put ("model", getProcuracy (null));
		} else if (Action.find.equals (action)) {
			model.put ("model", findProcuracyList (null));
		} else if (Action.update.equals (action)) {
			updateProcuracy (null, null);
			model.put ("model", "ok");
		} else if (Action.delete.equals (action)) {
			deleteProcuracy (null);
			model.put ("model", "ok");
		} else {
			throw new WebScriptException (String.format ("DelegationRepoWebScript was invoked with unknown template arg: %s.", action));
		}

		return model;
	}

	public void setDelegationService (IDelegation delegationService) {
		this.delegationService = delegationService;
	}
}
