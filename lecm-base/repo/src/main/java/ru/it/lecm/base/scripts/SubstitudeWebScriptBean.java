package ru.it.lecm.base.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.SubstitudeBeanImpl;

/**
 * @author dbashmakov
 *         Date: 28.12.12
 *         Time: 15:10
 */
public class SubstitudeWebScriptBean extends BaseScopableProcessorExtension {
	private SubstitudeBeanImpl service;

	public SubstitudeBeanImpl getService() {
		return service;
	}

	public void setService(SubstitudeBeanImpl service) {
		this.service = service;
	}

	public String formatNodeTitle(ScriptNode node, String title){
		return service.formatNodeTitle(node.getNodeRef(), title);
	}

	public String formatNodeTitle(String nodeRef, String title){
		return service.formatNodeTitle(new NodeRef(nodeRef), title);
	}
}
