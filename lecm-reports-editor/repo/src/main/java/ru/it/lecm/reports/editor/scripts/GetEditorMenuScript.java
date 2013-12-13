package ru.it.lecm.reports.editor.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.preference.PreferenceService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.reports.editor.ReportsEditorModel;
import ru.it.lecm.reports.editor.ReportsEditorService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: dbashmakov
 * Date: 10.12.13
 * Time: 12:49
 */
public class GetEditorMenuScript extends AbstractWebScript {
    final private static Logger logger = LoggerFactory.getLogger(GetEditorMenuScript.class);

    public static final String NODE_REF = "nodeRef";
    public static final String REDIRECT = "redirect";
    public static final String ID = "id";
    public static final String CHILD_TYPE = "childType";
    public static final String TITLE = "title";
    public static final String LABEL = "label";
    public static final String IS_LEAF = "isLeaf";

    private NodeService nodeService;
    private NamespaceService namespaceService;
    private PreferenceService preferenceService;
    private ReportsEditorService reportsEditorService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setPreferenceService(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    public void setReportsEditorService(ReportsEditorService reportsEditorService) {
        this.reportsEditorService = reportsEditorService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        List<JSONObject> nodes = new ArrayList<JSONObject>();

        String nodeRef = req.getParameter(NODE_REF);
        String childType = req.getParameter(CHILD_TYPE);

        if (nodeRef != null && NodeRef.isNodeRef(nodeRef)) {
            NodeRef currentRef = new NodeRef(nodeRef);
            if (childType == null || childType.isEmpty()) {
                childType = "report-custom";
            }
            if (childType.equals(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR.toPrefixString(namespaceService))) {// список отчетов
                Set<QName> type = new HashSet<QName>();
                type.add(QName.createQName(childType, namespaceService));
                List<ChildAssociationRef> childNodes = nodeService.getChildAssocs(currentRef, type);
                for (ChildAssociationRef childNode : childNodes) {
                    String label = (String) nodeService.getProperty(childNode.getChildRef(), ContentModel.PROP_NAME);
                    String title = (String) nodeService.getProperty(childNode.getChildRef(), ReportsEditorModel.PROP_REPORT_DESRIPTOR_CODE);
                    nodes.add(
                            getJSONNode(childNode.getChildRef().getId(), childNode.getChildRef(), "report-custom", label, title, "report-settings?reportId={reportId}", false)
                    );
                }
            } else if (childType.equals("report-custom")) {
                nodes.add(getJSONNode("settings", currentRef, "-", "Общие настройки", null, "report-settings?reportId={reportId}", true));

                nodes.add(getJSONNode("source", currentRef, "-", "Настройки набора данных", null, "report-source-edit?reportId={reportId}", true));

                String type = ReportsEditorModel.TYPE_REPORT_DESCRIPTOR.toPrefixString(namespaceService);
                Set<QName> reportType = new HashSet<QName>();
                reportType.add(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR);
                List<ChildAssociationRef> childReports = nodeService.getChildAssocs(currentRef, reportType);
                nodes.add(getJSONNode("subs", currentRef, type, "Вложенные отчеты", null, "report-subreports?reportId={reportId}", childReports.size() == 0));

                nodes.add(getJSONNode("template", currentRef, "-", "Настройки шаблона представления", null, "report-template-edit?reportId={reportId}", true));
            }
        } else { // корневой узел
            NodeRef ref = reportsEditorService.getReportsRootFolder();
            String type = ReportsEditorModel.TYPE_REPORT_DESCRIPTOR.toPrefixString(namespaceService);
            nodes.add(getJSONNode("reports", ref, type, "Отчеты", "Список дескрипторов отчетов", "reports-editor", false));

            ref = reportsEditorService.getTemplatesRootFolder();
            type = ReportsEditorModel.TYPE_REPORT_TEMPLATE.toPrefixString(namespaceService);
            nodes.add(getJSONNode("templates", ref, type, "Шаблоны представления", "Список шаблонов представления", "reports-editor-templates", true));

            ref = reportsEditorService.getSourcesRootFolder();
            type = ReportsEditorModel.TYPE_REPORT_DATA_SOURCE.toPrefixString(namespaceService);
            nodes.add(getJSONNode("sources", ref, type, "Шаблоны наборов данных", "Список шаблонов наборов данных", "reports-editor-sources", true));

            ref = nodeService.getChildByName(reportsEditorService.getDictionariesRootFolder(), ContentModel.ASSOC_CONTAINS, "Тип провайдера");
            type = ReportsEditorModel.TYPE_REPORT_PROVIDER.toPrefixString(namespaceService);
            nodes.add(getJSONNode("providers", ref, type, "Провайдеры отчетов", "Список провайдеров отчетов", "reports-editor-providers", true));
        }

        try {
            res.setContentType("application/json");
            res.setContentEncoding(Charset.defaultCharset().displayName());
            res.getWriter().write(nodes.toString());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private JSONObject getJSONNode(String id, NodeRef nodeRef, String childType, String label, String title, String redirectPage, boolean isLeaf) {
        JSONObject node = new JSONObject();
        try {
            node.put(ID, id);
            node.put(NODE_REF, nodeRef != null ? nodeRef.toString() : "-");
            node.put(CHILD_TYPE, childType);
            node.put(LABEL, label);
            if (title == null) {
                title = label;
            }
            node.put(TITLE, title);
            node.put(IS_LEAF, isLeaf);
            node.put(REDIRECT, redirectPage);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        return node;
    }
}
