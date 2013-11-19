package ru.it.lecm.modelEditor.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileNameValidator;
import ru.it.lecm.base.beans.BaseBean;

import java.util.*;

/**
 * User: AIvkin
 * Date: 18.11.13
 * Time: 10:10
 */
public class FormsEditorBeanImpl extends BaseBean {
	public static final String FORM_EDITOR_NAMESPACE_URI = "http://www.it.ru/lecm/forms/editor/1.0";
	public static final QName TYPE_FORM = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "form");
	public static final QName TYPE_FORM_ATTRIBUTE = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr");

	public static final QName PROP_ATTR_NAME = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr-name");

	public static final String FORMS_EDITOR_ROOT_NAME = "Сервис Реадктор форм";
	public static final String FORMS_EDITOR_ROOT_ID = "FORMS_EDITOR_ROOT_ID";

	private NamespaceService namespaceService;
	private DictionaryService dictionaryService;

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(FORMS_EDITOR_ROOT_ID);
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public boolean isForm(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_FORM);
		return isProperType(ref, types);
	}

	public boolean isFormAttribute(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_FORM_ATTRIBUTE);
		return isProperType(ref, types);
	}

	public QName getFormModelType(NodeRef nodeRef) {
		ChildAssociationRef parent = nodeService.getPrimaryParent(nodeRef);
		if (parent != null) {
			String name = (String) nodeService.getProperty(parent.getParentRef(), ContentModel.PROP_NAME);
			if (name != null) {
				return QName.createQName(name.replace("_", ":"), namespaceService);
			}
		}
		return null;
	}

	public Set<String> getFormFields(NodeRef nodeRef) {
		Set<String> result = new HashSet<String>();
		List<ChildAssociationRef> childs = nodeService.getChildAssocs(nodeRef);
		if (childs != null) {
			for (ChildAssociationRef assoc: childs) {
				NodeRef child = assoc.getChildRef();
				if (isFormAttribute(child)) {
					result.add((String) nodeService.getProperty(child, PROP_ATTR_NAME));
				}
			}
		}
		return result;
	}

	public List<PropertyDefinition> getNotExistFormFields(NodeRef nodeRef) {
		List<PropertyDefinition> result = new ArrayList<PropertyDefinition>();
		QName model = getFormModelType(nodeRef);
		if (model != null) {
			TypeDefinition type = dictionaryService.getType(model);
			if (type != null) {
				Map<QName, PropertyDefinition> properties = type.getProperties();
				if (properties != null) {
					Set<String> existFields = getFormFields(nodeRef);
					for (QName property: properties.keySet()) {
						if (!existFields.contains(property.toPrefixString(namespaceService))) {
							result.add(properties.get(property));
						}
					}
				}
			}
		}
		return result;
	}

	public List<AssociationDefinition> getNotExistFormAttributes(NodeRef nodeRef) {
		List<AssociationDefinition> result = new ArrayList<AssociationDefinition>();
		QName model = getFormModelType(nodeRef);
		if (model != null) {
			TypeDefinition type = dictionaryService.getType(model);
			if (type != null) {
				Map<QName, AssociationDefinition> associations = type.getAssociations();
				if (associations != null) {
					Set<String> existFields = getFormFields(nodeRef);
					for (QName assoc: associations.keySet()) {
						if (!existFields.contains(assoc.toPrefixString(namespaceService))) {
							result.add(associations.get(assoc));
						}
					}
				}
			}
		}
		return result;
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
