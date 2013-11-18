package ru.it.lecm.modelEditor.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.modelEditor.beans.FormsEditorBeanImpl;

/**
 * User: AIvkin
 * Date: 18.11.13
 * Time: 10:25
 */
public class FormsEditorWebScriptBean extends BaseWebScript {

	private FormsEditorBeanImpl formsEditorService;

	public void setFormsEditorService(FormsEditorBeanImpl formsEditorService) {
		this.formsEditorService = formsEditorService;
	}

	/**
	 * Получение папки с формами для модели
	 * @param modelName имя модели
	 * @return папка модели
	 */
	public ScriptNode getModelRootFolder(String modelName) {
		ParameterCheck.mandatory("modelName", modelName);
		NodeRef folder = formsEditorService.getModelRootFolder(modelName);
		if (folder != null) {
			return new ScriptNode(folder, serviceRegistry, getScope());
		}
		return null;
	}
}
