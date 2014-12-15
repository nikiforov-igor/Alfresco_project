package ru.it.lecm.reports.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.reports.api.ReportInfo;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportFileData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReportManagerJavascriptExtension extends BaseWebScript {
    public final static String REPORTS_EDITOR_URI = "http://www.it.ru/logicECM/reports/editor/1.0";
    public final static QName PROP_REPORT_DESCRIPTOR_IS_DEPLOYED = QName.createQName(REPORTS_EDITOR_URI, "reportIsDeployed");

    private static final transient Logger logger = LoggerFactory.getLogger(ReportManagerJavascriptExtension.class);

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
            result = getReportsManager().registerReportDescriptor(rdId);
        }
        logger.warn(String.format("report '%s' %sdeployed", reportDescNode, (result ? "" : "NOT ")));
        return result;
    }

    public boolean undeployReport(final String reportCode) {
        PropertyCheck.mandatory(this, "reportsManager", getReportsManager());

        logger.info(String.format("Undeploying report '%s' ...", reportCode));
        getReportsManager().unregisterReportDescriptor(reportCode);
        NodeRef report = getReportsManager().getReportEditorDAO().getReportDescriptorNodeByCode(reportCode);
        if (report != null && serviceRegistry.getNodeService().exists(report)) {
            serviceRegistry.getNodeService().setProperty(report, PROP_REPORT_DESCRIPTOR_IS_DEPLOYED, false);
        }
        logger.warn(String.format("report '%s' undeployed", reportCode));
        return true;
    }

    @SuppressWarnings("unused")
    public List<ReportInfo> getRegisteredReports(String docTypes, boolean forCollection) {
        return getRegisteredReports(docTypes, forCollection, null, false);
    }

    public List<ReportInfo> getRegisteredReports(String docTypes, boolean forCollection, String reportCodes, boolean dontFilterByRole) {
        PropertyCheck.mandatory(this, "reportsManager", getReportsManager());

        final List<ReportInfo> reports = new ArrayList<ReportInfo>();

        final String[] types = (docTypes != null && !docTypes.isEmpty()) ? docTypes.split(",") : null;
        final String[] codes = (reportCodes != null && !reportCodes.isEmpty()) ? reportCodes.split(",") : null;
        if (codes != null) {
            Arrays.sort(codes);
        }

        final List<ReportDescriptor> found = getReportsManager().getRegisteredReports(types, forCollection, dontFilterByRole);
        if (found != null) {
            for (ReportDescriptor rd : found) {
                String rdMnem = rd.getMnem();
                if (codes != null && Arrays.binarySearch(codes, rdMnem) < 0) {
                    continue;   //если задана фильтрация по кодам - отфильтровываем
                }
                final ReportInfo ri = new ReportInfo(
                        rdMnem,
                        (rd.getFlags() == null) ? null
                                : StringUtils.collectionToCommaDelimitedString(rd.getFlags().getSupportedNodeTypes())
                );
                ri.setReportName(rd.get(null, rdMnem));
                reports.add(ri);
            }
        }

        return reports;
    }

    public ScriptNode generateReportTemplate(final String reportRef) {
        PropertyCheck.mandatory(this, "reportsManager", getReportsManager());

        final NodeRef report = new NodeRef(reportRef);
        final NodeRef templateFileRef = getReportsManager().produceDefaultTemplate(report);
        return new ScriptNode(templateFileRef, serviceRegistry, getScope());
    }

    /**
     * Сгенерировать отчёт и сохранить его в указанном каталоге репозитория как
     *
     * @param reportCode    код отчёта для построения
     * @param destFolderRef папка репозитория для сохранения, не может быть null
     * @param args          аргументы для построения отчёта
     * @return nodeRef созданного узла
     */
    public ScriptNode buildReportAndSave(final String reportCode, final String templateCode, final String destFolderRef, Map<String, String> args) {
        PropertyCheck.mandatory(this, "reportsManager", getReportsManager());
        PropertyCheck.mandatory(this, "reportCode", reportCode);
        PropertyCheck.mandatory(this, "templateCode", templateCode);
        PropertyCheck.mandatory(this, "destFolderRef", destFolderRef);

        ReportFileData result;
        try {
            result = getReportsManager().generateReport(reportCode, templateCode, args);
        } catch (IOException ex) {
            final String msg = String.format("Exception at buildReportAndSave(reportCode='%s', destFolder={%s}), args:\n\t%s", reportCode, destFolderRef, args);
            logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }

        if (result == null || result.getData() == null) {
            logger.warn(String.format("Built report '%s' result returns %s !?"
                    , reportCode, (result == null ? "NULL" : "data NULL")));
            return null;
        }

        logger.info(String.format("built report info:\n\t mimeType: %s\n\t filename: %s\n\t dataSize: %s bytes"
                , result.getMimeType(), result.getFilename(), (result.getData() != null ? result.getData().length : "NULL")));

        final NodeRef folder = new NodeRef(destFolderRef);
        // сохранение внутри folder ...
        final NodeRef resultRef = getReportsManager().storeAsContent(result, folder);

        return new ScriptNode(resultRef, serviceRegistry, getScope());
    }
}
