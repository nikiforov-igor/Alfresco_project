package ru.it.lecm.modelEditor.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Type;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmModelsService;
import ru.it.lecm.documents.beans.DocumentService;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 16.12.13
 * Time: 9:48
 */
public class ModelsListBeanImpl extends BaseBean {
	private DictionaryService dictionaryService;
	private Repository repository;
	private ContentService contentService;
	private NamespaceService namespaceService;
	private DocumentService documentService;
	private LecmModelsService lecmModelsService;

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

    public void setLecmModelsService(LecmModelsService lecmModelsService) {
        this.lecmModelsService = lecmModelsService;
    }

    @Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	/**
	 * Получения папки для развёртывания форм
	 *
	 * @return папка для развёртывания форм
	 */
	public NodeRef getModelsRootFolder() {
		List<ChildAssociationRef> dictionaryAssocs = nodeService.getChildAssocs(
				repository.getCompanyHome(),
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.APP_MODEL_1_0_URI, "dictionary"));

		if (dictionaryAssocs.size() == 1) {
			NodeRef parent = dictionaryAssocs.get(0).getChildRef();

			List<ChildAssociationRef> modelsAssoc = nodeService.getChildAssocs(
					parent,
					ContentModel.ASSOC_CONTAINS,
					QName.createQName(NamespaceService.APP_MODEL_1_0_URI, "models"));

			if (modelsAssoc != null && modelsAssoc.size() == 1) {
				return modelsAssoc.get(0).getChildRef();
			}
		}

		return null;
	}

	public JSONObject getModelsList() {
		JSONObject result = new JSONObject();
		try {

			Map<String, JSONObject> models = new HashMap<String, JSONObject>();

			Collection<QName> documentSubTypes = documentService.getDocumentSubTypes();
			if (documentSubTypes != null) {
				for (QName typeQName : documentSubTypes) {
					TypeDefinition type = dictionaryService.getType(typeQName);
					if (type != null) {
						String typeName = type.getName().toPrefixString(namespaceService);

						JSONObject modelObject = new JSONObject();
						modelObject.put("title", type.getModel().getDescription(dictionaryService));
						modelObject.put("isActive", true);
						modelObject.put("isDocumentModel", true);
						modelObject.put("modelName", type.getModel().getName().toPrefixString());
						modelObject.put("isRestorable", lecmModelsService.isRestorable(type.getModel().getName().toPrefixString()));

						JSONObject typeObject = new JSONObject();
						typeObject.put("typeName", typeName);
						typeObject.put("title", type.getTitle(dictionaryService));
						typeObject.put("isDocument", true);
						typeObject.put("modelName", type.getModel().getName().toPrefixString());

						List<JSONObject> typesList = new ArrayList<JSONObject>();
						typesList.add(typeObject);
						modelObject.put("types", typesList);

						models.put(typeName, modelObject);
					}
				}

				NodeRef modelRootFolder = getModelsRootFolder();
				if (modelRootFolder != null) {
					JSONObject metadata = new JSONObject();
					metadata.put("parent", modelRootFolder.toString());
					result.put("metadata", metadata);

					List<ChildAssociationRef> dynamicModelsAssocs = nodeService.getChildAssocs(modelRootFolder);

					for (ChildAssociationRef assoc : dynamicModelsAssocs) {
						NodeRef child = assoc.getChildRef();

						if (child != null && nodeService.getType(child).equals(ContentModel.TYPE_DICTIONARY_MODEL)) {
							ContentReader contentReader = contentService.getReader(child, ContentModel.PROP_CONTENT);
							if (contentReader != null) {
								M2Model model = M2Model.createModel(contentReader.getContentInputStream());

                                List<M2Type> types = model.getTypes();
                                M2Type firstType = null;
                                if (types != null && !types.isEmpty()) {    //для случая, если в модели нет типов (например только аспекты)
                                    firstType = types.get(0);
                                }

								if (firstType != null) {
									if (models.containsKey(firstType.getName())) {
										models.get(firstType.getName()).put("nodeRef", child.toString());
									} else {
										Serializable modelActive = nodeService.getProperty(child, ContentModel.PROP_MODEL_ACTIVE);

										JSONObject modelObject = new JSONObject();
										modelObject.put("nodeRef", child.toString());
										modelObject.put("title",  model.getDescription());
										modelObject.put("isActive", modelActive != null && Boolean.TRUE.equals(modelActive));

										if (modelActive != null && Boolean.TRUE.equals(modelActive)) {
											modelObject.put("isDocumentModel", false);
										} else {
											if("lecm-eds-document:base".equals(firstType.getParentName())
												|| DocumentService.TYPE_BASE_DOCUMENT.toPrefixString(namespaceService).equals(firstType.getParentName())) {
												modelObject.put("isDocumentModel", true);
											} else {
												modelObject.put("isDocumentModel", false);
											}
										}
										modelObject.put("modelName", model.getName());
										modelObject.put("isRestorable", lecmModelsService.isRestorable(model.getName()));

										List<JSONObject> typesList = new ArrayList<JSONObject>();
										for (M2Type type: model.getTypes()) {
											JSONObject typeObject = new JSONObject();
											typeObject.put("typeName", type.getName());
											typeObject.put("title", type.getTitle());
											typeObject.put("modelName", model.getName());
											if (modelActive != null && Boolean.TRUE.equals(modelActive)) {
												typeObject.put("isDocument", false);
											} else {
												typeObject.put("isDocument", DocumentService.TYPE_BASE_DOCUMENT.toPrefixString(namespaceService).equals(type.getParentName()));
											}
											typesList.add(typeObject);
										}
										modelObject.put("types", typesList);

										models.put(firstType.getName(), modelObject);
									}
								}
							}
						}
					}
				}
			}

			result.put("items", models.values());
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}

		return result;
	}
}
