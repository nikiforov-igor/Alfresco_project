package ru.it.lecm.modelEditor.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileNameValidator;
import ru.it.lecm.base.beans.BaseBean;

import java.util.HashSet;
import java.util.Set;

/**
 * User: AIvkin
 * Date: 18.11.13
 * Time: 10:10
 */
public class FormsEditorBeanImpl extends BaseBean {
	public static final String FORM_EDITOR_NAMESPACE_URI = "http://www.it.ru/lecm/forms/editor/1.0";
	public static final QName TYPE_FORM = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "form");

	public static final String FORMS_EDITOR_ROOT_NAME = "Сервис Реадктор форм";
	public static final String FORMS_EDITOR_ROOT_ID = "FORMS_EDITOR_ROOT_ID";

	private NamespaceService namespaceService;

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(FORMS_EDITOR_ROOT_ID);
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public boolean isForm(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_FORM);
		return isProperType(ref, types);
	}

	public QName getFormModelType(NodeRef NodeRef) {
		ChildAssociationRef parent = nodeService.getPrimaryParent(NodeRef);
		if (parent != null) {
			String name = (String) nodeService.getProperty(parent.getParentRef(), ContentModel.PROP_NAME);
			if (name != null) {
				return QName.createQName(name.replace("_", ":"), namespaceService);
			}
		}
		return null;
	}

	/**
	 * Получение папки с формами для модели
	 * @param modelName имя модели
	 * @return NodeRef папки модели
	 */
	public NodeRef getModelRootFolder(String modelName) {
		String folderName = modelName.replace(":", "_");
		NodeRef parent = getServiceRootFolder();
		NodeRef folder = getFolder(parent, folderName);
		if (folder == null) {
			return createFolder(parent, folderName);
		} else {
			return folder;
		}
	}
}
