package ru.it.lecm.documents.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ParameterCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.LecmTransactionHelper;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.*;
import ru.it.lecm.documents.constraints.ArmUrlConstraint;

import java.io.Serializable;
import java.util.*;

/**
 * User: orakovskaya
 * Date: 12.03.13
 */
public class DocumentWebScriptBean extends BaseWebScript {
    private static final Logger logger = LoggerFactory.getLogger(DocumentWebScriptBean.class);

    private DocumentService documentService;
    private DocumentFrequencyAnalysisService documentFrequencyAnalysisService;
    private NodeService nodeService;
    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private TransactionService transactionService;
    private DocumentMembersService documentMembersService;
    private LecmTransactionHelper lecmTransactionHelper;

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

    public void setDocumentFrequencyAnalysisService(DocumentFrequencyAnalysisService documentFrequencyAnalysisService) {
        this.documentFrequencyAnalysisService = documentFrequencyAnalysisService;
    }

    public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
        this.lecmTransactionHelper = lecmTransactionHelper;
    }


    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Получить рейтинг документа
     *
     * @param documentNodeRef документ
     */
    public String getRating(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            return this.documentService.getRating(documentRef);
        }
        return null;
    }

    /**
     * Получить число проголосовавших за  документ
     *
     * @param documentNodeRef документ
     */
    @SuppressWarnings("unused")
    public Integer getRatedPersonCount(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            return this.documentService.getRatedPersonCount(documentRef);
        }
        return null;
    }

    /**
     * Получить рейтинг документа для текущего сотрудника
     *
     * @param documentNodeRef документ
     */
    @SuppressWarnings("unused")
    public Integer getMyRating(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            return this.documentService.getMyRating(documentRef);
        }
        return null;
    }

    /**
     * Установить рейтинг документу от текущего сотрудника
     *
     * @param documentNodeRef документ
     */
    @SuppressWarnings("unused")
    public Integer setMyRating(String documentNodeRef, String rating) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            return this.documentService.setMyRating(documentRef, Integer.parseInt(rating));
        }
        return null;
    }

    /**
     * Создать документ заданного типа с заданными свойствами и ассоциациями
     *
     * @param type         тип документа
     * @param properties   свойства документа
     * @param associations ассоциации документа
     */
    public ScriptNode createDocument(String type, Scriptable properties, Scriptable associations) {
        Map<String, String> mapProperties = new HashMap<String, String>();
        Map<String, String> mapAssociation = new HashMap<String, String>();
        if (properties != null) {
            mapProperties = takeProperties(Context.getCurrentContext().getElements(properties));
        }
        if (associations != null) {
            mapAssociation = takeProperties(Context.getCurrentContext().getElements(associations));
        }

        NodeRef documentRef = documentService.createDocument(type, mapProperties, mapAssociation);

        return new ScriptNode(documentRef, serviceRegistry, getScope());
    }

    /**
     * Произвести редактирование документа
     *
     * @param nodeRef    документ
     * @param properties свойства документа
     */
    public ScriptNode editDocument(String nodeRef, Scriptable properties) {
        return editDocument(nodeRef, Context.getCurrentContext().getElements(properties));
    }

    /**
     * Произвести редактирование документа
     *
     * @param nodeRef    документ
     * @param properties свойства документа
     */
    public ScriptNode editDocument(String nodeRef, Object[] properties) {
        NodeRef documentRef = new NodeRef(nodeRef);
        Map<String, String> property = takeProperties(properties);
        documentRef = documentService.editDocument(documentRef, property);
        return new ScriptNode(documentRef, serviceRegistry, getScope());
    }

    /**
     * Получить директорию с черновиками
     */
    @SuppressWarnings("unused")
    public ScriptNode getDraftsRoot() {
        return new ScriptNode(documentService.getDraftRoot(), serviceRegistry, getScope());
    }

    /**
     * Получить путь (xpath) к  директории с черновиками
     */
    @SuppressWarnings("unused")
    public String getDraftsPath() {
        return documentService.getDraftPath();
    }

    /**
     * Получить путь (xpath) к  директории с документами
     */
    @SuppressWarnings("unused")
    public String getDocumentsPath() {
        return documentService.getDocumentsFolderPath();
    }

    /**
     * Получить директорию с черновиками заданного типа
     *
     * @param rootType тип документов
     */
    @SuppressWarnings("unused")
    public ScriptNode getDraftRoot(String rootType) throws WriteTransactionNeededException {
        ParameterCheck.mandatory("rootType", rootType);
        final QName rootQNameType = QName.createQName(rootType, namespaceService);
        if (rootQNameType != null) {
//			Стоит учесть, что веб-скрипт, где используется этот метод не транзакционный, поэтому
//			Если какая-либо транзакция залочит ноду - получение отвалится по таймауту.
            NodeRef ref = documentService.getDraftRootByType(rootQNameType);
//			TODO: В случае, если папки черновиков ещё нет - создадим в транзакции.
            if (ref == null) {
//                RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
//                ref = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
//
//                    @Override
//                    public NodeRef execute() throws Throwable {
//                        return 
            	ref = documentService.createDraftRoot(rootQNameType);
//                    }
//
//                },false,true);
            }

            return new ScriptNode(ref, serviceRegistry, getScope());
        } else {
            return null;
        }
    }

	/**
     * Получить заголовок для типа
     *
     * @param documentType тип документов
     */
    public String getTypeTitle(String documentType) {
        ParameterCheck.mandatory("documentType", documentType);
        final QName documentQNameType = QName.createQName(documentType, namespaceService);
        if (documentQNameType != null) {
	        TypeDefinition typeDefinition = dictionaryService.getType(documentQNameType);
	        if (typeDefinition != null) {
		        return typeDefinition.getTitle(dictionaryService);
	        }
        }
        return null;
    }

    /**
     * Получить путь к директории с черновиками заданного типа
     *
     * @param rootType тип документов
     */
    @SuppressWarnings("unused")
    public String getDraftPath(String rootType) {
        ParameterCheck.mandatory("rootType", rootType);
        QName rootQNameType = QName.createQName(rootType, namespaceService);
        if (rootQNameType != null) {
            return documentService.getDraftPathByType(rootQNameType);
        }
        return null;
    }

    private Map<String, String> takeProperties(Object[] object) {
        List<String> list = getElements(object);
        Map<String, String> map = new HashMap<String, String>();
        String[] string;
        String value;
        for (String str : list) {
            if (!str.equals("")) {
                string = str.split("=");
                value = (string.length < 2) ? "" : string[1];
                map.put(string[0], value);
            }
        }
        return map;
    }

    /**
     * Получить фильтры по типам (Все, Зарегистрированные и т.д) для документов заданного типа
     *
     * @param type       тип документов
     * @param forArchive фильтры для архивных
     */
    public Map<String, String> getFilters(String type, boolean forArchive) {
        if (!forArchive) {
            return DocumentStatusesFilterBean.getFilterForType(type);
        } else {
            return DocumentStatusesFilterBean.getArchiveFilterForType(type);
        }
    }

    /**
     * Получить фильтр по умолчанию для типа документ ов
     *
     * @param type       тип документов
     * @param forArchive фильтр для архивных
     */
    @SuppressWarnings("unused")
    public Map<String, String> getDefaultFilter(String type, boolean forArchive) {
        Map<String, String> filters = getFilters(type, forArchive);
        HashMap<String, String> defaultFilter = new HashMap<String, String>();
        String defaultKey = DocumentStatusesFilterBean.getDefaultFilter(type, forArchive);
        if (filters != null) {
            if (defaultKey != null) {
                defaultFilter.put(defaultKey, filters.get(defaultKey));
            } else {
                for (Map.Entry<String, String> entry : filters.entrySet()) {
                    defaultFilter.put(entry.getKey(), entry.getValue());
                    break;
                }
            }
        } else {
            defaultFilter.put(defaultKey, "*");
        }
        return defaultFilter;
    }

    /**
     * Получить количество участников для данного типа документа
     *
     * @return список nodeRef
     */
    @SuppressWarnings("unused")
    public Integer getAmountMembers(final String type) {
//		TODO: Метод getMembers дёрагал метод getOrCreateDocMemberUnit,
//		который был благополучно разделён. Поэтому сделаем проверку на существование
        if (documentMembersService.getDocMembersUnit(type) == null) {
            RetryingTransactionHelper.RetryingTransactionCallback cb = new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                @Override
                public Void execute() throws Throwable {
                    documentMembersService.createDocMemberUnit(type);
                    return null;
                }
            };
            lecmTransactionHelper.doInTransaction(cb, false);
        }
        QName qNameType = QName.createQName(type, namespaceService);
        return documentService.getMembers(qNameType).size();
    }

    /**
     * Получить количество документов
     *
     * @param types     типы документов
     * @param paths     пути, в которых искать
     * @param statuses  статусы
     * @param filterStr дополнительный фильтр для поиска
     */
    @SuppressWarnings("unused")
    public Long getAmountDocuments(Scriptable types, Scriptable paths, Scriptable statuses, String filterStr) {
        List<String> docTypes = getElements(Context.getCurrentContext().getElements(types));
        List<QName> qNameTypes = new ArrayList<QName>();
        String queryFilter = "";

        if (filterStr != null && !filterStr.isEmpty()) {
            queryFilter = getFilterQuery(filterStr);
        }

        for (String docType : docTypes) {
            qNameTypes.add(QName.createQName(docType, namespaceService));
        }
        return documentService.getAmountDocumentsByFilter(qNameTypes,
                getElements(Context.getCurrentContext().getElements(paths)), getElements(Context.getCurrentContext().getElements(statuses)), queryFilter, null);
    }

    /**
     * Получить  список пермиссий, по которым предоставляется доступ к странице с документами заданного типа
     *
     * @param type тип документов
     */
    @SuppressWarnings("unused")
    public List<String> getAccessPermissionsList(String type) {
        return DocumentsPermissionsBean.getPermissionsByType(type);
    }

    /**
     * Получить свойства документа
     *
     * @param nodeRef документ
     */
    public String getProperties(String nodeRef) {
        NodeRef documentRef = new NodeRef(nodeRef);
        JSONArray properties = new JSONArray();
        JSONObject object = new JSONObject();
        Map<QName, Serializable> data = documentService.getProperties(documentRef);
        try {
            for (Map.Entry<QName, Serializable> e : data.entrySet()) {
                object.put(e.getKey().getLocalName(), e.getValue());
            }
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        return properties.put(object).toString();
    }

    /**
     * Получить название свойства, в котором хранится автор документа
     *
     * @param docType тип документов
     */
    @SuppressWarnings("unused")
    public String getAuthorProperty(String docType) {
        QName qNameType = QName.createQName(docType, namespaceService);
        if (qNameType != null) {
            return documentService.getAuthorProperty(qNameType);
        }
        return null;
    }

    private ArrayList<String> getElements(Object[] object) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Object obj : object) {
            if (obj != null && obj instanceof NativeJavaObject) {
                NativeJavaObject element = (NativeJavaObject) obj;
                arrayList.add((String) element.unwrap());
            } else if (obj != null && obj instanceof String) {
                arrayList.add(obj.toString());
            }
        }
        return arrayList;
    }


    /**
     * Оборачиваем узел в ссылку на document
     *
     * @param node        документ
     * @param description описание (текст ссылки)
     */
    @SuppressWarnings("unused")
    public String wrapperDocumentLink(ScriptNode node, String description) {
        return wrapperLink(node.getNodeRef().toString(), description, documentService.getDocumentUrl(node.getNodeRef()));
    }

    /**
     * Получить запрос для фильтра
     *
     * @param filter код фильтра + параметры
     */
    public String getFilterQuery(String filter) {
        String[] filterParts = filter.split("\\|");
        if (filterParts.length > 0) {
            String filterId = filterParts[0];
            String filterData = "";
            if (filterParts.length == 2) {
                filterData = filterParts[1];
            }

            DocumentFilter docFilter = FiltersManager.getFilterById(filterId);
            if (docFilter != null) {
                if (filterData.isEmpty()) {
                    filterData = docFilter.getParamStr();
                }
                Object[] params = filterData != null ? filterData.split("/") : new Object[]{};
                return docFilter.getQuery(params);
            }
        } else {
            logger.warn("Check filter code!!! Filter=" + filter);
        }
        return "";
    }

    /**
     * Создать дубликат документа
     *
     * @param nodeRef документ
     */
    @SuppressWarnings("unused")
    public ScriptNode dublicateDocument(String nodeRef) {
        ParameterCheck.mandatory("nodeRef", nodeRef);
        NodeRef ref = new NodeRef(nodeRef);
        if (nodeService.exists(ref)) {
            NodeRef createdNode = documentService.duplicateDocument(ref);
            if (createdNode != null) {
                return new ScriptNode(createdNode, serviceRegistry, getScope());
            }
        }
        return null;
    }

    /**
     * Проверяет наличие настроек копирования
     * @param document документ
     * @return наличие настроек копирования
     */
    public boolean canCopyDocument(ScriptNode document) {
        return documentService.canCopyDocument(document.getNodeRef());
    }

    /**
     * Получить URL для копирования документа
     *
     * @param nodeRef документ
     */
    @SuppressWarnings("unused")
    public String getDocumentCopyURL(String nodeRef) {
        ParameterCheck.mandatory("nodeRef", nodeRef);
        NodeRef ref = NodeRef.isNodeRef(nodeRef) ? new NodeRef(nodeRef): null;

        return documentService.getDocumentCopyURL(ref);
    }

    /**
     * Получить ссылку на АРМ документа
     *
     * @param nodeRef документ
     */
    public String getArmUrl(String nodeRef) {
        ParameterCheck.mandatory("nodeRef", nodeRef);
        NodeRef ref = new NodeRef(nodeRef);
        if (nodeService.exists(ref)) {
            QName type = nodeService.getType(ref);
            if (type != null) {
                ConstraintDefinition constraint = dictionaryService.getConstraint(QName.createQName(type.getNamespaceURI(), DocumentService.CONSTRAINT_ARM_URL));
                if (constraint != null && (constraint.getConstraint() instanceof ArmUrlConstraint)) {
                    return ((ArmUrlConstraint) constraint.getConstraint()).getArmUrl();
                }
            }
        }
        return null;
    }

    /**
     * Получить строку представления документа
     *
     * @param document документ
     */
    @SuppressWarnings("unused")
    public String getPresentString(ScriptNode document) {
        return documentService.getPresentString(document.getNodeRef());
    }

    /**
     * Возвращает автора документа
     *
     * @param document - текстовая ссылка на документ
     * @return ноду сотрудника-автор или null, если по какой-то причине нет такого свойства или сохранены битые данные
     */
    @SuppressWarnings("unused")
    public ScriptNode getDocumentAuthor(String document) {
        ParameterCheck.mandatory("document", document);
        if (NodeRef.isNodeRef(document)) {
            NodeRef ref = new NodeRef(document);
            if (nodeService.exists(ref)) {
                NodeRef author = documentService.getDocumentAuthor(ref);
                return new ScriptNode(author, serviceRegistry, getScope());
            }
        }
        return null;
    }

    /**
     * Получить регистрационный номер документа
     *
     * @param document документ
     */
    @SuppressWarnings("unused")
    public String getDocumentRegNumber(ScriptNode document) {
        ParameterCheck.mandatory("document", document);
        if (document != null) {
            return documentService.getDocumentRegNumber(document.getNodeRef());
        }
        return null;
    }

    public void finalizeToUnit(ScriptNode document, Boolean sharedFolder, ScriptNode primaryUnit, Scriptable additionalUnits) {
        List<NodeRef> additionalUnitsRefs = getNodeRefsFromScriptableCollection(additionalUnits);
        documentService.finalizeToUnit(document.getNodeRef(), sharedFolder, primaryUnit.getNodeRef(), additionalUnitsRefs);
    }

    public void finalizeToUnit(ScriptNode document, Boolean sharedFolder, ScriptNode primaryUnit) {
        finalizeToUnit(document, sharedFolder, primaryUnit, null);
    }

    public void finalizeToUnit(ScriptNode document, ScriptNode primaryUnit, Scriptable additionalUnits) {
        finalizeToUnit(document, null, primaryUnit, additionalUnits);
    }

    public void finalizeToUnit(ScriptNode document, ScriptNode primaryUnit) {
        finalizeToUnit(document, null, primaryUnit, null);
    }

    /**
     * Получить список документов по поисковому запросу
     *
     * @param query     поисковый запрос
     * @param skipCount сколько документов пропустить при выполнении запроса
     * @param loadCount число документов для загрузки
     */
    public Scriptable getDocumentsByQuery(String query, int skipCount, int loadCount) {
        List<SearchParameters.SortDefinition> sort = new ArrayList<SearchParameters.SortDefinition>();
        sort.add(new SearchParameters.SortDefinition(SearchParameters.SortDefinition.SortType.FIELD, "@" + ContentModel.PROP_MODIFIED.toString(), false));

        List<NodeRef> refs = documentService.getDocumentsByQuery(query, sort, skipCount, loadCount);
        return createScriptable(refs);
    }

    /**
     * Добавить документ в избранное
     * @param document - ссылка на документ
     */
    public void addToFavourites(ScriptNode document) {
        documentService.addToFavourites(document.getNodeRef());
    }

    /**
     * Удалить документ из избранного
     * @param document - ссылка на документ
     */
    public void removeFromFavourites(ScriptNode document) {
        documentService.removeFromFavourites(document.getNodeRef());
    }

	/**
	 * Получение списка зарегистрированных типов документов
	 * @return список зарегистрированных типов документов
	 */
	@SuppressWarnings("unused")
	public Map<String, String> getRegisteredTypes() {
		Map<String, String> results = new HashMap<>();

		Collection<QName> types = documentService.getDocumentSubTypes();
		if (types != null) {
			for (QName type : types) {
				TypeDefinition typeDef = dictionaryService.getType(type);
				results.put(type.toPrefixString(namespaceService), typeDef.getTitle(dictionaryService));
			}
		}
		return results;
	}

    public String getCreateUrl(ScriptNode document) {
        return documentService.getCreateUrl(document.getQNameType());
    }

    public String getEditUrl(ScriptNode document) {
        return documentService.getEditUrl(document.getQNameType());
    }

    public String getViewUrl(ScriptNode document) {
        return documentService.getViewUrl(document.getQNameType());
    }

    public String getViewUrl(NodeRef document) {
        QName type = nodeService.getType(document);
        return documentService.getViewUrl(type);
    }


    /**
     * Получение списка зарегистрированных типов документов в виде древовидной структуры
     * @return список зарегистрированных типов документов м
     */
    @SuppressWarnings("unused")
    public List<Map.Entry> getRegisteredTypesTree() {
        List<Map.Entry> results = new ArrayList<>();

        TypeDefinition typeDef = dictionaryService.getType(DocumentService.TYPE_BASE_DOCUMENT);
        Map<String, String> base = new HashMap<>();
        base.put(DocumentService.TYPE_BASE_DOCUMENT.toPrefixString(namespaceService), typeDef.getTitle(dictionaryService));
        for (Map.Entry<String, String> stringEntry : base.entrySet()) {
            results.add(stringEntry);
        }

        results.addAll(getSubtypesInternal(DocumentService.TYPE_BASE_DOCUMENT, 1).entrySet());
        return results;
    }

    private Map<String, String> getSubtypesInternal(QName parentType, int level) {
        Map<String, String> results = new LinkedHashMap<>();
        Collection<QName> types = dictionaryService.getSubTypes(parentType, false);
        Collection<QName> typesList = new ArrayList<>();
        if (types != null) {
            typesList.addAll(types);
            StringBuilder indent = new StringBuilder();
            for (int i = 0; i < level; i++) {
                indent.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            }
            level++;
            Collections.sort((List<QName>) typesList, new Comparator<QName>() {
                @Override
                public int compare(QName o1, QName o2) {
                    try {
                        TypeDefinition typeDef1 = dictionaryService.getType(o1);
                        TypeDefinition typeDef2 = dictionaryService.getType(o2);
                        return typeDef1.getTitle(dictionaryService).toUpperCase().compareTo(typeDef2.getTitle(dictionaryService).toUpperCase());

                    } catch (Exception ignored) {

                    }
                    return 0;
                }
            });

            for (QName type : typesList) {
                TypeDefinition typeDef = dictionaryService.getType(type);
                results.put(type.toPrefixString(namespaceService), indent.toString() + "&nbsp;" + typeDef.getTitle(dictionaryService));
                results.putAll(getSubtypesInternal(type, level));
            }
        }
        return results;
    }
    
    public String getDocumentTypeLabel(String docType) {
    	return documentService.getDocumentTypeLabel(docType);
    }
}
