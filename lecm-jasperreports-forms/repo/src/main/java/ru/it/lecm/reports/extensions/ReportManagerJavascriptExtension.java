package ru.it.lecm.reports.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.reports.api.ReportInfo;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;

import java.util.ArrayList;
import java.util.List;

public class ReportManagerJavascriptExtension extends BaseWebScript {
    public final static String REPORTS_EDITOR_URI = "http://www.it.ru/logicECM/reports/editor/1.0";
    public final static QName PROP_REPORT_DESCRIPTOR_IS_DEPLOYED = QName.createQName(REPORTS_EDITOR_URI, "reportIsDeployed");

    private ReportsManager reportsManager;

    public ReportsManager getReportsManager() {
        return reportsManager;
    }

    public void setReportsManager(ReportsManager reportsManager) {
        this.reportsManager = reportsManager;
    }

    public boolean deployReport(final String reportDescNode) {
        PropertyCheck.mandatory(this, "reportsManager", getReportsManager());

        boolean result = false;
        if (NodeRef.isNodeRef(reportDescNode)) {
            final NodeRef rdId = new NodeRef(reportDescNode);
            getReportsManager().registerReportDescriptor(rdId);
            result = true;
            serviceRegistry.getNodeService().setProperty(rdId, PROP_REPORT_DESCRIPTOR_IS_DEPLOYED, result);
        }
        return result;
    }

    public boolean undeployReport(final String reportCode) {
        PropertyCheck.mandatory(this, "reportsManager", getReportsManager());

        getReportsManager().unregisterReportDescriptor(reportCode);
        NodeRef report = getReportsManager().getReportDAO().getReportDescriptorByCode(reportCode);
        if (report != null) {
            serviceRegistry.getNodeService().setProperty(report, PROP_REPORT_DESCRIPTOR_IS_DEPLOYED, false);
        }
        return true;
    }

    public byte[] getDsXmlBytes(final String reportCode) {
        PropertyCheck.mandatory(this, "reportsManager", getReportsManager());
        final byte[] result = getReportsManager().loadDsXmlBytes(reportCode);
        return result;
    }

    public List<ReportInfo> getRegisteredReports(String docType, String reportType) {
        PropertyCheck.mandatory(this, "reportsManager", getReportsManager());
        List<ReportInfo> reports = new ArrayList<ReportInfo>();
        final List<ReportDescriptor> found = getReportsManager().getRegisteredReports(docType, reportType);
        if (found != null && !found.isEmpty()) {
            for (ReportDescriptor rd : found) {
                final ReportInfo ri = new ReportInfo(
                        rd.getReportType()
                        , rd.getMnem()
                        , (rd.getFlags() != null) ? rd.getFlags().getPreferedNodeType() : null
                );
                ri.setReportName(rd.get(null, rd.getMnem()));
                reports.add(ri);
            }
        }

        return reports;
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
