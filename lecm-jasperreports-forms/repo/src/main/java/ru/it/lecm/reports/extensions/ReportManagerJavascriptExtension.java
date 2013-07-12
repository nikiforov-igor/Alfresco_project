package ru.it.lecm.reports.extensions;

import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;

import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.reports.api.ReportInfo;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.ScriptApiReportManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;

public class ReportManagerJavascriptExtension
		extends BaseWebScript 
		implements ScriptApiReportManager
{

	private ReportsManager reportsManager;

	public ReportsManager getReportsManager() {
		return reportsManager;
	}

	public void setReportsManager(ReportsManager reportsManager) {
		this.reportsManager = reportsManager;
	}

	@Override
	public boolean deployReport(final String reportDescNode) {
		PropertyCheck.mandatory (this, "reportsManager", getReportsManager());

		boolean result = false;
		if (NodeRef.isNodeRef (reportDescNode)) {
			final NodeRef rdId = new NodeRef(reportDescNode);
			getReportsManager().registerReportDescriptor(rdId);
			result = true;
		}
		// final Scriptable scriptable = Context.getCurrentContext ().newArray (getScope (), new Object[] {result});
		// return scriptable;
		return result;
	}

	@Override
	public boolean undeployReport(final String reportCode) {
		PropertyCheck.mandatory (this, "reportsManager", getReportsManager());

		getReportsManager().unregisterReportDescriptor(reportCode);
		// final Scriptable scriptable = Context.getCurrentContext ().newArray (getScope (), new Object[] {result});
		// return scriptable;
		return true;
	}

	@Override
	public byte[] getDsXmlBytes(final String reportCode) {
		PropertyCheck.mandatory (this, "reportsManager", getReportsManager());

		final byte[] result = getReportsManager().loadDsXmlBytes(reportCode);
		// final Scriptable scriptable = Context.getCurrentContext ().newArray (getScope (), new Object[] {result});
		// return scriptable;
		return result;
	}

	@Override
	public ReportInfo[] getRegisteredReports(String docType,
			String reportType) {
		PropertyCheck.mandatory (this, "reportsManager", getReportsManager());

		final List<ReportDescriptor> found = getReportsManager().getRegisteredReports(docType, reportType);
		if (found == null || found.isEmpty())
			return null;
		final ReportInfo[] result = new ReportInfo[ found.size()];
		int i = 0;
		for(ReportDescriptor rd: found) {
			final ReportInfo ri = new ReportInfo( 
					rd.getReportType()
					, rd.getMnem()
					, (rd.getFlags() != null) ? rd.getFlags().getPreferedNodeType() : null
			);
			result[i++] = ri;
		}
		return result;
	}
}
