package ru.it.lecm.base.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.SubstitudeBean;

import java.util.List;

/**
 * @author dbashmakov
 *         Date: 28.12.12
 *         Time: 15:10
 */
public class SubstitudeWebScriptBean extends BaseScopableProcessorExtension {
	private SubstitudeBean service;
    private ServiceRegistry registry;

	public void setService(SubstitudeBean service) {
		this.service = service;
	}

	public String formatNodeTitle(ScriptNode node, String title){
		return service.formatNodeTitle(node.getNodeRef(), title);
	}

	public String formatNodeTitle(String nodeRef, String title){
		return service.formatNodeTitle(new NodeRef(nodeRef), title);
	}

    public Scriptable getObjectsByTitle(ScriptNode node, String title) {
        List<NodeRef> results = service.getObjectsByTitle(node.getNodeRef(), title);
        return createScriptable(results);
    }
    /**
     * Возвращает массив, пригодный для использования в веб-скриптах
     *
     * @return Scriptable
     */
    private Scriptable createScriptable(List<NodeRef> refs) {
        Object[] results = new Object[refs.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = new ScriptNode(refs.get(i), registry, getScope());
        }
        return Context.getCurrentContext().newArray(getScope(), results);
    }

    public void setRegistry(ServiceRegistry registry) {
        this.registry = registry;
    }
}
