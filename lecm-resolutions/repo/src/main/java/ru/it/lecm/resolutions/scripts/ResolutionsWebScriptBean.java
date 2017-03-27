package ru.it.lecm.resolutions.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.resolutions.api.ResolutionsService;

import java.util.List;

/**
 * User: AIvkin
 * Date: 18.11.2016
 * Time: 14:43
 */
public class ResolutionsWebScriptBean extends BaseWebScript {
    private ResolutionsService resolutionsService;

    public ResolutionsService getResolutionsService() {
        return resolutionsService;
    }

    public void setResolutionsService(ResolutionsService resolutionsService) {
        this.resolutionsService = resolutionsService;
    }

    public Scriptable getResolutionClosers(ScriptNode resolution) {
        List<NodeRef> results = resolutionsService.getResolutionClosers(resolution.getNodeRef());
        if (results != null) {
            return createScriptable(results);
        }
        return null;
    }

    public void sendAnnulSignal(String resolutionRef, String reason) {
        NodeRef resolution = new NodeRef(resolutionRef);
        if (serviceRegistry.getNodeService().exists(resolution)) {
            resolutionsService.sendAnnulSignal(resolution, reason);
        }
    }

    /**
     * Возвращает NodeRef настроек дашлетов для резолюций
     * @return NodeRef настроек дашлетов для резолюций
     */
    public ScriptNode getDashletSettings() {
        NodeRef settings = resolutionsService.getDashletSettingsNode();
        if (settings != null) {
            return new ScriptNode(settings, serviceRegistry, getScope());
        }
        return null;
    }
}
