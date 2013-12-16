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
				for (QName typeName : documentSubTypes) {
					TypeDefinition type = dictionaryService.getType(typeName);
					if (type != null) {
						String modelName = type.getName().toPrefixString(namespaceService);

						JSONObject object = new JSONObject();
						object.put("id", modelName);
						object.put("title", type.getTitle());
						object.put("description", type.getDescription());
						object.put("isActive", true);
						object.put("isDocument", true);

						models.put(modelName, object);
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

								M2Type m2Type = model.getTypes().get(0);

								if (m2Type != null) {
									String modelName = m2Type.getName();

									if (models.containsKey(modelName)) {
										models.get(modelName).put("nodeRef", child.toString());
									} else {
										Serializable modelActive = nodeService.getProperty(child, ContentModel.PROP_MODEL_ACTIVE);

										JSONObject object = new JSONObject();
										object.put("id", modelName);
										object.put("nodeRef", child.toString());
										object.put("title", m2Type.getTitle());
										object.put("description", m2Type.getDescription());
										object.put("isActive", modelActive != null && Boolean.TRUE.equals(modelActive));

										if (modelActive != null && Boolean.TRUE.equals(modelActive)) {
											object.put("isDocument", false);
										} else {
											object.put("isDocument", DocumentService.TYPE_BASE_DOCUMENT.toPrefixString(namespaceService).equals(m2Type.getParentName()));
										}

										models.put(modelName, object);
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
