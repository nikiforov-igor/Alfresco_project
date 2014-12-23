package ru.it.lecm.modelEditor.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.modelEditor.beans.ControlsEditorBeanImpl;

/**
 *
 * @author vmalygin
 */
public class ControlsEditorWebScriptBean extends BaseWebScript {

	private ControlsEditorBeanImpl controlsEditorService;

	public void setControlsEditorService(ControlsEditorBeanImpl controlsEditorService) {
		this.controlsEditorService = controlsEditorService;
	}

	public ScriptNode getTypeRootFolder(final String typename) {
		NodeRef folderRef = controlsEditorService.getTypeRootFolder(typename);
		return (folderRef != null) ? new ScriptNode(folderRef, serviceRegistry, getScope()) : null;
	}

	public ScriptNode createTypeRootFolder(final String typename) throws WriteTransactionNeededException {
		NodeRef folderRef = controlsEditorService.createTypeRootFolder(typename);
		return (folderRef != null) ? new ScriptNode(folderRef, serviceRegistry, getScope()) : null;
	}

	public boolean generateControls(final String typename) {
		ParameterCheck.mandatory("typename", typename);
		return controlsEditorService.generateControls(typename);
	}
}
