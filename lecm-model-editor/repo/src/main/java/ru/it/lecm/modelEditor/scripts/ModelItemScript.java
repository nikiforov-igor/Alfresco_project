package ru.it.lecm.modelEditor.scripts;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Type;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.documents.beans.DocumentService;
import static ru.it.lecm.modelEditor.beans.FormsEditorBeanImpl.FAKE_ATTRIBUTE_TYPE;
import ru.it.lecm.modelEditor.beans.ModelsListBeanImpl;

/**
 *
 * @author vmalygin
 */
public class ModelItemScript extends AbstractWebScript {
	private static final Logger logger = LoggerFactory.getLogger(ModelItemScript.class);

	private DictionaryService dictionaryService;
	private ContentService contentService;
	private NamespaceService namespaceService;
	private NodeService nodeService;
	private ModelsListBeanImpl modelListService;

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setModelListService(ModelsListBeanImpl modelListService) {
		this.modelListService = modelListService;
	}

	private Map<String, Object> getModelItemByNodeRef(NodeRef nodeRef,String typeParam) {
		Map<String, Object> modelObject = new HashMap<>();
		ContentReader contentReader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
		if (contentReader != null) {
			M2Model model = M2Model.createModel(contentReader.getContentInputStream());

			List<M2Type> types = model.getTypes();
			M2Type firstType = null;
			if (types != null && !types.isEmpty()) {    //для случая, если в модели нет типов (например только аспекты)
				if(!StringUtils.isNotBlank(typeParam)) {
					firstType = types.get(0);
				} else {
					for(M2Type t:types) {
						if(t.getName().equals(typeParam)) {
							firstType = t;
						}
					}
				}
			}

			if (firstType != null) {
				Serializable modelActive = nodeService.getProperty(nodeRef, ContentModel.PROP_MODEL_ACTIVE);
				Boolean isModelActive = modelActive != null && Boolean.TRUE.equals(modelActive);
				if(isModelActive) {
					QName typeQName = QName.createQName(firstType.getName(), namespaceService);
					Boolean isDocumentModel = dictionaryService.isSubClass(typeQName, DocumentService.TYPE_BASE_DOCUMENT);
	
					modelObject.put("typeName", firstType.getName());
					modelObject.put("isDocumentModel", isDocumentModel);
				} else {
					modelObject.put("typeName", "Undefined");
					modelObject.put("isDocumentModel", "false");
				}
				
				modelObject.put("nodeRef", nodeRef.toString());
				modelObject.put("isModelActive", isModelActive);
			}
		}
		return modelObject;
	}

	private Map<String, Object> getModelItemByType(QName typeQName) {
		Map<String, Object> modelObject = new HashMap<>();
		modelObject.put("typeName", typeQName.getPrefixString());
		modelObject.put("isDocumentModel", dictionaryService.isSubClass(typeQName, DocumentService.TYPE_BASE_DOCUMENT));

		List<ChildAssociationRef> dynamicModelsAssocs = nodeService.getChildAssocs(modelListService.getModelsRootFolder());

		NodeRef modelRef = null;
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
						for(M2Type t:types) {
							if(t.getName().equals(typeQName.getPrefixString())) {
								firstType = t;
							}
						}
					}

					if (firstType != null && firstType.getName().equalsIgnoreCase(typeQName.getPrefixString())) {
						modelRef = child;
						break;
					}
				}
			}
		}
		if (modelRef != null) {
			Serializable modelActive = nodeService.getProperty(modelRef, ContentModel.PROP_MODEL_ACTIVE);
			Boolean isModelActive = modelActive != null && Boolean.TRUE.equals(modelActive);

			modelObject.put("nodeRef", modelRef.toString());
			modelObject.put("isModelActive", isModelActive);
		} else {
			modelObject.put("nodeRef", null);
			modelObject.put("isModelActive", false);
		}

		return modelObject;
	}

	private Map<String, Object> getModelItemByFakeType() {
		Map<String, Object> modelObject = new HashMap<>();
		modelObject.put("nodeRef", null);
		modelObject.put("isModelActive", true);
		modelObject.put("typeName", FAKE_ATTRIBUTE_TYPE);
		modelObject.put("isDocumentModel", false);
		return modelObject;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		String nodeRefParam = req.getParameter("nodeRef");
		String typeParam = req.getParameter("type");

		logger.info("!!!!!!!! nodeRefParam: "+nodeRefParam+", typeParam: "+typeParam);
		
		Map<String, Object> modelObject = null;
		if (StringUtils.isNotBlank(nodeRefParam) && NodeRef.isNodeRef(nodeRefParam)) {
			modelObject = getModelItemByNodeRef(new NodeRef(nodeRefParam),typeParam);
		} else if (StringUtils.isNotBlank(typeParam)) {
			if (FAKE_ATTRIBUTE_TYPE.equals(typeParam)) {
				modelObject = getModelItemByFakeType();
			} else {
				try {
					QName typeQName = QName.createQName(typeParam, namespaceService);
					modelObject = getModelItemByType(typeQName);
				} catch (RuntimeException ex) {
					modelObject = getModelItemByFakeType();
				}
			}
		} else {
			throw new WebScriptException("You must specify nodeRef or type");
		}

		if (modelObject == null) {
			throw new WebScriptException("Failed to build model object by " + nodeRefParam + " and " + typeParam);
		}

		try {
			res.setContentType("application/json");
			res.setContentEncoding("UTF-8");
			res.getWriter().write(new JSONObject(modelObject).toString());
		} catch (IOException ex) {
			throw new WebScriptException(ex.getMessage(), ex);
		}
	}
}
