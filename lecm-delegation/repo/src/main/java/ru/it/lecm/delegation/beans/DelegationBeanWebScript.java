package ru.it.lecm.delegation.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.delegation.IWebScriptDelegation;

public class DelegationBeanWebScript implements IWebScriptDelegation {

	private IDelegation proxy;
	private static Log logger = LogFactory.getLog(DelegationBean.class);


	static RuntimeException createAndRegException(final Throwable t, final String info) {
		logger.error( info, t);
		return new RuntimeException( info, t);
	}


	public IDelegation getProxy() {
		return proxy;
	}

	public void setProxy(IDelegation proxy) {
		this.proxy = proxy;
	}


	@Override
	public String createProcuracy(String args) {
		try {
			final String id = proxy.createProcuracy( new JSONObject(args));
			return makeSimpleJson("id", id);
		} catch (JSONException ex) {
			throw createAndRegException( ex, "error processing CreateProcuracy with args:\n"+ args);
		}
	}


	final static String makeSimpleJson(final String key, final String value) throws JSONException {
		final JSONObject result = new JSONObject();
		result.put(key, value);
		return result.toString();
	}


	@Override
	public String /*JSONObject*/ getProcuracy(String argId) {
		try {
			final JSONObject jargs = new JSONObject(argId);
			final JSONObject result = proxy.getProcuracy( jargs.getString("id"));
			return result.toString();
		} catch (JSONException ex) {
			throw createAndRegException( ex, "error processing getProcuracy with id="+ argId);
		}
	}

	@Override
	public String /*JSONArray*/ findProcuracyList(String /*JSONObject*/ searchArgs) {
		try {
			final JSONObject jargs = new JSONObject(searchArgs);
			final JSONArray result = proxy.findProcuracyList( jargs);
			return result.toString();
		} catch (JSONException ex) {
			throw createAndRegException( ex, "error processing findProcuracyList with args:\n"+ searchArgs);
		}
	}

	@Override
	public String updateProcuracy(String /*JSONObject*/ args) {
		try {
			final JSONObject jargs = new JSONObject(args);
			final String procuracyId = jargs.getString("id");
			jargs.remove("id");
			proxy.updateProcuracy(procuracyId, jargs);
			return makeSimpleJson("result", "ok");
		} catch (JSONException ex) {
			throw createAndRegException( ex, "error processing updateProcuracy with args:\n"+ args);
		}
	}

	@Override
	public String deleteProcuracy(String /*JSONObject*/ argId) {
		try {
			final JSONObject jargs = new JSONObject(argId);
			final String procuracyId = jargs.getString("id");
			proxy.deleteProcuracy(procuracyId);
			return makeSimpleJson("result", "ok");
		} catch (JSONException ex) {
			throw createAndRegException( ex, "error processing deleteProcuracy with id="+ argId);
		}
	}
}
