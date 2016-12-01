package ru.it.lecm.modelEditor.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassAttributeDefinition;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.util.PropertyMap;
import org.apache.commons.lang.StringUtils;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

/**
 * User: AIvkin
 * Date: 18.11.13
 * Time: 10:10
 */
public class FormsEditorBeanImpl extends BaseBean {
	private static final Logger logger = LoggerFactory.getLogger(FormsEditorBeanImpl.class);

	public static final String FORM_EDITOR_NAMESPACE_URI = "http://www.it.ru/lecm/forms/editor/1.0";
	public static final QName TYPE_FORM = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "form");
	public static final QName TYPE_FORM_ATTRIBUTE = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr");

	public static final QName PROP_FORM_EVALUATOR = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "form-evaluator");
	public static final QName PROP_FORM_ID = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "form-id");
	public static final QName PROP_FORM_TEMPLATE = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "form-template");

	public static final QName PROP_ATTR_INDEX = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr-index");
	public static final QName PROP_ATTR_NAME = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr-name");
	public static final QName PROP_ATTR_TITLE = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr-title");
	public static final QName PROP_ATTR_TAB = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr-tab");
	public static final QName PROP_ATTR_SET = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr-set");
	public static final QName PROP_ATTR_CONTROL = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr-control");
	public static final QName PROP_ATTR_MANDATORY = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr-mandatory");
	public static final QName PROP_ATTR_FOR_MODE = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr-for-mode");
	public static final QName PROP_ATTR_DESCRIPTION = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr-description");
	public static final QName PROP_ATTR_HELP = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr-help");
	public static final QName PROP_ATTR_READ_ONLY = QName.createQName(FORM_EDITOR_NAMESPACE_URI, "attr-read-only");

	public static final String FORMS_EDITOR_ROOT_ID = "FORMS_EDITOR_ROOT_ID";
	public static final String FORMS_EDITOR_MODELS_DEPLOY_UUID = "lecm_forms_container";

	public static final String FAKE_ATTRIBUTE_TYPE = "fake";

	protected NamespaceService namespaceService;
	protected DictionaryService dictionaryService;

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

	/**
	 * Получения папки для развёртывания форм
	 * @return папка для развёртывания форм
	 */
        //TODO Refactoring in progress
	public NodeRef getModelsDeployRootFolder() {
		NodeRef folder;
		NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, FORMS_EDITOR_MODELS_DEPLOY_UUID);
		if (nodeService.exists(nodeRef)) {
			folder = nodeRef;
		} else {
			String msg = String.format("Node %s does not exist! Check bootstrap-forms-editor.xml", nodeRef);
			throw new AlfrescoRuntimeException(msg);
		}
		return folder;
	}

	/**
	 * Проверка, что элемент является формой
	 * @param ref идентификатор элемента
	 * @return true, если элемент является формой
	 */
	public boolean isForm(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_FORM);
		return isProperType(ref, types);
	}

	/**
	 * Проверка, что элемент является аттрибутом формой
	 * @param ref идентификатор элемента
	 * @return true, если элемент является аттрибутом формой
	 */
	public boolean isFormAttribute(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_FORM_ATTRIBUTE);
		return isProperType(ref, types);
	}

	/**
	 * Получение названия модели для формы
	 * @param nodeRef идентификатор формы
	 * @return тип модели
	 */
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

	/**
	 * Получение аттрибутов формы
	 * @param nodeRef форма
	 * @param sort если true, то аттрибуты будут отсотрированы по индексу
	 * @return список аттрибутов формы
	 */
	public List<NodeRef> getFormFields(NodeRef nodeRef, boolean sort) {
		List<NodeRef> result = new ArrayList<NodeRef>();
		List<ChildAssociationRef> childs = nodeService.getChildAssocs(nodeRef);
		if (childs != null) {
			for (ChildAssociationRef assoc : childs) {
				NodeRef child = assoc.getChildRef();
				if (isFormAttribute(child)) {
					result.add(child);
				}
			}
		}

		if (sort) {
			Comparator<NodeRef> comparator = new Comparator<NodeRef>() {
				public int compare(NodeRef ref1, NodeRef ref2) {
					Integer index1 = (Integer) nodeService.getProperty(ref1, PROP_ATTR_INDEX);
					Integer index2 = (Integer) nodeService.getProperty(ref2, PROP_ATTR_INDEX);

					if (index1 == null) {
						return index2 == null ? 0 : 1;
					} else if (index2 == null) {
						return -1;
					} else {
						return index1.compareTo(index2);
					}
				}
			};

			Collections.sort(result, comparator);
		}

		return result;
	}

	/**
	 * Получение названий аттрибутов для формы
	 * @param nodeRef идентификатор формы
	 * @return названия всех аттрибутов формы
	 */
	public Set<String> getFormFieldsName(NodeRef nodeRef) {
		Set<String> result = new HashSet<String>();
		List<NodeRef> fields = getFormFields(nodeRef, false);
		if (fields != null) {
			for (NodeRef field : fields) {
				result.add((String) nodeService.getProperty(field, PROP_ATTR_NAME));
			}
		}
		return result;
	}

	/**
	 * Получение полей для типа модели, которые ещё не были добавлены на форму
	 * @param nodeRef инентификатор формы
	 * @return поля модели, которые ещё не были добавлены на форму
	 */
	public List<PropertyDefinition> getNotExistFormFields(NodeRef nodeRef) {
		List<PropertyDefinition> result = new ArrayList<PropertyDefinition>();
		QName model = getFormModelType(nodeRef);
		if (model != null) {
			TypeDefinition type = dictionaryService.getType(model);
			if (type != null) {
				Map<QName, PropertyDefinition> properties = type.getProperties();
				if (properties != null) {
					Set<String> existFields = getFormFieldsName(nodeRef);
					for (QName property : properties.keySet()) {
						if (!existFields.contains(property.toPrefixString(namespaceService))) {
							result.add(properties.get(property));
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Получение ассоциаций для типа модели, которые ещё не были добавлены на форму
	 * @param nodeRef инентификатор формы
	 * @return ассоциации модели, которые ещё не были добавлены на форму
	 */
	public List<AssociationDefinition> getNotExistFormAssociations(NodeRef nodeRef) {
		List<AssociationDefinition> result = new ArrayList<AssociationDefinition>();
		QName model = getFormModelType(nodeRef);
		if (model != null) {
			TypeDefinition type = dictionaryService.getType(model);
			if (type != null) {
				Map<QName, AssociationDefinition> associations = type.getAssociations();
				if (associations != null) {
					Set<String> existFields = getFormFieldsName(nodeRef);
					for (QName assoc : associations.keySet()) {
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
	 * Получение названия типа аттрибута
	 * @param field идентификатор аттрибута
	 * @return если аттрибут, то его title. Если ассоциация, то title его целевого класса
	 */
	public String getFieldType(NodeRef field) {
		String attrName = (String) nodeService.getProperty(field, PROP_ATTR_NAME);
		if (attrName != null) {
			QName attrQName = QName.createQName(attrName, namespaceService);
			if (attrQName != null) {
				PropertyDefinition attrDefinition = dictionaryService.getProperty(attrQName);
				if (attrDefinition != null) {
					return attrDefinition.getDataType().getName().toPrefixString(namespaceService);
				} else {
					AssociationDefinition attrAssocDefinition = dictionaryService.getAssociation(attrQName);
					if (attrAssocDefinition != null) {
						return attrAssocDefinition.getTargetClass().getName().toPrefixString(namespaceService);
					} else {
						return FAKE_ATTRIBUTE_TYPE;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Получение названия модели для сохранения в репозитории
	 * @param modelName название модели
	 * @return Название, подходящее для сохранения элементов в репозитории
	 */
	public String getModelFileName(String modelName) {
		return modelName.replace(":", "_") + ".xml";
	}

	/**
	 * Получение папки с формами для модели
	 *
	 * @param modelName имя модели
	 * @return NodeRef папки модели
	 */
        //TODO Refactoring in progress
        //TODO ALF-2616 Пока создаём каталог здесь, из-за логики работы формы. Надо придумать более правильное место для создания.
	public NodeRef getModelRootFolder(String modelName) {
		final String folderName = modelName.replace(":", "_");
		final NodeRef parent = getServiceRootFolder();
                NodeRef result =  getFolder(parent, folderName);
                if (null == result) {
                    logger.debug("Folder \""+ folderName +"\" not found in \""+parent.toString()+ "\", creating.");
                    result = lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {

                        @Override
                        public NodeRef execute() throws Throwable {
                            return createFolder(parent, folderName);
                        }
                    });
                    logger.debug("Folder \"" + folderName+ "\" created in \""+parent.toString()+ "\"");
                }
                return result;
	}

	/**
	 * Получение элемента с конфигом для модели
	 * @param modelName название модели
	 * @return элемент с конфигом модели
	 */
	public NodeRef getModelConfigNode(String modelName) {
		NodeRef parent = getModelsDeployRootFolder();
		return nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS, getModelFileName(modelName));
	}

	/**
	 * Получение всех форм для модели
	 * @param modelName название модели
	 * @return список форм модели
	 */
	public List<NodeRef> getModelForms(String modelName) {
		List<NodeRef> result = new ArrayList<NodeRef>();
		NodeRef modelRoot = getModelRootFolder(modelName);
		if (modelRoot != null) {
			List<ChildAssociationRef> forms = nodeService.getChildAssocs(modelRoot);
			if (forms != null) {
				for (ChildAssociationRef assoc : forms) {
					NodeRef form = assoc.getChildRef();
					if (isForm(form)) {
						result.add(form);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Сгенерировать формы модели
	 *
	 * @param modelName название модели
	 * @return true - если формы успешно сгенерированы
	 */
	public boolean generateModelForms(final String modelName) {
		NodeRef configNode = getModelConfigNode(modelName);
		if (configNode == null) {
                    try {
                        configNode = createNode(getModelsDeployRootFolder(), ContentModel.TYPE_CONTENT, getModelFileName(modelName), null);
                    } catch (WriteTransactionNeededException ex) {
                        logger.debug("Can't create folder.", ex );
                        throw new RuntimeException(ex);
                    }
		}
		if (!nodeService.hasAspect(configNode, ContentModel.ASPECT_VERSIONABLE)) {
			nodeService.addAspect(configNode, ContentModel.ASPECT_VERSIONABLE, null);
		}

		ContentService contentService = serviceRegistry.getContentService();
		ContentWriter writer = contentService.getWriter(configNode, ContentModel.PROP_CONTENT, true);

		if (writer != null) {
			writer.setEncoding("UTF-8");
			writer.setMimetype(MimetypeMap.MIMETYPE_XML);
			writer.putContent(getModelConfig(modelName));
		}

		return true;
	}

	protected String getModelConfig(String modelName) {
		List<NodeRef> forms = getModelForms(modelName);
		if (forms != null) {
			StringWriter out = null;
			XMLStreamWriter xmlw = null;
			try{
				out = new StringWriter();
				xmlw = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
				xmlw.writeStartElement("alfresco-config");

				writeForms(xmlw, forms, modelName);

				xmlw.writeEndElement();
			} catch (XMLStreamException e) {
				logger.error("Error get form config", e);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						logger.error("Error get form config", e);
					}
				}
				if (xmlw != null) {
					try {
						xmlw.close();
					} catch (XMLStreamException e) {
						logger.error("Error get form config", e);
					}
				}
			}
			return out.toString();
		}
		return null;
	}

	public void writeForms(XMLStreamWriter xmlw, List<NodeRef> forms, String modelName) throws XMLStreamException {
		Map<String, List<NodeRef>> evaluatorFromGroups = new HashMap<String, List<NodeRef>>();
		for (NodeRef form : forms) {
			String evaluator = (String) nodeService.getProperty(form, PROP_FORM_EVALUATOR);
			if (evaluator != null) {
				if (!evaluatorFromGroups.containsKey(evaluator)) {
					evaluatorFromGroups.put(evaluator, new ArrayList<NodeRef>());
				}
				evaluatorFromGroups.get(evaluator).add(form);
			}
		}

		for (String evaluator: evaluatorFromGroups.keySet()) {
			xmlw.writeStartElement("config");
			xmlw.writeAttribute("evaluator", evaluator);
			xmlw.writeAttribute("condition", modelName);
			xmlw.writeAttribute("replace", "true");

			xmlw.writeStartElement("forms");

			for (NodeRef form: evaluatorFromGroups.get(evaluator)) {
				writeForm(xmlw, form);
			}

			xmlw.writeEndElement();
			xmlw.writeEndElement();
		}
	}

	protected void writeForm(XMLStreamWriter xmlw, NodeRef form) throws XMLStreamException {
		String evaluator = (String) nodeService.getProperty(form, PROP_FORM_EVALUATOR);
	    if (evaluator != null) {
		    xmlw.writeStartElement("form");

		    String formId = (String) nodeService.getProperty(form, PROP_FORM_ID);
		    if (formId != null && formId.trim().length() > 0) {
			    xmlw.writeAttribute("id", formId);
		    }

		    String formTemplate = (String) nodeService.getProperty(form, PROP_FORM_TEMPLATE);
		    if (formTemplate != null && formTemplate.trim().length() > 0) {
			    xmlw.writeStartElement("edit-form");
			    xmlw.writeAttribute("template", formTemplate);
			    xmlw.writeEndElement();
			    xmlw.writeStartElement("view-form");
			    xmlw.writeAttribute("template", formTemplate);
			    xmlw.writeEndElement();
			    xmlw.writeStartElement("create-form");
			    xmlw.writeAttribute("template", formTemplate);
			    xmlw.writeEndElement();
		    }

		    List<NodeRef> fields = getFormFields(form, true);

		    writeFieldsVisibility(xmlw, fields);
		    writeAppearance(xmlw, fields);

		    xmlw.writeEndElement();
	    }
	}

	protected void writeFieldsVisibility(XMLStreamWriter xmlw, List<NodeRef> fields) throws XMLStreamException {
		xmlw.writeStartElement("field-visibility");
		if (fields != null) {
			for (NodeRef field: fields) {
			    xmlw.writeStartElement("show");
				String attrName = (String) nodeService.getProperty(field, PROP_ATTR_NAME);
				xmlw.writeAttribute("id", attrName);
				Serializable forMode = nodeService.getProperty(field, PROP_ATTR_FOR_MODE);
				if (forMode != null && !forMode.toString().trim().isEmpty()) {
					xmlw.writeAttribute("for-mode", forMode.toString());
				}
				xmlw.writeAttribute("force", "true");
			    xmlw.writeEndElement();
			}
		}
		xmlw.writeEndElement();
	}

	protected void writeAppearance(XMLStreamWriter xmlw, List<NodeRef> fields) throws XMLStreamException {
		xmlw.writeStartElement("appearance");
		Map<String, List<String>> sets = getSets(fields);

		writeSets(xmlw, sets);

		if (fields != null) {
			for (NodeRef field: fields) {
			    xmlw.writeStartElement("field");

				String attrName = (String) nodeService.getProperty(field, PROP_ATTR_NAME);
				xmlw.writeAttribute("id", attrName);

				String attrLabel = (String) nodeService.getProperty(field, PROP_ATTR_TITLE);
				xmlw.writeAttribute("label", attrLabel);

				Serializable mandatory = nodeService.getProperty(field, PROP_ATTR_MANDATORY);
				if (mandatory != null && !mandatory.toString().trim().isEmpty()) {
					xmlw.writeAttribute("mandatory", mandatory.toString());
				}

				Serializable description = nodeService.getProperty(field, PROP_ATTR_DESCRIPTION);
				if (description != null && !description.toString().trim().isEmpty()) {
					xmlw.writeAttribute("description", description.toString());
				}

				Serializable help = nodeService.getProperty(field, PROP_ATTR_HELP);
				if (help != null && !help.toString().trim().isEmpty()) {
					xmlw.writeAttribute("help", help.toString());
				}

				Serializable readOnly = nodeService.getProperty(field, PROP_ATTR_READ_ONLY);
				if (readOnly != null && !readOnly.toString().trim().isEmpty()) {
					xmlw.writeAttribute("read-only", readOnly.toString());
				}

				String tab = (String) nodeService.getProperty(field, PROP_ATTR_TAB);
				if (tab != null && tab.trim().length() == 0) {
					tab = null;
				}

				List<String> tabSets = sets.get(tab);

				int tabIndex = 0;
				for (String temp: sets.keySet()) {
					if ((temp == null && tab == null) || (temp != null && tab != null && temp.equals(tab))) {
						break;
					}
					tabIndex++;
				}

				String set = (String) nodeService.getProperty(field, PROP_ATTR_SET);
				if (set != null && set.trim().length() > 0) {
					xmlw.writeAttribute("set", "tab" + tabIndex + "set" + tabSets.indexOf(set));
				} else if (tab != null) {
					xmlw.writeAttribute("set", "tab" + tabIndex);
				}

				writeControl(xmlw, field);

			    xmlw.writeEndElement();
			}
		}
		xmlw.writeEndElement();
	}

	protected void writeControl(XMLStreamWriter xmlw, NodeRef field) throws XMLStreamException {
		String control = (String) nodeService.getProperty(field, PROP_ATTR_CONTROL);
		if (control != null && control.trim().length() > 0) {
			try {
				JSONObject json = new JSONObject(control);
				xmlw.writeStartElement("control");

				String template = json.getString("template");
				if (template != null) {
					xmlw.writeAttribute("template", template);
				}

				JSONArray params = json.getJSONArray("params");
				if (params != null) {
					for (int i = 0; i < params.length(); i++) {
						String name = params.getJSONObject(i).getString("name");
						if (name != null) {
							xmlw.writeStartElement("control-param");
							xmlw.writeAttribute("name", name);

							String value = params.getJSONObject(i).getString("value");
							if (value != null) {
								xmlw.writeCharacters(value);
							}

							xmlw.writeEndElement();
						}
					}
				}

				xmlw.writeEndElement();
			} catch (JSONException e) {
				logger.error("Error parse control JSON", e);
			}
		}
	}

	protected void writeSets(XMLStreamWriter xmlw, Map<String, List<String>> sets) throws XMLStreamException {
		if (sets != null) {
			int tabIndex = 0;
			for (String tab: sets.keySet()) {
				List<String> tabSets = sets.get(tab);

				if (tab != null) {
					xmlw.writeStartElement("set");
					xmlw.writeAttribute("id", "tab" + tabIndex);
					xmlw.writeAttribute("label", tab);
					xmlw.writeEndElement();
				}

				for (int i = 0; i < tabSets.size(); i++) {
					xmlw.writeStartElement("set");
					xmlw.writeAttribute("id", "tab" + tabIndex + "set" + i);
					xmlw.writeAttribute("label", tabSets.get(i));
					if (tab != null) {
						xmlw.writeAttribute("parent", "tab" + tabIndex);
					}
					xmlw.writeEndElement();
				}

				tabIndex++;
			}
		}
	}

	protected Map<String, List<String>> getSets(List<NodeRef> fields) {
		Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
		result.put(null, new ArrayList<String>());
		for (NodeRef field: fields) {
			String tab = (String) nodeService.getProperty(field, PROP_ATTR_TAB);
			if (tab != null && tab.trim().length() == 0) {
				tab = null;
			}
			if (!result.containsKey(tab)) {
				result.put(tab, new ArrayList<String>());
			}

			String set = (String) nodeService.getProperty(field, PROP_ATTR_SET);
			if (set != null && set.trim().length() > 0) {
				List<String> tabSets = result.get(tab);
				if (!tabSets.contains(set)) {
					tabSets.add(set);
				}
			}
		}
		return result;
	}

	protected void createAttribute(final NodeRef formRef, final ClassAttributeDefinition attrDef, final int order) {
		PropertyMap props = new PropertyMap();
		String name = attrDef.getName().toPrefixString(namespaceService);
		String title = StringUtils.defaultString(attrDef.getTitle(dictionaryService));
		props.put(PROP_ATTR_NAME, name);
		props.put(PROP_ATTR_TITLE, title);
		props.put(PROP_ATTR_INDEX, order);
		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
		nodeService.createNode(formRef, ContentModel.ASSOC_CONTAINS, assocQName, TYPE_FORM_ATTRIBUTE, props);
	}

	public void generateDefaultFormAttributes(final NodeRef formRef, final String typename) {
		QName type = QName.createQName(typename, namespaceService);
		TypeDefinition typeDef = dictionaryService.getType(type);
		Collection<PropertyDefinition> propDefs = typeDef.getProperties().values();
		Collection<AssociationDefinition> assocDefs = typeDef.getAssociations().values();
		Collection<ChildAssociationDefinition> childAssocDefs = typeDef.getChildAssociations().values();

		int order = 0;
		for (PropertyDefinition propDef : propDefs) {
			if (type.isMatch(propDef.getContainerClass().getName())) {
				createAttribute(formRef, propDef, order++);
			}
		}
		for (AssociationDefinition assocDef : assocDefs) {
			if (type.isMatch(assocDef.getSourceClass().getName())) {
				createAttribute(formRef, assocDef, order++);
			}
		}
		for (ChildAssociationDefinition childAssocDef : childAssocDefs) {
			if (type.isMatch(childAssocDef.getSourceClass().getName())) {
				createAttribute(formRef, childAssocDef, order++);
			}
		}
	}
	
}
