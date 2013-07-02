package ru.it.lecm.reports.editor.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.reports.editor.ReporstEditorService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 19.06.13
 * Time: 11:11
 */
public class ReportsEditorWebScriptBean extends BaseWebScript {

    private ReporstEditorService service;

    public void setService(ReporstEditorService service) {
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
}
