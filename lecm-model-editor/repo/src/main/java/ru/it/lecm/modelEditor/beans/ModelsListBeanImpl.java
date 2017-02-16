package ru.it.lecm.modelEditor.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.NamespaceDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Type;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmModelsService;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentService;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 16.12.13
 * Time: 9:48
 */
public class ModelsListBeanImpl extends BaseBean {
	private static final Logger logger = LoggerFactory.getLogger(ModelsListBeanImpl.class);
	protected DictionaryService dictionaryService;
	protected Repository repository;
	protected ContentService contentService;
	protected NamespaceService namespaceService;
	protected DocumentService documentService;
	protected LecmModelsService lecmModelsService;
	private DictionaryBootstrapPostProcessor models;
	
	public void setModels(DictionaryBootstrapPostProcessor models){
		this.models = models;
	}

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

	public JSONObject getModelsList(String parentType) {
		JSONObject result = new JSONObject();
		try {
			boolean isDictionary = "dict".equals(parentType);
			Map<String, JSONObject> models = new HashMap<>();

			Collection<QName> subTypes = null;
			if(isDictionary) {
				subTypes = dictionaryService.getSubTypes(DictionaryBean.TYPE_PLANE_DICTIONARY_VALUE, true);
				subTypes.remove(DictionaryBean.TYPE_PLANE_DICTIONARY_VALUE);
			}
			else {
				subTypes = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);
				subTypes.remove(DocumentService.TYPE_BASE_DOCUMENT);
				subTypes.remove(QName.createQName("http://www.it.ru/logicECM/eds-document/1.0", "base"));
			}
			
			if (subTypes != null) {
				for (QName typeQName : subTypes) {
					TypeDefinition type = dictionaryService.getType(typeQName);
					if (type != null) {
						String typeName = type.getName().toPrefixString(namespaceService);
						String modelName = type.getModel().getName().toPrefixString();

						JSONObject modelObject = models.get(modelName);
						if(modelObject==null) {
							modelObject = new JSONObject();
							modelObject.put("title", type.getModel().getDescription(dictionaryService));
							modelObject.put("isActive", true);
							modelObject.put("isDocumentModel", !isDictionary);
							modelObject.put("modelName", type.getModel().getName().toPrefixString());
							modelObject.put("isRestorable", lecmModelsService.isRestorable(type.getModel().getName().toPrefixString()));
							
							models.put(modelName, modelObject);
						}

						JSONObject typeObject = new JSONObject();
						typeObject.put("typeName", typeName);
						typeObject.put("title", type.getTitle(dictionaryService));//getDescription(dictionaryService));
						typeObject.put("isDocument", !isDictionary);
						typeObject.put("modelName", type.getModel().getName().toPrefixString());

						JSONArray typesList = (JSONArray)modelObject.opt("types");
						if(typesList==null) {
							typesList = new JSONArray();
							modelObject.put("types", typesList);
						}
						typesList.put(typeObject);
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
									if (models.containsKey(model.getName())) {
										JSONObject jModel = models.get(model.getName());
										jModel.put("nodeRef", child.toString());
										JSONArray typesList = (JSONArray)jModel.opt("types");
										for(int i=0;i<typesList.length();i++){ 
											JSONObject jType = (JSONObject)typesList.get(i);
											jType.put("nodeRef", child.toString());
											
										}
									} else {
										boolean isDocumentModel = false;
										Collection<QName> potentialParent = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);
										for(QName qn:potentialParent) {
											if(qn.toPrefixString(namespaceService).equals(firstType.getParentName())){
												isDocumentModel = true;
											}
										}
//										if("lecm-eds-document:base".equals(firstType.getParentName())
//												|| DocumentService.TYPE_BASE_DOCUMENT.toPrefixString(namespaceService).equals(firstType.getParentName())) {
//											isDocumentModel = true;
//										}
										Serializable modelActive = nodeService.getProperty(child, ContentModel.PROP_MODEL_ACTIVE);
										if(Boolean.FALSE.equals(modelActive)&&((!isDictionary&&isDocumentModel)||(isDictionary&&!isDocumentModel))) {
											JSONObject modelObject = new JSONObject();
											modelObject.put("nodeRef", child.toString());
											modelObject.put("title",  model.getDescription());
											modelObject.put("isActive", modelActive != null && Boolean.TRUE.equals(modelActive));
											modelObject.put("isDocumentModel", isDocumentModel);
											modelObject.put("modelName", model.getName());
											modelObject.put("isRestorable", lecmModelsService.isRestorable(model.getName()));
	
											List<JSONObject> typesList = new ArrayList<>();
											for (M2Type type: model.getTypes()) {
												JSONObject typeObject = new JSONObject();
												typeObject.put("typeName", type.getName());
												typeObject.put("title", type.getTitle());
												typeObject.put("modelName", model.getName());
												boolean isDocument = false;
												for(QName qn:potentialParent) {
													if(qn.toPrefixString(namespaceService).equals(type.getParentName())){
														isDocument = true;
													}
												}
												typeObject.put("isDocument", isDocumentModel);
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
			}

			List<JSONObject> items = new ArrayList<>(models.values());
			Collections.sort(items, new JSONComparator("title"));

			result.put("items", items);
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}

		return result;
	}
	
	public Map<String, String> getDocumentSubTypes() {
        Collection<QName> subTypes = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);
        
        Map<String, String> results = new HashMap<>();
		if (subTypes != null) {
			for (QName type : subTypes) {
				TypeDefinition typeDef = dictionaryService.getType(type);
				results.put(type.toPrefixString(namespaceService), typeDef.getTitle(dictionaryService));
			}
		}
        return results;
    }
	
	public List<String> getCategories(String nodeRef, String documentType) {
		List<String> categories = new ArrayList<String>();
		TypeDefinition type = null;
		try{
			QName documentTypeQName = QName.createQName(documentType, namespaceService);
			type = dictionaryService.getType(documentTypeQName);
		} catch(Exception e) {}
		QName parentDocumentTypeQName = null;
		if(type==null) {
			ContentReader contentReader = contentService.getReader(new NodeRef(nodeRef), ContentModel.PROP_CONTENT);
			if (contentReader != null) {
				M2Model model = M2Model.createModel(contentReader.getContentInputStream());
	
	            List<M2Type> types = model.getTypes();
	            M2Type firstType = null;
	            if (types != null && !types.isEmpty()) {//для случая, если в модели нет типов (например только аспекты)
	            	for(M2Type mtype:types) {
						if(mtype.getName().equals(documentType)) {
							firstType = mtype;
						}
					}
	            }
				if (firstType != null) {
					parentDocumentTypeQName = QName.createQName(firstType.getParentName(), namespaceService);
				}
			}
		} else {
			parentDocumentTypeQName = type.getParentName();
		}
		if(parentDocumentTypeQName!=null) {
			ConstraintDefinition constraint = dictionaryService.getConstraint(QName.createQName(parentDocumentTypeQName.getNamespaceURI(), "attachment-categories"));
			if (constraint != null && constraint.getConstraint() != null && (constraint.getConstraint() instanceof ListOfValuesConstraint)) {
				ListOfValuesConstraint psConstraint = (ListOfValuesConstraint) constraint.getConstraint();
				if (psConstraint.getAllowedValues() != null) {
					categories.addAll(psConstraint.getAllowedValues());
				}
			}
		}
		return categories;
	}
	
	public JSONObject getAttrs(String nodeRef, String documentType) {
		JSONObject result = new JSONObject();
		try {
			List<JSONObject> items = new ArrayList<>();
			TypeDefinition type = null;
			try{
				QName documentTypeQName = QName.createQName(documentType, namespaceService);
				type = dictionaryService.getType(documentTypeQName);
			} catch(Exception e) {}
			QName parentDocumentTypeQName = null;
			if(type==null) {
				ContentReader contentReader = contentService.getReader(new NodeRef(nodeRef), ContentModel.PROP_CONTENT);
				if (contentReader != null) {
					M2Model model = M2Model.createModel(contentReader.getContentInputStream());
		
		            List<M2Type> types = model.getTypes();
		            M2Type firstType = null;
		            if (types != null && !types.isEmpty()) {//для случая, если в модели нет типов (например только аспекты)
		            	for(M2Type mtype:types) {
							if(mtype.getName().equals(documentType)) {
								firstType = mtype;
							}
						}
		            }
		
					if (firstType != null) {
						parentDocumentTypeQName = QName.createQName(firstType.getParentName(), namespaceService);
					}
				}
			} else {
				parentDocumentTypeQName = type.getParentName();
			}
			if(parentDocumentTypeQName!=null) {
				while(parentDocumentTypeQName!=null) {
					String parentNS = parentDocumentTypeQName.toPrefixString().substring(0,parentDocumentTypeQName.toPrefixString().indexOf(":"));
					JSONObject parentObject = new JSONObject();
					parentObject.put("_name", parentDocumentTypeQName.toPrefixString());
					TypeDefinition parentType = dictionaryService.getType(parentDocumentTypeQName);
					Map<QName, PropertyDefinition> props = parentType.getProperties();
					List<JSONObject> propsArray = new ArrayList<>();
					for(PropertyDefinition prop : props.values()) {
						if( prop.getName().toPrefixString(namespaceService).startsWith(parentNS)) {
							JSONObject propObject = new JSONObject();
							propObject.put("_name", prop.getName().toPrefixString(namespaceService));
							propObject.put("title", prop.getTitle());
							propObject.put("type", prop.getDataType().getName().toPrefixString(namespaceService));
							propObject.put("default", prop.getDefaultValue());
							propObject.put("mandatory", prop.isMandatory());
							propObject.put("_enabled", prop.isIndexed());
							propObject.put("tokenised", prop.getIndexTokenisationMode());
							propsArray.add(propObject);
						}
					}
					Collections.sort(propsArray, new JSONComparator("_name"));
					if(propsArray.size()>0) {
						JSONObject typeObject = new JSONObject();
						typeObject.put("props", propsArray);
						parentObject.put("type", typeObject);
						items.add(parentObject);
					}
					parentDocumentTypeQName = parentType.getParentName();
					if("cm:cmobject".equals(parentDocumentTypeQName.toPrefixString())) parentDocumentTypeQName = null;
				}
			}
			result.put("data", items);
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}
		return result;
	}

	public JSONObject getAssocs(String nodeRef, String documentType) {
		JSONObject result = new JSONObject();
		try {
			List<JSONObject> items = new ArrayList<>();
			TypeDefinition type = null;
			try{
				QName documentTypeQName = QName.createQName(documentType, namespaceService);
				type = dictionaryService.getType(documentTypeQName);
			} catch(Exception e) {}
			QName parentDocumentTypeQName = null;
			if(type==null) {
				ContentReader contentReader = contentService.getReader(new NodeRef(nodeRef), ContentModel.PROP_CONTENT);
				if (contentReader != null) {
					M2Model model = M2Model.createModel(contentReader.getContentInputStream());
		
		            List<M2Type> types = model.getTypes();
		            M2Type firstType = null;
		            if (types != null && !types.isEmpty()) {//для случая, если в модели нет типов (например только аспекты)
		            	for(M2Type mtype:types) {
							if(mtype.getName().equals(documentType)) {
								firstType = mtype;
							}
						}
		            }
		
					if (firstType != null) {
						parentDocumentTypeQName = QName.createQName(firstType.getParentName(), namespaceService);
					}
				}
			} else {
				parentDocumentTypeQName = type.getParentName();
			}
			if(parentDocumentTypeQName!=null) {
				while(parentDocumentTypeQName!=null) {
					String parentNS = parentDocumentTypeQName.toPrefixString().substring(0,parentDocumentTypeQName.toPrefixString().indexOf(":"));
					JSONObject parentObject = new JSONObject();
					parentObject.put("_name", parentDocumentTypeQName.toPrefixString());
					TypeDefinition parentType = dictionaryService.getType(parentDocumentTypeQName);
					Map<QName, AssociationDefinition> assocs = parentType.getAssociations();
					List<JSONObject> assocsArray = new ArrayList<>();
					for(AssociationDefinition assoc : assocs.values()) {
						if( assoc.getName().toPrefixString(namespaceService).startsWith(parentNS)) {
							JSONObject assocObject = new JSONObject();
							assocObject.put("_name", assoc.getName().toPrefixString(namespaceService));
							assocObject.put("title", assoc.getTitle());
							assocObject.put("class", assoc.getTargetClass().getName().toPrefixString(namespaceService));
							assocObject.put("mandatory", assoc.isTargetMandatory());
							assocObject.put("many", assoc.isTargetMany());
							assocsArray.add(assocObject);
						}
					}
					Collections.sort(assocsArray, new JSONComparator("_name"));
					if(assocsArray.size()>0) {
						JSONObject typeObject = new JSONObject();
						typeObject.put("assocs", assocsArray);
						parentObject.put("type", typeObject);
						items.add(parentObject);
					}
					parentDocumentTypeQName = parentType.getParentName();
					if("cm:cmobject".equals(parentDocumentTypeQName.toPrefixString())) parentDocumentTypeQName = null;
				}
			}
			result.put("data", items);
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}
		return result;
	}
	
	private JSONObject getAspectObject(QName aspectQN) throws JSONException {
		JSONObject aspectObject = null;
		AspectDefinition aspect = dictionaryService.getAspect(aspectQN);
		
		if( aspect!=null) {
			aspectObject = new JSONObject();
			aspectObject.put("aspectName", aspect.getName().toPrefixString(namespaceService));
			aspectObject.put("aspectTitle", aspect.getTitle(dictionaryService));
			aspectObject.put("name", aspect.getName().toPrefixString(namespaceService));
			List<JSONObject> props = new ArrayList<>();
			for(PropertyDefinition pdRow: aspect.getProperties().values()){
				JSONObject propObject = new JSONObject();
				propObject.put("_name", pdRow.getName().toPrefixString(namespaceService));
				propObject.put("title", pdRow.getTitle());
				propObject.put("type", pdRow.getDataType().getName().toPrefixString(namespaceService));
				propObject.put("default", pdRow.getDefaultValue());
				propObject.put("mandatory", pdRow.isMandatory());
				propObject.put("_enabled", pdRow.isIndexed());
				propObject.put("tokenised", pdRow.getIndexTokenisationMode());
				props.add(propObject);
			}
			Collections.sort(props, new JSONComparator("_name"));
			JSONObject tableObject = new JSONObject();
			tableObject.put("name", aspect.getName().toPrefixString(namespaceService));
			tableObject.put("props", props);
			List<JSONObject> assocs = new ArrayList<>();
			for(AssociationDefinition adRow: aspect.getAssociations().values()) {
				JSONObject assocObject = new JSONObject();
				assocObject.put("_name", adRow.getName().toPrefixString(namespaceService));
				assocObject.put("title", adRow.getTitle());
				assocObject.put("class", adRow.getTargetClass().getName().toPrefixString(namespaceService));
				assocObject.put("mandatory", adRow.isTargetMandatory());
				assocObject.put("many", adRow.isTargetMany());
				assocs.add(assocObject);
			}
			Collections.sort(assocs, new JSONComparator("_name"));
			tableObject.put("assocs", assocs);
			aspectObject.put("aspect", tableObject);
		}
		return aspectObject;
	}
	
	public JSONObject getAspects(String documentType) {
		JSONObject result = new JSONObject();
		try {
			List<JSONObject> items = new ArrayList<>();
			AspectDefinition type = null;
			QName documentTypeQName = null;
			try{
				documentTypeQName = QName.createQName(documentType, namespaceService);
				type = dictionaryService.getAspect(documentTypeQName);
			} catch(Exception e) {}
			QName parentDocumentTypeQName = null;
			if(type!=null) {
				parentDocumentTypeQName = documentTypeQName;
			}
			if(parentDocumentTypeQName!=null) {
				Collection<QName> aspects = dictionaryService.getSubAspects(parentDocumentTypeQName, true);
				for(QName aspectQN : aspects) {
					AspectDefinition aspect = dictionaryService.getAspect(aspectQN);
					
					JSONObject aspectObject = getAspectObject(aspectQN);;
					if(aspectObject!=null) items.add(aspectObject);
				}
				Collections.sort(items, new JSONComparator());
			} else {
				Collection<QName> aspects = dictionaryService.getAllAspects();
				for(QName aspectQN : aspects) {
					AspectDefinition aspect = dictionaryService.getAspect(aspectQN);
					
					JSONObject aspectObject = getAspectObject(aspectQN);;
					if(aspectObject!=null) items.add(aspectObject);
				}
				Collections.sort(items, new JSONComparator());
			}
			result.put("data", items);
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}
		return result;
	}
	
	public JSONObject getParentAspects(String nodeRef, String documentType) {
		JSONObject result = new JSONObject();
		try {
			List<JSONObject> items = new ArrayList<>();
			TypeDefinition type = null;
			try{
				QName documentTypeQName = QName.createQName(documentType, namespaceService);
				type = dictionaryService.getType(documentTypeQName);
			} catch(Exception e) {}
			QName parentDocumentTypeQName = null;
			if(type==null) {
				ContentReader contentReader = contentService.getReader(new NodeRef(nodeRef), ContentModel.PROP_CONTENT);
				if (contentReader != null) {
					M2Model model = M2Model.createModel(contentReader.getContentInputStream());
		
		            List<M2Type> types = model.getTypes();
		            M2Type firstType = null;
		            if (types != null && !types.isEmpty()) {//для случая, если в модели нет типов (например только аспекты)
		            	for(M2Type mtype:types) {
							if(mtype.getName().equals(documentType)) {
								firstType = mtype;
							}
						}
		            }
		
					if (firstType != null) {
						parentDocumentTypeQName = QName.createQName(firstType.getParentName(), namespaceService);
					}
				}
			} else {
				parentDocumentTypeQName = type.getParentName();
			}
			if(parentDocumentTypeQName!=null) {
				TypeDefinition parentType = dictionaryService.getType(parentDocumentTypeQName);
				Set<QName> aspects = parentType.getDefaultAspectNames();
				for(QName aspectQN : aspects) {
					JSONObject aspectObject = getAspectObject(aspectQN);
					if(aspectObject!=null) items.add(aspectObject);
				}
				Collections.sort(items, new JSONComparator());
			}
			result.put("data", items);
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}
		return result;
	}
	
	private JSONObject getTableObject(QName aspectQN) throws JSONException {
		JSONObject aspectObject = null;
		AspectDefinition aspect = dictionaryService.getAspect(aspectQN);
		
		for(AssociationDefinition ad: aspect.getAssociations().values()) {
			TypeDefinition td = dictionaryService.getType(ad.getTargetClass().getName());
			//lecm-document:tableDataRowType
			for(PropertyDefinition pd: td.getProperties().values()){
				String tableRowProp = pd.getName().toPrefixString();
				if("lecm-document:tableDataRowType".equals(tableRowProp)) {
					String tableRowType = pd.getDefaultValue();
					QName tableRowTypeQname = QName.createQName(tableRowType, namespaceService);
					TypeDefinition tdRow = dictionaryService.getType(tableRowTypeQname);
					if(tdRow!=null) {
						aspectObject = new JSONObject();
						aspectObject.put("aspectName", aspect.getName().toPrefixString(namespaceService));
						aspectObject.put("aspectTitle", aspect.getTitle(dictionaryService));
						aspectObject.put("name", tableRowType);
						List<JSONObject> props = new ArrayList<>();
						for(PropertyDefinition pdRow: tdRow.getProperties().values()){
							JSONObject propObject = new JSONObject();
							propObject.put("_name", pdRow.getName().toPrefixString(namespaceService));
							propObject.put("title", pdRow.getTitle());
							propObject.put("type", pdRow.getDataType().getName().toPrefixString(namespaceService));
							propObject.put("default", pdRow.getDefaultValue());
							propObject.put("mandatory", pdRow.isMandatory());
							propObject.put("_enabled", pdRow.isIndexed());
							propObject.put("tokenised", pdRow.getIndexTokenisationMode());
							props.add(propObject);
						}
						Collections.sort(props, new JSONComparator("_name"));
						JSONObject tableObject = new JSONObject();
						tableObject.put("name", tableRowType);
						tableObject.put("props", props);
						List<JSONObject> assocs = new ArrayList<>();
						for(AssociationDefinition adRow: tdRow.getAssociations().values()) {
							JSONObject assocObject = new JSONObject();
							assocObject.put("_name", adRow.getName().toPrefixString(namespaceService));
							assocObject.put("title", adRow.getTitle());
							assocObject.put("class", adRow.getTargetClass().getName().toPrefixString(namespaceService));
							assocObject.put("mandatory", adRow.isTargetMandatory());
							assocObject.put("many", adRow.isTargetMany());
							assocs.add(assocObject);
						}
						Collections.sort(assocs, new JSONComparator("_name"));
						tableObject.put("assocs", assocs);
						aspectObject.put("table", tableObject);
					}
				}
			}
		}
		return aspectObject;
	}
	
	public JSONObject getTables(String documentType) {
		JSONObject result = new JSONObject();
		try {
			List<JSONObject> items = new ArrayList<>();
			AspectDefinition type = null;
			QName documentTypeQName = null;
			try{
				documentTypeQName = QName.createQName(documentType, namespaceService);
				type = dictionaryService.getAspect(documentTypeQName);
			} catch(Exception e) {}
			QName parentDocumentTypeQName = null;
			if(type!=null) {
				parentDocumentTypeQName = documentTypeQName;
			}
			if(parentDocumentTypeQName!=null) {
				Collection<QName> aspects = dictionaryService.getSubAspects(parentDocumentTypeQName, true);
				for(QName aspectQN : aspects) {
					AspectDefinition aspect = dictionaryService.getAspect(aspectQN);
					
					JSONObject aspectObject = getTableObject(aspectQN);;
					if(aspectObject!=null) items.add(aspectObject);
				}
				Collections.sort(items, new JSONComparator());
			}
			result.put("data", items);
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}
		return result;
	}
	
	public JSONObject getParentTables(String nodeRef, String documentType) {
		JSONObject result = new JSONObject();
		try {
			List<JSONObject> items = new ArrayList<>();
			TypeDefinition type = null;
			try{
				QName documentTypeQName = QName.createQName(documentType, namespaceService);
				type = dictionaryService.getType(documentTypeQName);
			} catch(Exception e) {}
			QName parentDocumentTypeQName = null;
			if(type==null) {
				ContentReader contentReader = contentService.getReader(new NodeRef(nodeRef), ContentModel.PROP_CONTENT);
				if (contentReader != null) {
					M2Model model = M2Model.createModel(contentReader.getContentInputStream());
		
		            List<M2Type> types = model.getTypes();
		            M2Type firstType = null;
		            if (types != null && !types.isEmpty()) {//для случая, если в модели нет типов (например только аспекты)
		            	for(M2Type mtype:types) {
							if(mtype.getName().equals(documentType)) {
								firstType = mtype;
							}
						}
		            }
		
					if (firstType != null) {
						parentDocumentTypeQName = QName.createQName(firstType.getParentName(), namespaceService);
					}
				}
			} else {
				parentDocumentTypeQName = type.getParentName();
			}
			if(parentDocumentTypeQName!=null) {
				TypeDefinition parentType = dictionaryService.getType(parentDocumentTypeQName);
				Set<QName> aspects = parentType.getDefaultAspectNames();
				for(QName aspectQN : aspects) {
					JSONObject aspectObject = getTableObject(aspectQN);
					if(aspectObject!=null) items.add(aspectObject);
				}
				Collections.sort(items, new JSONComparator());
			}
			result.put("data", items);
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}
		return result;
	}
	
	public JSONObject getModel(String documentType) {
		JSONObject result = new JSONObject();
		try {
			QName documentTypeQName = QName.createQName(documentType, namespaceService);
			TypeDefinition type = dictionaryService.getType(documentTypeQName);
			if(type!=null) {
				
				//_name:"lecm-meetings:model"
				//_xmlns:"http://www.alfresco.org/model/dictionary/1.0"
				//author:"Logic ECM"
				//constraints
				//	constraint
				//		_name:"lecm-meetings:present-string-constraint"
				//		_type:"ru.it.lecm.documents.constraints.PresentStringConstraint"
				//		parameter
				//			_name:"presentString"
				//			value:#cdata:"{lecm-events:title}"
				//description:"Совещания"
				//imports:
				//	import:[
				//		{_prefix:"lecm-document"
				//		_uri:"http://www.it.ru/logicECM/document/1.0"
				//namespaces
				//	namespace
				//		_prefix:"lecm-meetings"
				//		_uri:"http://www.it.ru/logicECM/meetings/1.0"
				//types{
				//	type[
				//		{_name:"lecm-meetings:document"
				//		associations:
				//			association:[
				//				{_name:"lecm-meetings:chairman-assoc"
				//				source:{
				//					mandatory:"false"
				//					many:"true"
				//				target:{
				//					class:"lecm-orgstr:employee"
				//					mandatory:"true"
				//					many:"false"
				//		mandatory-aspects:
				//			aspect:["lecm-meetings-ts:items-table-aspect","lecm-meetings-ts:holding-items-table-aspect","lecmApproveAspects:approvalDetailsAspect"]
				//		overrides:
				//			property:[
				//				{_name:"lecm-events:send-notifications"
				//				default:"false"
				//		parent:"lecm-events:document"
				//		properties:{
				//			property:[
				//				{_name:"lecm-meetings:approve-agenda"
				//				default:"true"
				//				mandatory:"false"
				//				type:"d:boolean"
				//				index
				//					_enabled:"true"
				//					tokenised:"both"
				//		title:"Logic Ecm Meetings Document"
				//version:"1.0"
				result.put("_name", type.getModel().getName().toPrefixString());
				result.put("file", models.getModels().get(type.getName().toPrefixString()));
				result.put("_xmlns", "http://www.alfresco.org/model/dictionary/1.0");
				result.put("author", type.getModel().getAuthor());
				JSONObject constraints = new JSONObject();
				List<JSONObject> constraintArray = new ArrayList<>();
				for(ConstraintDefinition cd: dictionaryService.getConstraints(type.getModel().getName(), false)){
					JSONObject constraintObject = new JSONObject();
					constraintObject.put("_name",cd.getName().toPrefixString());
					constraintObject.put("_type",cd.getConstraint().getType());
					List<JSONObject> parameterArray = new ArrayList<>();
					for(String name:cd.getConstraint().getParameters().keySet()){
						JSONObject parameterObject = new JSONObject();
						parameterObject.put("_name",name);
						if(cd.getConstraint().getParameters().get(name) instanceof List) {
							JSONObject listObject = new JSONObject();
							listObject.put("value",cd.getConstraint().getParameters().get(name));
							parameterObject.put("list",listObject);
						} else {
							parameterObject.put("value",cd.getConstraint().getParameters().get(name));
						}
						parameterArray.add(parameterObject);
					}
					constraintObject.put("parameter",parameterArray);
					constraintArray.add(constraintObject);
				}
				constraints.put("constraint",constraintArray);
				result.put("constraints", constraints);
				result.put("description", type.getModel().getDescription(dictionaryService));
				JSONObject imports = new JSONObject();
				List<JSONObject> importArray = new ArrayList<>();
				for(NamespaceDefinition ns: type.getModel().getImportedNamespaces()){
					JSONObject importObject = new JSONObject();
					importObject.put("_prefix",ns.getPrefix());
					importObject.put("_uri",ns.getUri());
					importArray.add(importObject);
				}
				imports.put("import",importArray);
				result.put("imports",imports);
				JSONObject namespaces = new JSONObject();
				List<JSONObject> namespaceArray = new ArrayList<>();
				for(NamespaceDefinition ns: type.getModel().getNamespaces()){
					JSONObject namespaceObject = new JSONObject();
					namespaceObject.put("_prefix",ns.getPrefix());
					namespaceObject.put("_uri",ns.getUri());
					namespaceArray.add(namespaceObject);
				}
				namespaces.put("namespace",namespaceArray);
				result.put("namespaces", namespaces);
				JSONObject types = new JSONObject();
				List<JSONObject> typeArray = new ArrayList<>();
					JSONObject typeObject = new JSONObject();
					typeObject.put("_name",type.getName().toPrefixString());
					String typeNS = type.getName().toPrefixString().substring(0,type.getName().toPrefixString().indexOf(":"));
					JSONObject associations = new JSONObject();
					List<JSONObject> associationArray = new ArrayList<>();
					for(AssociationDefinition ad: type.getAssociations().values()) {
						if( ad.getName().toPrefixString(namespaceService).startsWith(typeNS)) {
							JSONObject associationObject = new JSONObject();
							associationObject.put("_name",ad.getName().toPrefixString().replace(typeNS+":",""));
							associationObject.put("title",ad.getTitle());
							JSONObject source = new JSONObject();
							source.put("mandatory",ad.isSourceMandatory());
							source.put("many",ad.isSourceMany());
							associationObject.put("source",source);
							JSONObject target = new JSONObject();
							target.put("class",ad.getTargetClass().getName().toPrefixString());
							target.put("mandatory",ad.isTargetMandatory());
							target.put("many",ad.isTargetMany());
							associationObject.put("target",target);
							associationArray.add(associationObject);
						}
					}
					Collections.sort(associationArray, new JSONComparator("_name"));
					associations.put("association",associationArray);
					typeObject.put("associations",associations);
					JSONObject mandatoryAspects = new JSONObject();
					List<String> aspectArray = new ArrayList<>();
					for(AspectDefinition ad: type.getDefaultAspects()){
						aspectArray.add(ad.getName().toPrefixString());
					}
					mandatoryAspects.put("aspect",aspectArray);
					typeObject.put("mandatory-aspects",mandatoryAspects);
//					typeObject.put("overrides",);
					typeObject.put("parent",type.getParentName().toPrefixString());
					JSONObject properties = new JSONObject();
					List<JSONObject> propertyArray = new ArrayList<>();
					for(PropertyDefinition pd: type.getProperties().values()){
						if( pd.getName().toPrefixString(namespaceService).startsWith(typeNS)) {
							JSONObject propertyObject = new JSONObject();
							propertyObject.put("_name",pd.getName().toPrefixString().replace(typeNS+":",""));
							propertyObject.put("title",pd.getTitle());
							propertyObject.put("default",pd.getDefaultValue());
							propertyObject.put("mandatory",pd.isMandatory());
							propertyObject.put("type",pd.getDataType().getName().toPrefixString());
							JSONObject indexObject = new JSONObject();
							indexObject.put("_enabled",pd.isIndexed());
							indexObject.put("tokenised",pd.getIndexTokenisationMode().toString().toLowerCase());
							propertyObject.put("index",indexObject);
							propertyArray.add(propertyObject);
						}
					}
					Collections.sort(propertyArray, new JSONComparator("_name"));
					properties.put("property",propertyArray);
					typeObject.put("properties",properties);
					typeObject.put("title",type.getTitle());
				typeArray.add(typeObject);
				types.put("type",typeArray);
				result.put("types", types);
				result.put("version", type.getModel().getVersion());
			}
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}
		return result;
	}
}

class JSONComparator implements Comparator<JSONObject> {
	private String key;
	public JSONComparator() {
		this.key = "name";
	}
	public JSONComparator(String key) {
		this.key = key;
	}
	@Override
	public int compare(JSONObject o1, JSONObject o2) {
		String title1 = null;
		String title2 = null;
		try {
			title1 = o1.getString(key);
		} catch (JSONException ignored) {}
		try {
			title2 = o2.getString(key);
		} catch (JSONException ignored) {}

		if (title1 == null && title2 == null) {
			return 0;
		} else if (title1 == null && title2 != null) {
			return -1;
		} else if (title2 == null && title1 != null) {
			return 1;
		} else {
			return title1.compareTo(title2);
		}
	}
}