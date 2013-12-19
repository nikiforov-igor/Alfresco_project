package ru.it.lecm.base.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.SubstitudeBean;

import java.util.List;

/**
 * @author dbashmakov
 *         Date: 28.12.12
 *         Time: 15:10
 */
public class SubstitudeWebScriptBean extends BaseWebScript {
    private SubstitudeBean service;

    public void setService(SubstitudeBean service) {
        this.service = service;
    }

    public String formatNodeTitle(ScriptNode node, String title) {
        return service.formatNodeTitle(node.getNodeRef(), title);
    }

    public String formatNodeTitle(String nodeRef, String title) {
        return service.formatNodeTitle(new NodeRef(nodeRef), title);
    }

    public Scriptable getObjectsByTitle(ScriptNode node, String title) {
        List<NodeRef> results = service.getObjectsByTitle(node.getNodeRef(), title);
        return createScriptable(results);
    }

	public String getObjectDescription(String nodeRef) {
		return service.getObjectDescription(new NodeRef(nodeRef));
	}

	public String getObjectDescription(ScriptNode node) {
		return service.getObjectDescription(node.getNodeRef());
	}

}
