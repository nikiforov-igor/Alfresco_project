package ru.it.lecm.modelEditor.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.FileNameValidator;
import ru.it.lecm.base.beans.BaseBean;

/**
 * User: AIvkin
 * Date: 18.11.13
 * Time: 10:10
 */
public class FormsEditorBeanImpl extends BaseBean {
	public static final String FORMS_EDITOR_ROOT_NAME = "Сервис Реадктор форм";
	public static final String FORMS_EDITOR_ROOT_ID = "FORMS_EDITOR_ROOT_ID";

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(FORMS_EDITOR_ROOT_ID);
	}

	/**
	 * Получение папки с формами для модели
	 * @param modelName имя модели
	 * @return NodeRef папки модели
	 */
	public NodeRef getModelRootFolder(String modelName) {
		String folderName = FileNameValidator.getValidFileName(modelName);
		NodeRef parent = getServiceRootFolder();
		NodeRef folder = getFolder(parent, folderName);
		if (folder == null) {
			return createFolder(parent, folderName);
		} else {
			return folder;
		}
	}
}
