package ru.it.lecm.reports.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.json.simple.JSONObject;
import org.mozilla.javascript.NativeObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.json.JSONUtils;
import org.springframework.util.StringUtils;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.reports.api.ReportInfo;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;

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

    /**
     * Метод для групповой операции
     */
    public boolean deployReport(final String reportDescNode, boolean throwException) throws Exception{
        boolean result = deployReport(reportDescNode);
        if (!result && throwException) {
            throw new Exception("Deploy report failed!");
        }
        return true;
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
        NodeRef resultRef = reportsManager.buildReportAndSave(reportCode, templateCode, destFolderRef, args);
        return new ScriptNode(resultRef, serviceRegistry, getScope());
    }

    /**
     * Сгенерировать отчёт и добавить его в категорию вложений указанного документа
     *
     * @param document           документ в котороый
     * @param reportCode         код отчета для построяния
     * @param attachmentCategory название категории вложений
     * @param filename           имя генерируемого файла
     * @param existsPolicy       поведение при нахождении в категории вложения контента с таким же именем.
                                 Возможные значения :
                                    1) CREATE_NEW_VERSION - создать новую версию документа вложения
                                    2) CREATE_NEW_FILE - создать новое вложение, к имени файла приписывается цифра
                                    3) REWRITE_FILE - удалить старое вложение и создать новое
                                    4) SKIP - пропустить
                                    5) RETURN_ERROR - вернуть ошибку
     * @return NativeObject
     */
    public NativeObject buildReportAndAttachToDocument(NodeRef document, String reportCode, String attachmentCategory, String filename, String existsPolicy) {

        String error = "";
        ScriptNode resultNode = null;

        try {
            NodeRef resultRef = reportsManager.buildReportAndAttachToDocumentCategory(document, reportCode, null, attachmentCategory, filename, ReportsManager.AttachmentExistsPolicy.valueOf(existsPolicy));
            if (resultRef != null) {
                resultNode = new ScriptNode(resultRef, serviceRegistry, getScope());
            }
        } catch (DuplicateChildNodeNameException ex) {
            error = ex.getMessage();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", error);
        jsonObject.put("node", resultNode);

        JSONUtils jsonUtils = new JSONUtils();
        return jsonUtils.toObject(jsonObject);

    }
}
