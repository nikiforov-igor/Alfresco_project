package ru.it.lecm.modelEditor.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

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

	public NodeRef getTypeRootFolder(final String typename) {
		String folderName = typename.replace(":", "_");
		NodeRef parent = getServiceRootFolder();
		return getFolder(parent, folderName);
	}

	public NodeRef createTypeRootFolder(final String typename) throws WriteTransactionNeededException {
		String folderName = typename.replace(":", "_");
		NodeRef parent = getServiceRootFolder();
		return createFolder(parent, folderName);
	}
}
