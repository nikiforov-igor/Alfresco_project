package ru.it.lecm.modelEditor.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.dictionary.*;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.modelEditor.beans.FormsEditorBeanImpl;

import java.util.ArrayList;
import java.util.List;

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
	 * Получения папки для развёртывания форм
	 * @return папка для развёртывания форм
	 */
	public ScriptNode getModelsDeployRootFolder() {
		NodeRef folder = formsEditorService.getModelsDeployRootFolder();
		if (folder != null) {
			return new ScriptNode(folder, serviceRegistry, getScope());
		}
		return null;
	}

	/**
	 * Получение элемента с конфигом для модели
	 * @param modelName имя модели
	 * @return элемент с конфигом модели
	 */
	public ScriptNode getModelConfigNode(String modelName) {
		ParameterCheck.mandatory("modelName", modelName);
		NodeRef node = formsEditorService.getModelConfigNode(modelName);
		if (node != null) {
			return new ScriptNode(node, serviceRegistry, getScope());
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
			return formsEditorService.getNotExistFormAssociations(ref);
		}
		return null;
	}

	/**
	 * Сгенерировать формы модели
	 * @param modelName название модели
	 * @return true - если форма успешно развёрнута
	 */
	public boolean generateModelForms(String modelName) {
		ParameterCheck.mandatory("modelName", modelName);
		return formsEditorService.generateModelForms(modelName);
	}

	/**
	 * Развернуть модель
	 * @param modelName название модели
	 * @return true - если форма успешно развёрнута
	 */
	public Scriptable getModelForms(String modelName) {
		ParameterCheck.mandatory("modelName", modelName);
		List<NodeRef> modelForms = formsEditorService.getModelForms(modelName);
		if (modelForms != null) {
			return createScriptable(modelForms);
		}
		return null;
	}

	/**
	 * Получение типа для атрибута
	 * @param nodeRef атрибут
	 * @return тип атрибута
	 */
	public String getFieldType(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);
		NodeRef ref = new NodeRef(nodeRef);
		if (formsEditorService.isFormAttribute(ref)) {
			return formsEditorService.getFieldType(ref);
		}
		return null;
	}

	public void generateDefaultFormAttributes(final ScriptNode form, final String typename) {
		formsEditorService.generateDefaultFormAttributes(form.getNodeRef(), typename);
	}
}
