package ru.it.lecm.reports.extensions;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.IOUtils;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.reports.api.ReportInfo;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

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

    public List<ReportInfo> getRegisteredReports(String docTypes, boolean forCollection) {
        List<ReportInfo> reports = new ArrayList<ReportInfo>();
        String[] types;
        List<ReportDescriptor> found;
        if (docTypes != null && docTypes.length() > 0) { // задан тип(типы) - значит фильтруем по ним
            types = docTypes.split(",");
            found = getReportsManager().getRegisteredReports(types, forCollection);
        } else {
            found = getReportsManager().getRegisteredReports(); // нет типов - берем все зарегистрированные
        }
        if (found != null && !found.isEmpty()) {
            for (ReportDescriptor rd : found) {
                final ReportInfo ri = new ReportInfo(rd.getReportType(), rd.getMnem(), (rd.getFlags() != null) ? rd.getFlags().getPreferedNodeType() : null);
                ri.setReportName(rd.get(null, rd.getMnem()));
                reports.add(ri);
            }
        }
        return reports;
    }

    public ScriptNode generateReportTemplate(final String reportRef) {
        NodeRef report = new NodeRef(reportRef);
        ReportDescriptor desc = (getReportsManager()).getReportDAO().getReportDescriptor(report);
        if (desc != null) {
            byte[] content = getReportsManager().produceDefaultTemplate(desc);
            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
            final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
            properties.put(ContentModel.PROP_NAME, desc.getMnem() + ".jrxml");
            ChildAssociationRef child =
                    serviceRegistry.getNodeService().createNode(new NodeRef(reportRef), ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_CONTENT, properties);
            InputStream is = null;
            try {
                ContentService contentService = serviceRegistry.getContentService();
                is = new ByteArrayInputStream(content);
                ContentWriter writer = contentService.getWriter(child.getChildRef(), ContentModel.PROP_CONTENT, true);
                writer.setEncoding("UTF-8");
                writer.setMimetype("text/xml");
                writer.putContent(is);
            } finally {
                IOUtils.closeQuietly(is);
            }
            return new ScriptNode(child.getChildRef(), serviceRegistry, getScope());
        }
        return null;
    }
}
