package ru.it.lecm.modelEditor.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
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
import org.alfresco.util.FileNameValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

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

	public static final String FORMS_EDITOR_ROOT_ID = "FORMS_EDITOR_ROOT_ID";
	public static final String FORMS_EDITOR_MODELS_DEPLOY_ROOT_NAME = "Формы";

	private NamespaceService namespaceService;
	private DictionaryService dictionaryService;
	private Repository repository;

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

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public NodeRef getModelsDeployRootFolder() {
		NodeRef folder = getFolder(repository.getCompanyHome(), FORMS_EDITOR_MODELS_DEPLOY_ROOT_NAME);
		if (folder == null) {
			folder = createFolder(repository.getCompanyHome(), FORMS_EDITOR_MODELS_DEPLOY_ROOT_NAME);
		}
		return folder;
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

	public List<AssociationDefinition> getNotExistFormAttributes(NodeRef nodeRef) {
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
	 * Получение папки с формами для модели
	 *
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

	public NodeRef getModelDeployRootFolder(String modelName) {
		String folderName = modelName.replace(":", "_");
		NodeRef parent = getModelsDeployRootFolder();
		NodeRef folder = getFolder(parent, folderName);
		if (folder == null) {
			return createFolder(parent, folderName);
		} else {
			return folder;
		}
	}

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

	private void cleanFolder(NodeRef folder) {
		List<ChildAssociationRef> childs = nodeService.getChildAssocs(folder);
		if (childs != null) {
			for (ChildAssociationRef assoc : childs) {
				nodeService.removeChild(folder, assoc.getChildRef());
			}
		}
	}

	/**
	 * Развернуть модель
	 *
	 * @param modelName название модели
	 * @return true - если форма успешно развёрнута
	 */
	public boolean deployModel(final String modelName) {
		final List<NodeRef> forms = getModelForms(modelName);
		if (forms != null) {
			final NodeRef rootFolder = getModelDeployRootFolder(modelName);
			if (rootFolder != null) {
				AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
					@Override
					public Object doWork() throws Exception {
						RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
						return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
							@Override
							public Object execute() throws Throwable {
								cleanFolder(rootFolder);

								for (NodeRef form : forms) {
									String nodeName = (String) nodeService.getProperty(form, PROP_FORM_EVALUATOR);
									String formId = (String) nodeService.getProperty(form, PROP_FORM_ID);
									if (formId != null && formId.trim().length() > 0) {
										nodeName += "_" + formId;
									}
									nodeName = FileNameValidator.getValidFileName(nodeName);

									NodeRef configNode = createNode(rootFolder, ContentModel.TYPE_CONTENT, nodeName, null);

									ContentService contentService = serviceRegistry.getContentService();
									ContentWriter writer = contentService.getWriter(configNode, ContentModel.PROP_CONTENT, true);
									if (writer != null) {
										writer.setEncoding("UTF-8");
										writer.setMimetype(MimetypeMap.MIMETYPE_XML);

										writer.putContent(getFormConfig(form, modelName));
									}
								}
								return null;
							}
						}, false, true);
					}
				});
			}
		}
		return true;
	}

	public String getFormConfig(NodeRef form, String modelName) {
		StringWriter out = null;
		XMLStreamWriter xmlw = null;
		try{
			out = new StringWriter();
			xmlw = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
			xmlw.writeStartElement("alfresco-config");

			writeForm(xmlw, form, modelName);

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

	private void writeForm(XMLStreamWriter xmlw, NodeRef form, String modelName) throws XMLStreamException {
		String evaluator = (String) nodeService.getProperty(form, PROP_FORM_EVALUATOR);
	    if (evaluator != null) {
		    xmlw.writeStartElement("config");
		    xmlw.writeAttribute("evaluator", evaluator);
		    xmlw.writeAttribute("condition", modelName);

		    xmlw.writeStartElement("forms");
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
		    xmlw.writeEndElement();

		    xmlw.writeEndElement();
	    }
	}

	private void writeFieldsVisibility(XMLStreamWriter xmlw, List<NodeRef> fields) throws XMLStreamException {
		xmlw.writeStartElement("field-visibility");
		if (fields != null) {
			for (NodeRef field: fields) {
			    xmlw.writeStartElement("show");
				String attrName = (String) nodeService.getProperty(field, PROP_ATTR_NAME);
				xmlw.writeAttribute("id", attrName);
			    xmlw.writeEndElement();
			}
		}
		xmlw.writeEndElement();
	}

	private void writeAppearance(XMLStreamWriter xmlw, List<NodeRef> fields) throws XMLStreamException {
		xmlw.writeStartElement("appearance");
		if (fields != null) {
			for (NodeRef field: fields) {
			    xmlw.writeStartElement("field");

				String attrName = (String) nodeService.getProperty(field, PROP_ATTR_NAME);
				xmlw.writeAttribute("id", attrName);

				String attrLabel = (String) nodeService.getProperty(field, PROP_ATTR_TITLE);
				xmlw.writeAttribute("label", attrLabel);

			    xmlw.writeEndElement();
			}
		}
		xmlw.writeEndElement();
	}
}
