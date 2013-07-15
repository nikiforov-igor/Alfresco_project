package ru.it.lecm.reports.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.reports.api.ReportInfo;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;

import java.util.List;

public class ReportManagerJavascriptExtension
		extends BaseWebScript
{
	private ReportsManager reportsManager;

	public ReportsManager getReportsManager() {
		return reportsManager;
	}

	public void setReportsManager(ReportsManager reportsManager) {
		this.reportsManager = reportsManager;
	}

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

	public boolean undeployReport(final String reportCode) {
		PropertyCheck.mandatory (this, "reportsManager", getReportsManager());

		getReportsManager().unregisterReportDescriptor(reportCode);
		// final Scriptable scriptable = Context.getCurrentContext ().newArray (getScope (), new Object[] {result});
		// return scriptable;
		return true;
	}

	public byte[] getDsXmlBytes(final String reportCode) {
		PropertyCheck.mandatory (this, "reportsManager", getReportsManager());

		final byte[] result = getReportsManager().loadDsXmlBytes(reportCode);
		// final Scriptable scriptable = Context.getCurrentContext ().newArray (getScope (), new Object[] {result});
		// return scriptable;
		return result;
	}

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
			ri.setReportName( rd.get(null, rd.getMnem()));
			result[i++] = ri;
		}
		return result;
	}

    public ScriptNode generateReportTemplate(final String reportRef) {
        /*ReportDescriptor desc = ((ReportsManagerImpl)getReportsManager()).getReportDAO().getReportDescriptor(new NodeRef(reportRef));
        byte[] content = getReportsManager().produceDefaultTemplate(desc);
        NodeRef templateFileRef = serviceRegistry.getNodeService().createNode(templateContainer, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "default"), ContentModel.TYPE_CONTENT, null).getChildRef();
        ByteArrayInputStream bis = null;
        try {
            bis = new ByteArrayInputStream(content);
            ContentService contentService = serviceRegistry.getContentService();
            ContentReader reader = contentService.getReader(templateFileRef, ContentModel.PROP_CONTENT);
            if (reader == null) {
                ContentWriter writer = contentService.getWriter(templateFileRef, ContentModel.PROP_CONTENT, true);
                writer.setMimetype("text/xml");
                writer.setEncoding("UTF-8");
                writer.putContent(bis);
            }
        } finally {
            IOUtils.closeQuietly(bis);
        }
        return new ScriptNode(templateFileRef, serviceRegistry, getScope());*/
        return null;
    }
}
