package ru.it.lecm.delegation;

import org.json.JSONException;
import org.json.JSONObject;

public interface ITestSearch {

	JSONObject runTest(int testnum) throws JSONException;
}
