package ru.it.lecm.delegation;

import org.json.JSONException;
import org.json.JSONObject;

public interface ITestSearch {

	JSONObject test(JSONObject args);

	JSONObject runTest(int testnum) throws JSONException;

	void setConfig(final JSONObject config) throws JSONException;
}
