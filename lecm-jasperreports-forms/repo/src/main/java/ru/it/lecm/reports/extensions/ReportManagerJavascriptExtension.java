package ru.it.lecm.reports.extensions;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.reports.api.ReportsManager;

public class ReportManagerJavascriptExtension extends BaseWebScript {

	private ReportsManager reportsManager;

	public ReportsManager getReportsManager() {
		return reportsManager;
	}

	public void setReportsManager(ReportsManager reportsManager) {
		this.reportsManager = reportsManager;
	}

	public Scriptable deployReport(final String refReportDesc) {
		PropertyCheck.mandatory (this, "reportsManager", getReportsManager());

		String result = "nothing";
		if (NodeRef.isNodeRef (refReportDesc)) {
			final NodeRef rdId = new NodeRef(refReportDesc);
			getReportsManager().registerReportDescriptor(rdId);
			result = "ok";
		}
		final Scriptable scriptable = Context.getCurrentContext ().newArray (getScope (), new Object[] {result});
		return scriptable;
	}

	public Scriptable getDsXmlBytes(final String reportCode) {
		PropertyCheck.mandatory (this, "reportsManager", getReportsManager());

		final byte[] result = getReportsManager().loadDsXmlBytes(reportCode);
		final Scriptable scriptable = Context.getCurrentContext ().newArray (getScope (), new Object[] {result});
		return scriptable;
	}
	
}
