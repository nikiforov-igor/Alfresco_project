package ru.it.lecm.modelEditor.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;

/**
 *
 * @author vmalygin
 */
public class ControlsEditorBeanImpl extends BaseBean {

	public static final String CONTROLS_EDITOR_ROOT_ID = "CONTROLS_EDITOR_ROOT_ID";

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(CONTROLS_EDITOR_ROOT_ID);
	}

}
