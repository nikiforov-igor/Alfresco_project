package ru.it.lecm.reports.editor.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.editor.ReportsEditorModel;
import ru.it.lecm.reports.editor.ReportsEditorService;
import ru.it.lecm.reports.model.impl.ReportTemplate;
import ru.it.lecm.reports.model.impl.SubReportDescriptorImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 19.06.13
 * Time: 11:11
 */
public class ReportsEditorWebScriptBean extends BaseWebScript {

    final static protected Logger logger = LoggerFactory.getLogger(ReportsEditorWebScriptBean.class);

    private ReportsEditorService service;

    public void setService(ReportsEditorService service) {
        this.service = service;
    }

    public ScriptNode getRoot() {
        return new ScriptNode(service.getServiceRootFolder(), serviceRegistry, getScope());
    }

    public ScriptNode getReportsRoot() {
        return new ScriptNode(service.getReportsRootFolder(), serviceRegistry, getScope());
    }

    public ScriptNode getSourcesRoot() {
        return new ScriptNode(service.getSourcesRootFolder(), serviceRegistry, getScope());
    }

    public ScriptNode getTemplatesRoot() {
        return new ScriptNode(service.getTemplatesRootFolder(), serviceRegistry, getScope());
    }

    public ScriptNode getDictionariesRoot() {
        return new ScriptNode(service.getDictionariesRootFolder(), serviceRegistry, getScope());
    }

    public Scriptable getReportTypes() {
        List<NodeRef> refs = service.getReportTypes();
        return createScriptable(refs);
    }

    public Scriptable getDataSources() {
        List<NodeRef> refs = service.getDataSources();
        return createScriptable(refs);
    }

    public Scriptable getTemplates() {
        List<NodeRef> refs = service.getTemplates();
        return createScriptable(refs);
    }

    public List<JSONObject> getReportTemplates(String reportId, boolean fromParent) {
        NodeRef reportRef;
        List<JSONObject> results = new ArrayList<JSONObject>();
        if (NodeRef.isNodeRef(reportId)) {
            reportRef = new NodeRef(reportId);
            if (fromParent) {
                reportRef = serviceRegistry.getNodeService().getPrimaryParent(reportRef).getParentRef();
            }
            List<NodeRef> templatesRefs = service.getReportTemplates(reportRef);
            for (NodeRef templateRef : templatesRefs) {
                JSONObject templateObj = new JSONObject();
                try {
                    templateObj.put("name", serviceRegistry.getNodeService().getProperty(templateRef, ContentModel.PROP_NAME));
                    templateObj.put("code", serviceRegistry.getNodeService().getProperty(templateRef, ReportsEditorModel.PROP_RTEMPLATE_CODE));
                    templateObj.put("nodeRef", templateRef.toString());

                    results.add(templateObj);
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } else {
            Map<String, ReportDescriptor> descriptors = service.getReportsManager().getDescriptors();
            ReportDescriptor descriptor = descriptors.get(reportId);
            if (descriptor != null) {
                if (fromParent && descriptor.isSubReport()) {
                    descriptor = ((SubReportDescriptorImpl) descriptor).getOwnerReport();
                    if (descriptor == null) {
                        return results;
                    }
                }
                List<ReportTemplate> templates = descriptor.getReportTemplates();
                if (templates != null) {
                    for (ReportTemplate reportTemplate : templates) {
                        JSONObject templateObj = new JSONObject();
                        try {
                            templateObj.put("name", reportTemplate.getDefault());
                            templateObj.put("code", reportTemplate.getMnem());
                            templateObj.put("nodeRef", "");

                            results.add(templateObj);
                        } catch (JSONException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
        return results;
    }
}
