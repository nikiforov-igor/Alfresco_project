package ru.it.lecm.modelEditor.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.dictionary.*;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.modelEditor.beans.FormsEditorBeanImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	/**
	 * Получение всех полей модели
	 * @param nodeRef форма
	 * @return список полей модели
	 */
	public List<PropertyDefinition> getAvailableFormFields(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);

		NodeRef ref = new NodeRef(nodeRef);
		if (formsEditorService.isForm(ref)) {
			return formsEditorService.getNotExistFormFields(ref);
		}
		return null;
	}

	/**
	 * Получение всех полей модели
	 * @param nodeRef форма
	 * @return список полей модели
	 */
	public List<AssociationDefinition> getAvailableFormAttributes(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);
		List<AssociationDefinition> fields = new ArrayList<AssociationDefinition>();

		NodeRef ref = new NodeRef(nodeRef);
		if (formsEditorService.isForm(ref)) {
			return formsEditorService.getNotExistFormAttributes(ref);
		}
		return null;
	}

	/**
	 * Развернуть модель
	 * @param modelName название модели
	 * @return true - если форма успешно развёрнута
	 */
	public boolean deployModel(String modelName) {
		ParameterCheck.mandatory("modelName", modelName);
		return formsEditorService.deployModel(modelName);
	}
}
