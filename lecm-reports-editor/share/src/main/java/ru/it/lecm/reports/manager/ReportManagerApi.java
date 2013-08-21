package ru.it.lecm.reports.manager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;

import ru.it.lecm.reports.api.ReportInfo;
import ru.it.lecm.reports.api.ScriptApiReportManager;
import ru.it.lecm.reports.model.impl.L18Value;
import ru.it.lecm.reports.model.impl.ReportTypeImpl;

public class ReportManagerApi
		implements ScriptApiReportManager
{

	private final static Log logger = LogFactory.getLog(ReportManagerApi.class);

	private ScriptRemote scriptRemote;

	public void setScriptRemote(ScriptRemote scriptRemote) {
		this.scriptRemote = scriptRemote;
	}

	@Override
	public boolean deployReport(String reportDescNode) {
		final String url = "/lecm/reports/rptmanager/deployReport?reportDescNode="+ reportDescNode;
		final Response response = scriptRemote.connect("alfresco").get(url);
		final String errmsg = String.format( "Cannot register at server report node '%s'", reportDescNode);
		try {
			if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
				final org.json.JSONObject resultJson = new JSONObject(response.getResponse());
				return resultJson.getBoolean("data");
			}
		} catch (JSONException e) {
			logger.warn( errmsg, e);
		}
		throw new RuntimeException( errmsg);
	}

	@Override
	public boolean undeployReport(String reportCode) {
		final String url = "/lecm/reports/rptmanager/undeployReport?reportCode="+ reportCode;
		final Response response = scriptRemote.connect("alfresco").get(url);
		final String errmsg = String.format( "Cannot unregister at server report node '%s'", reportCode);
		try {
			if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
				// final org.json.JSONObject resultJson = new JSONObject(response.getResponse());
				return true;
			}
		} catch (Exception e) {
			logger.warn( errmsg, e);
		}
		throw new RuntimeException( errmsg);
	}

	@Override
	public InputStream getDsXmlBytes(String reportCode) {
		final String url = "/lecm/reports/rptmanager/dsXmlBytes?reportCode=" + reportCode;
		final Response response = scriptRemote.connect("alfresco").get(url);
		if (response.getStatus().getCode() != ResponseStatus.STATUS_OK) {
			throw new RuntimeException(String.format("Cannot get ds-file for report '%s' from server", reportCode));
		}
		return response.getResponseStream();
	}

	@Override
	public InputStream generateReportTemplate(String reportRef) {
		final String url = "/lecm/reports/rptmanager/generateReportTemplate?reportRef=" + reportRef;

		final Response response = scriptRemote.connect("alfresco").get(url);
		if (response.getStatus().getCode() != ResponseStatus.STATUS_OK) {
			throw new RuntimeException(String.format("Cannot generate report template for descriptor '%s'", reportRef));
		}
		return response.getResponseStream();
	}

	@Override
	public List<ReportInfo> getRegisteredReports(String docType, String reportType) {
		final String url = "/lecm/reports/rptmanager/registeredReports";//?docType=" + docType + "&reportType=" + reportType;
		final Response response = scriptRemote.connect("alfresco").get(url);
		final String errmsg = String.format("Cannot get from server the list or reports: docType '%s', reportType '%s'"
				, docType, reportType);
		try {
			if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
				List<ReportInfo> results = new ArrayList<ReportInfo>();
				final JSONObject resultJson = new JSONObject(response.getResponse());
				JSONArray reportInfoArray = (JSONArray) resultJson.get("list");
				for (int i = 0; i < reportInfoArray.length(); i++) {
					JSONObject ri = (JSONObject) reportInfoArray.get(i);
					ReportInfo riFromReq = new ReportInfo(new ReportTypeImpl(ri.getString("reportType"), new L18Value()), ri.getString("code"));
					riFromReq.setDocumentType(ri.getString("docType"));
					riFromReq.setReportName(ri.getString("name"));
					results.add(riFromReq);
				}
				return results;
			}
		} catch (JSONException e) {
			logger.warn(errmsg, e);
		}
		throw new RuntimeException(errmsg);
	}

}

