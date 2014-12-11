package ru.it.lecm.reports.editor.scripts;

import org.alfresco.model.ContentModel;
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
import java.util.*;

/**
 * User: dbashmakov
 * Date: 10.12.13
 * Time: 12:49
 */
public class GetEditorMenuScript extends AbstractWebScript {
    final private static Logger logger = LoggerFactory.getLogger(GetEditorMenuScript.class);

    public static final String NODE_REF = "nodeRef";
    public static final String REDIRECT = "redirect";
    public static final String ACTIONS = "actions";
    public static final String ID = "id";
    public static final String CHILD_TYPE = "childType";
    public static final String TITLE = "title";
    public static final String LABEL = "label";
    public static final String IS_LEAF = "isLeaf";
    public static final String REPORT_CUSTOM = "report-custom";
    public static final String SUBREPORT_CUSTOM = "subreport-custom";

    private NodeService nodeService;
    private NamespaceService namespaceService;
    private ReportsEditorService reportsEditorService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setReportsEditorService(ReportsEditorService reportsEditorService) {
        this.reportsEditorService = reportsEditorService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        List<JSONObject> nodes = new ArrayList<JSONObject>();

        String nodeRef = req.getParameter(NODE_REF);
        String childType = req.getParameter(CHILD_TYPE);

        Comparator<JSONObject> comparator = new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = "";
                String valB = "";

                try {
                    valA = (String) a.get("label");
                    valB = (String) b.get("label");
                } catch (JSONException e) {
                    logger.error("JSONException in combineJSONArrays sort section", e);
                }

                return valA.compareTo(valB);
            }
        };

        if (nodeRef != null && NodeRef.isNodeRef(nodeRef)) {
            NodeRef currentRef = new NodeRef(nodeRef);
            if (childType == null || childType.isEmpty()) {
                childType = "custom";
            }
            if (childType.equals(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR.toPrefixString(namespaceService)) ||
                    childType.equals(ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR.toPrefixString(namespaceService)) ) {// список отчетов
                Set<QName> type = new HashSet<QName>();
                type.add(QName.createQName(childType, namespaceService));
                List<ChildAssociationRef> childNodes = nodeService.getChildAssocs(currentRef, type);
                for (ChildAssociationRef childNode : childNodes) {
                    String label = (String) nodeService.getProperty(childNode.getChildRef(), ContentModel.PROP_NAME);
                    String title = (String) nodeService.getProperty(childNode.getChildRef(), ReportsEditorModel.PROP_REPORT_DESRIPTOR_CODE);
                    Boolean isSub = childType.equals(ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR.toPrefixString(namespaceService));
                    nodes.add(
                            getJSONNode(childNode.getChildRef().getId(), childNode.getChildRef(),
                                    !isSub ? REPORT_CUSTOM : SUBREPORT_CUSTOM, label, title, "lecm/reports-editor/report-settings?reportId={reportId}",
                                    !isSub ? "reportActions" : null, false)
                    );
                }
            } else if (childType.equals(ReportsEditorModel.TYPE_REPORT_DATA_SOURCE.toPrefixString(namespaceService))) {// список НД
                Set<QName> type = new HashSet<QName>();
                type.add(QName.createQName(childType, namespaceService));
                List<ChildAssociationRef> childNodes = nodeService.getChildAssocs(currentRef, type);
                for (ChildAssociationRef childNode : childNodes) {
                    String label = (String) nodeService.getProperty(childNode.getChildRef(), ContentModel.PROP_NAME);
                    String title = (String) nodeService.getProperty(childNode.getChildRef(), ReportsEditorModel.PROP_DATA_SOURCE_CODE);
                    nodes.add(
                            getJSONNode(childNode.getChildRef().getId(), childNode.getChildRef(), "source-custom", label, title,
                                    "lecm/reports-editor/source-columns?sourceId=" + childNode.getChildRef().toString(), null, true)
                    );
                }
            } else if (childType.equals(REPORT_CUSTOM) || childType.equals(SUBREPORT_CUSTOM)) {
                nodes.add(getJSONNode("settings", currentRef, "-", "Общие настройки", null, "lecm/reports-editor/report-settings?reportId={reportId}", null, true));

                nodes.add(getJSONNode("source", currentRef, "-", "Настройки набора данных", null, "lecm/reports-editor/source-edit?reportId={reportId}", null, true));

                String type = ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR.toPrefixString(namespaceService);
                Set<QName> reportType = new HashSet<QName>();
                reportType.add(ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR);
                List<ChildAssociationRef> childReports = nodeService.getChildAssocs(currentRef, reportType);
                nodes.add(getJSONNode("subs", currentRef, type, "Вложенные отчеты", null, "lecm/reports-editor/subreports?reportId={reportId}", null, childReports.size() == 0));

                nodes.add(getJSONNode("template", currentRef, "-", "Настройки шаблона представления", null, "lecm/reports-editor/template-edit?reportId={reportId}", null, true));
            }
        } else { // корневой узел
            Set<QName> types = new HashSet<QName>();
            List<ChildAssociationRef> childs;

            NodeRef ref = reportsEditorService.getReportsRootFolder();
            String type = ReportsEditorModel.TYPE_REPORT_DESCRIPTOR.toPrefixString(namespaceService);

            types.add(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR);

            childs = nodeService.getChildAssocs(ref, types);
            nodes.add(getJSONNode("reports", ref, type, "Отчеты", "Список дескрипторов отчетов", "lecm/reports-editor/main", null, childs.isEmpty()));

            ref = reportsEditorService.getTemplatesRootFolder();
            type = ReportsEditorModel.TYPE_REPORT_TEMPLATE.toPrefixString(namespaceService);

            nodes.add(getJSONNode("templates", ref, type, "Шаблоны представления", "Список шаблонов представления", "lecm/reports-editor/templates", null, true));

            ref = reportsEditorService.getSourcesRootFolder();
            type = ReportsEditorModel.TYPE_REPORT_DATA_SOURCE.toPrefixString(namespaceService);

            types.clear();
            types.add(ReportsEditorModel.TYPE_REPORT_DATA_SOURCE);
            childs = nodeService.getChildAssocs(ref, types);
            nodes.add(getJSONNode("sources", ref, type, "Шаблоны наборов данных", "Список шаблонов наборов данных", "lecm/reports-editor/sources", null, childs.isEmpty()));
        }

        try {
            Collections.sort(nodes, comparator);

            res.setContentType("application/json");
            res.setContentEncoding(Charset.defaultCharset().displayName());
            res.getWriter().write(nodes.toString());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private JSONObject getJSONNode(String id, NodeRef nodeRef, String childType, String label, String title, String redirectPage, String showActions, boolean isLeaf) {
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
            node.put(ACTIONS, showActions != null ? showActions : "");
            node.put(REDIRECT, redirectPage != null ? redirectPage : "");
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        return node;
    }
}
