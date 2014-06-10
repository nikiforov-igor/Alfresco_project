package ru.it.lecm.businessjournal.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.repo.search.impl.lucene.SolrJSONResultSet;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.policies.BusinessJournalOnCreateAssocsPolicy;

import java.io.Serializable;
import java.util.*;

/**
 * @author dbashmakov Date: 25.12.12 Time: 10:18
 */
public class LocalBusinessJournalServiceImpl extends AbstractBusinessJournalService implements BusinessJournalService {

    private static final String BUSINESS_JOURNAL_POST_TRANSACTION_PENDING_OBJECTS = "business_journal_post_transaction_pending_objects";
	
    private static final Logger logger = LoggerFactory.getLogger(LocalBusinessJournalServiceImpl.class);

    private SearchService searchService;
    private NamespaceService namespaceService;

    private BusinessJournalOnCreateAssocsPolicy businessJournalOnCreateAssocsPolicy;

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setBusinessJournalOnCreateAssocsPolicy(BusinessJournalOnCreateAssocsPolicy businessJournalOnCreateAssocsPolicy) {
        this.businessJournalOnCreateAssocsPolicy = businessJournalOnCreateAssocsPolicy;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return getBusinessJournalDirectory();
    }

    /**
     * Метод инициализвции сервиса Создает рабочую директорию - если она еще не
     * создана. Записыывает в свойства сервиса nodeRef директории с
     * бизнес-журналами
     */
    public void init() {
        businessJournalOnCreateAssocsPolicy.setBusinessJournalService(this);
        bjRootID = BJ_ROOT_ID;
        bjArchiveID = BJ_ARCHIVE_ROOT_ID;
    }

    @Override
    public void saveToStore(final BusinessJournalRecord record) throws Exception {
        final NodeRef objectType = record.getObjectType();

        String type = record.getObjectTypeText();

        String category = record.getEventCategoryText();
        final List<String> directoryPath = getDirectoryPath(type, category, record.getDate());
        NodeRef saveDirectoryRef = getFolder(getBusinessJournalDirectory(),directoryPath);
        if (null == saveDirectoryRef) {
                saveDirectoryRef = createPath(getBusinessJournalDirectory(), directoryPath);
        }
        // создаем ноду
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(7);
        properties.put(PROP_BR_RECORD_DATE, record.getDate());
        properties.put(PROP_BR_RECORD_DESC, record.getRecordDescription());
        properties.put(PROP_BR_RECORD_INITIATOR, record.getInitiatorText());
        properties.put(PROP_BR_RECORD_MAIN_OBJECT, record.getMainObjectDescription());
        if (record.getObjects() != null && record.getObjects().size() > 0) {
            for (int i = 0; i < record.getObjects().size() && i < MAX_SECONDARY_OBJECTS_COUNT; i++) {
                String description = record.getObjects().get(i).getDescription();
                properties.put(QName.createQName(BJ_NAMESPACE_URI, getSecondObjPropName(i + 1)), description);
            }
        }

        ChildAssociationRef associationRef = nodeService.createNode(saveDirectoryRef, ContentModel.ASSOC_CONTAINS,
                QName.createQName(BJ_NAMESPACE_URI, GUID.generate()), TYPE_BR_RECORD, properties);
        NodeRef result = associationRef.getChildRef();

        // создаем ассоциации
        if (record.getInitiator() != null && nodeService.exists(record.getInitiator())) {
            nodeService.createAssociation(result, record.getInitiator(), ASSOC_BR_RECORD_INITIATOR);
        }
//                if (record.getMainObject() != null && nodeService.exists(record.getMainObject())) {
//                    nodeService.createAssociation(result, record.getMainObject(), ASSOC_BR_RECORD_MAIN_OBJ);
//                }
        if (record.getMainObject() != null) {
            nodeService.setProperty(result, ASSOC_BR_RECORD_MAIN_OBJ_REF, record.getMainObject().toString());
        }

        // необязательные
        if (record.getEventCategory() != null && nodeService.exists(record.getEventCategory())) {
            nodeService.createAssociation(result, record.getEventCategory(), ASSOC_BR_RECORD_EVENT_CAT);
        }
        if (objectType != null && nodeService.exists(record.getObjectType())) {
            nodeService.createAssociation(result, objectType, ASSOC_BR_RECORD_OBJ_TYPE);
        }

        if (record.getObjects() != null && record.getObjects().size() > 0) {
            for (int j = 0; j < record.getObjects().size() && j < MAX_SECONDARY_OBJECTS_COUNT; j++) {
                if (record.getObjects().get(j).getNodeRef() != null) {
//                            nodeService.createAssociation(result, record.getObjects().get(j).getNodeRef(), QName.createQName(BJ_NAMESPACE_URI, getSeconObjAssocName(j + 1)));
                    nodeService.setProperty(result, QName.createQName(BJ_NAMESPACE_URI, getSeconObjAssocName(j + 1) + "-ref"), record.getObjects().get(j).getNodeRef());
                }
            }
        }
    }

    private String getSeconObjAssocName(int j) {
        return "bjRecord-secondaryObj" + j + "-assoc";
    }

    private String getSecondObjPropName(int i) {
        return "bjRecord-secondaryObj" + i;
    }

    /**
     * Метод, возвращающий список ссылок на записи бизнес-журнала,
     * сформированные за заданный период
     *
     * @param begin - начальная дата
     * @param end - конечная дата
     * @return список ссылок
     */
    @Override
    public List<BusinessJournalRecord> getRecordsByInterval(Date begin, Date end) {
        List<NodeRef> records = new ArrayList<NodeRef>(10);
        final String MIN = begin != null ? DateFormatISO8601.format(begin) : "MIN";
        final String MAX = end != null ? DateFormatISO8601.format(end) : "MAX";
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        sp.setQuery("TYPE:\"" + TYPE_BR_RECORD.toString() + "\" AND @lecm\\-busjournal\\:bjRecord\\-date:[" + MIN + " TO " + MAX + "]");
        ResultSet results = null;
        try {
            results = searchService.query(sp);
            for (ResultSetRow row : results) {
                NodeRef currentNodeRef = row.getNodeRef();
                if (!isArchive(currentNodeRef)) {
                    records.add(currentNodeRef);
                }
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
        return pack(records);
    }

    private List<String> getDirectoryPath(String type, String category, Date date) {
        final List<String> directoryPaths = new ArrayList<String>();
        if (type != null) {
            directoryPaths.add(type);
        }
        if (category != null) {
            directoryPaths.add(category);
        }
        if (date != null) {
            directoryPaths.addAll(getDateFolderPath(date));
        }
        return directoryPaths;
    }
    
    @Override
    public List<BusinessJournalRecord> getRecordsByParams(String objectTypeRefs, Date begin, Date end, String whoseKey, Boolean checkMainObject) {
        return getRecordsByParams(objectTypeRefs, begin, end, whoseKey, checkMainObject, null, null);
    }

    @Override
    public List<BusinessJournalRecord> getRecordsByParams(String objectTypeRefs, Date begin, Date end, String whoseKey, Boolean checkMainObject, Integer skipCount, Integer maxItems) {
        List<NodeRef> records = new ArrayList<NodeRef>(10);
        final String MIN = begin != null ? DateFormatISO8601.format(begin) : "MIN";
        final String MAX = end != null ? DateFormatISO8601.format(end) : "MAX";
        ResultSet results = null;
        String query;
        SearchParameters sp = new SearchParameters();

        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        query = "PARENT: \"" + getBusinessJournalDirectory().toString() + "\" TYPE:\"" + TYPE_BR_RECORD.toString() + "\" AND @lecm\\-busjournal\\:bjRecord\\-date:[" + MIN + " TO " + MAX + "] AND @lecm\\-dic\\:active: true ";

        if (objectTypeRefs != null && !"".equals(objectTypeRefs)) {
            String types = "";
            String[] typesArray = objectTypeRefs.split(",");

            for (String type : typesArray) {
                type = type.trim();
                if (!"".equals(type)) {
                    types += ("".equals(types) ? "" : " OR ") + "\"" + type + "\"";
                }
            }
            query += " AND @lecm\\-busjournal\\:bjRecord\\-objType\\-assoc\\-ref:(" + types + ")";
        }
        if (whoseKey != null && !"".equals(whoseKey)) {
            switch (WhoseEnum.valueOf(whoseKey.toUpperCase())) {
                case MY: {
                    NodeRef employee = orgstructureService.getCurrentEmployee();

                    if (employee != null) {
                        query += " AND @lecm\\-busjournal\\:bjRecord\\-initiator\\-assoc\\-ref:\"" + employee.toString() + "\"";
                    }
                    break;
                }
                case DEPARTMENT: {
                    NodeRef boss = orgstructureService.getCurrentEmployee();

                    if (boss != null) {
                        String employeesList = "";
                        List<NodeRef> employees = orgstructureService.getBossSubordinate(boss);

                        employees.add(boss);
                        for (NodeRef employee : employees) {
                            if (employee != null) {
                                employeesList += ("".equals(employeesList) ? "(" : " ") + "\"" + employee.toString() + "\"";
                            }
                        }
                        employeesList += ")";
                        query += " AND @lecm\\-busjournal\\:bjRecord\\-initiator\\-assoc\\-ref:" + employeesList + "";
                    }
                    break;
                }
                case CONTROL: {
                    //todo
                    break;
                }
                default: {

                }
            }
        }
        sp.addSort("@" + PROP_BR_RECORD_DATE.toString(), false);
        sp.setQuery(query);
        if (skipCount != null) {
            sp.setSkipCount(skipCount);
        }
        if (maxItems != null) {
            sp.setMaxItems(maxItems);
        }
        try {
            results = searchService.query(sp);
            for (ResultSetRow row : results) {
                NodeRef rowNodeRef = row.getNodeRef();
                if (checkMainObject != null && checkMainObject) {
                    // проверить доступность основного объекта
                    String mainObjRefStr = (String) nodeService.getProperty(rowNodeRef, ASSOC_BR_RECORD_MAIN_OBJ_REF);
                    if (mainObjRefStr != null && NodeRef.isNodeRef(mainObjRefStr)) {
                            NodeRef nodeRef = new NodeRef(mainObjRefStr);

                            if (nodeService.exists(nodeRef) && lecmPermissionService.hasReadAccess(nodeRef)
                                    && (!stateMachineService.isDraft(nodeRef) || isOwnNode(nodeRef))) {
                                records.add(rowNodeRef);
                            }
                    }
                } else {
                    records.add(rowNodeRef);
                }
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
        return pack(records);
    }

    @Override
    //TODO Refactoring in progress...
    public boolean moveRecordToArchive(final Long recordId) {
        if (!orgstructureService.isCurrentUserTheSystemUser() && !isBJEngineer()) {
            logger.warn("Current employee is not business journal engeneer");
            return false;
        }
        AuthenticationUtil.RunAsWork<Boolean> raw = new AuthenticationUtil.RunAsWork<Boolean>() {
            @Override
            public Boolean doWork() throws Exception {
                        final NodeRef record = serviceRegistry.getNodeService().getNodeRef(recordId);
                        if (!isArchive(record)) {
                            NodeRef objectType = findNodeByAssociationRef(record, ASSOC_BR_RECORD_OBJ_TYPE, null, ASSOCIATION_TYPE.TARGET);
                            final String type;
                            if (objectType != null) {
                                type = (String) nodeService.getProperty(objectType, ContentModel.PROP_NAME);
                            } else {
                                NodeRef mainObject = findNodeByAssociationRef(record, ASSOC_BR_RECORD_MAIN_OBJ, null, ASSOCIATION_TYPE.TARGET);
                                if (mainObject != null) {
                                    type = nodeService.getType(mainObject).getPrefixString().replace(":", "_");
                                } else {
                                    type = null;
                                }
                            }
                            final String category;
                            NodeRef eventCategory = findNodeByAssociationRef(record, ASSOC_BR_RECORD_EVENT_CAT, null, ASSOCIATION_TYPE.TARGET);
                            if (eventCategory != null) {
                                category = (String) nodeService.getProperty(eventCategory, ContentModel.PROP_NAME);
                            } else {
                                category = "unknown";
                            }

                            final List<String> directoryPath = getDirectoryPath((type != null ? type : "unknown"), category, new Date());
                            NodeRef archiveRef = getFolder(getBusinessJournalArchiveDirectory(), directoryPath);
                            if (null == archiveRef) {
                                archiveRef = createPath(getBusinessJournalArchiveDirectory(), directoryPath);
                            }       
                            nodeService.setProperty(record, IS_ACTIVE, false); // помечаем как неактивная запись
                            ChildAssociationRef newRef = nodeService.moveNode(record, archiveRef, ContentModel.ASSOC_CONTAINS, nodeService.getPrimaryParent(record).getQName());
                            return newRef != null;
                        
                        } else {
                            return true;
                        }
                    }
        };
        return AuthenticationUtil.runAsSystem(raw);
    }

    @Override
    public List<BusinessJournalRecord> getHistory(NodeRef nodeRef, String sortColumnLocalName, final boolean sortAscending, boolean showSecondary, boolean showInactive) {
        List<NodeRef> result = getHistory(nodeRef, showSecondary, showInactive);

        final QName sortFieldQName =
                sortColumnLocalName != null && sortColumnLocalName.length() > 0 ?
                        QName.createQName(sortColumnLocalName, namespaceService) : PROP_BR_RECORD_DATE;
        if (sortFieldQName == null) {
            return pack(result);

        }

        class NodeRefComparator<T extends Serializable & Comparable<T>> implements Comparator<NodeRef> {

            @Override
            public int compare(NodeRef nodeRef1, NodeRef nodeRef2) {
                T obj1 = (T) nodeService.getProperty(nodeRef1, sortFieldQName);
                T obj2 = (T) nodeService.getProperty(nodeRef2, sortFieldQName);

                return sortAscending ? obj1.compareTo(obj2) : obj2.compareTo(obj1);
            }
        }

        if (sortFieldQName.equals(PROP_BR_RECORD_DATE)) {
            Collections.sort(result, new NodeRefComparator<Date>());
        }

        if (sortFieldQName.equals(PROP_BR_RECORD_DESC)) {
            Collections.sort(result, new NodeRefComparator<String>());
        }

        return pack(result);
    }

    private List<NodeRef> getHistory(NodeRef nodeRef, boolean showSecondary, boolean showInactive) {
        if (nodeRef == null) {
            return new ArrayList<NodeRef>();
        }

        List<NodeRef> records = new ArrayList<NodeRef>(100);
        ResultSet results = null;
        String query;
        SearchParameters sp = new SearchParameters();

        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
        query = "TYPE:\"" + TYPE_BR_RECORD.toString() + "\"";
        if (showSecondary) {
            query += " AND (@lecm\\-busjournal\\:bjRecord\\-mainObject\\-assoc\\-ref: \"" + nodeRef.toString() + "\" OR "
                    + "lecm\\-busjournal\\:bjRecord\\-secondaryObj1\\-assoc\\-ref: \"" + nodeRef.toString() + "\" OR "
                    + "lecm\\-busjournal\\:bjRecord\\-secondaryObj2\\-assoc\\-ref: \"" + nodeRef.toString() + "\" OR "
                    + "lecm\\-busjournal\\:bjRecord\\-secondaryObj3\\-assoc\\-ref: \"" + nodeRef.toString() + "\" OR "
                    + "lecm\\-busjournal\\:bjRecord\\-secondaryObj4\\-assoc\\-ref: \"" + nodeRef.toString() + "\" OR "
                    + "lecm\\-busjournal\\:bjRecord\\-secondaryObj5\\-assoc\\-ref: \"" + nodeRef.toString() + "\")";
        } else {
            query += " AND @lecm\\-busjournal\\:bjRecord\\-mainObject\\-assoc\\-ref: \"" + nodeRef.toString() + "\"";
        }
        sp.setQuery(query);
        try {
            results = searchService.query(sp);
            for (ResultSetRow row : results) {
                records.add(row.getNodeRef());
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }

        List<NodeRef> result = new ArrayList<NodeRef>();
        for (NodeRef record : records) {
            if (!showInactive && isArchive(record)) {
                continue;
            }
            result.add(record);
        }

        return result;
    }

    @Override
    public List<BusinessJournalRecord> getStatusHistory(NodeRef nodeRef, String sortColumnLocalName, final boolean sortAscending) {

        List<NodeRef> result = getHistory(nodeRef, false, false);
        List<NodeRef> resultStatus = new ArrayList<NodeRef>();

        final QName sortFieldQName = sortColumnLocalName != null && sortColumnLocalName.length() > 0 ? QName.createQName(BJ_NAMESPACE_URI, sortColumnLocalName) : PROP_BR_RECORD_DATE;
        if (sortFieldQName == null) {
            return pack(result);
        }

        List<NodeRef> eventStatus = new ArrayList<NodeRef>();
        // Получаем nodeRef события - Переход документа в новый статус
        eventStatus.add(getEventCategoryByCode("CHANGE_DOCUMENT_STATUS"));
        eventStatus.add(getEventCategoryByCode("ADD"));

        for (NodeRef status : eventStatus) {
            for (int i = 0; i < result.size(); i++) {
                String strNodeRef = (String) nodeService.getProperty(result.get(i), PROP_BR_RECORD_EVENT_CAT);
                NodeRef property = new NodeRef(strNodeRef);
                if (property != null) {
                    if (status.equals(property)) {
                        resultStatus.add(result.get(i));

                    }
                }
            }
        }
        result = resultStatus;

        class NodeRefComparator<T extends Serializable & Comparable<T>> implements Comparator<NodeRef> {

            @Override
            public int compare(NodeRef nodeRef1, NodeRef nodeRef2) {
                T obj1 = (T) nodeService.getProperty(nodeRef1, sortFieldQName);
                T obj2 = (T) nodeService.getProperty(nodeRef2, sortFieldQName);

                return sortAscending ? obj1.compareTo(obj2) : obj2.compareTo(obj1);
            }
        }

        if (sortFieldQName.getLocalName().equals(PROP_BR_RECORD_DATE.getLocalName())) {
            Collections.sort(result, new NodeRefComparator<Date>());
        }

        if (sortFieldQName.getLocalName().equals(PROP_BR_RECORD_DESC.getLocalName())) {
            Collections.sort(result, new NodeRefComparator<String>());
        }

        return pack(result);
    }

    @Override
    public List<BusinessJournalRecord> getLastRecords(int maxRecordsCount, boolean includeFromArchive) {
        List<NodeRef> records = new ArrayList<NodeRef>(100);
        ResultSet results = null;
        String query;
        SearchParameters sp = new SearchParameters();

        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        query = "TYPE:\"" + TYPE_BR_RECORD.toString() + "\"" + (!includeFromArchive ? " AND @lecm\\-dic\\:active: true " : "");
        sp.addSort("@" + PROP_BR_RECORD_DATE.toString(), false);
        sp.setQuery(query);
        sp.setMaxItems(maxRecordsCount);
        try {
            results = searchService.query(sp);
            for (ResultSetRow row : results) {
                records.add(row.getNodeRef());
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
        return pack(records);
    }

    @Override
    public List<BusinessJournalRecord> getRecords(BusinessJournalRecord.Field sortField, boolean ascending, int startIndex, int maxResults, Map<BusinessJournalRecord.Field, String> filter, boolean andFilter, boolean includeArchived) {
        List<NodeRef> records = new ArrayList<NodeRef>(100);
        ResultSet results = null;
        String query;
        SearchParameters sp = new SearchParameters();

        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
        query = "TYPE:\"" + TYPE_BR_RECORD.toString() + "\"" + (!includeArchived ? " AND @lecm\\-dic\\:active: true " : "");
        query += getFilter(filter, andFilter);
        if (sortField != null) {
            QName field = QName.createQName(sortField.getFieldName(), serviceRegistry.getNamespaceService());
            sp.addSort("@" + field.toString(), ascending);
        }
        sp.setQuery(query);
        sp.setSkipCount(startIndex);
        sp.setMaxItems(maxResults);
        try {
            results = searchService.query(sp);
            for (ResultSetRow row : results) {
                records.add(row.getNodeRef());
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
        return pack(records);
    }

    @Override
    public Long getRecordsCount(Map<BusinessJournalRecord.Field, String> filter, boolean andFilter, boolean includeArchived) {
        ResultSet results = null;
        SearchParameters sp = new SearchParameters();

        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
        String query = "TYPE:\"" + TYPE_BR_RECORD.toString() + "\"" + (!includeArchived ? " AND @lecm\\-dic\\:active: true " : "");
        query += getFilter(filter, andFilter);
        sp.setQuery(query);
        long result = 0;
        try {
            sp.setMaxItems(0);
            results = searchService.query(sp);
            if (results instanceof SolrJSONResultSet) {
                result = ((SolrJSONResultSet) results).getNumberFound();
            } else {
                //если вдруг в бин был подложен другой SearchComponent - выполнил запрос без ограничений
                sp.setMaxItems(-1);
                result = (long) searchService.query(sp).length();
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
        return result;
    }

    @Override
    public BusinessJournalRecord getNodeById(final Long nodeId) {
        NodeRef nodeRef = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
            @Override
            public NodeRef doWork() throws Exception {
                return nodeService.getNodeRef(nodeId);
            }
        });
        return nodeRef == null ? null : pack(nodeRef);
    }

    @Override
    public List<BusinessJournalRecord> getRecordsAfter(Long lastRecordId) {
        ArrayList<NodeRef> result = new ArrayList<NodeRef>();
        QName subscribeAspect = QName.createQName("http://www.it.ru/lecm/subscriptions/1.0", "subscribedAspect");
        if (serviceRegistry.getDictionaryService().getAspect(subscribeAspect) != null) {
            NodeRef businessJournalRoot = getBusinessJournalDirectory();
            String path = nodeService.getPath(businessJournalRoot).toPrefixString(serviceRegistry.getNamespaceService());
            String type = BusinessJournalService.TYPE_BR_RECORD.toString();
            String id = ContentModel.PROP_NODE_DBID.toPrefixString(serviceRegistry.getNamespaceService());

            SearchParameters parameters = new SearchParameters();
            parameters.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
            parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            parameters.setQuery(" +PATH:\"" + path + "//*\" AND TYPE:\"" + type + "\" AND @" + id.replace(":", "\\:").replace("-", "\\-") + ":[" + (lastRecordId + 1) + " TO MAX]");
            parameters.addSort("@" + ContentModel.PROP_NODE_DBID.toString(), true);
            ResultSet resultSet = null;
            try {
                resultSet = searchService.query(parameters);
                for (ResultSetRow row : resultSet) {
                    NodeRef node = row.getNodeRef();
                    result.add(node);
                }
            } catch (LuceneQueryParserException e) {
            } catch (Exception e1) {
                logger.error("Error while getting business journal's records without sending notification for subscribe", e1);
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
        }
        return pack(result);
    }

    private String getFilter(Map<BusinessJournalRecord.Field, String> filter, boolean andFilter) {
        String result = "";
        String operator = andFilter ? " AND " : " OR ";
        boolean first = true;
        for (BusinessJournalRecord.Field key : filter.keySet()) {
            String value = filter.get(key);
            if (!value.equals("")) {
                String normalizeKey = key.getFieldName().replace("-", "\\-").replace(":", "\\:");
                if (key == BusinessJournalRecord.Field.DATE) {
                    String[] dates = value.split("\\|");
                    String start = "MIN";
                    String end = "NOW";
                    if (!"".equals(dates[0])) {
                        start = dates[0];
                    }
                    if (dates.length > 1 && !"".equals(dates[1])) {
                        end = dates[1];
                    }
                    result += (!first ? operator : "") + normalizeKey + ": [\"" + start + "\" TO \"" + end + "\"]";
                } else {
                    result += (!first ? operator : "") + normalizeKey + ":\"*" + value + "*\"";
                    first = false;
                }
            }
        }
        if (!result.equals("")) {
            result = "AND (" + result + ")";
        }
        return result;
    }

    private List<BusinessJournalRecord> pack(List<NodeRef> refs) {
        ArrayList<BusinessJournalRecord> records = new ArrayList<BusinessJournalRecord>();
        for (NodeRef ref : refs) {
            BusinessJournalRecord record = pack(ref);
            if (record != null) {
                records.add(pack(ref));
            }
        }
        return records;
    }

    private BusinessJournalRecord pack(NodeRef ref) {

        try {
            Long nodeId = (Long) nodeService.getProperty(ref, ContentModel.PROP_NODE_DBID);

            Date date = (Date) nodeService.getProperty(ref, BusinessJournalService.PROP_BR_RECORD_DATE);

            String filledDescription = "";
            Object filledDescriptionObj = nodeService.getProperty(ref, BusinessJournalService.PROP_BR_RECORD_DESC);
            if (filledDescriptionObj != null) {
                filledDescription = (String) filledDescriptionObj;
            }

            String mainObjectDescription = "";
            Object mainObjectDescriptionObj = nodeService.getProperty(ref, BusinessJournalService.PROP_BR_RECORD_MAIN_OBJECT);
            if (mainObjectDescriptionObj != null) {
                mainObjectDescription = (String) mainObjectDescriptionObj;
            }
            Object initiatorObj = nodeService.getProperty(ref, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-initiator-assoc-ref"));
            NodeRef initiator = null;
            if (initiatorObj != null) {
                initiator = new NodeRef(initiatorObj.toString());
            }

            String initiatorText = (String) nodeService.getProperty(ref, PROP_BR_RECORD_INITIATOR);

            String categoryRef = (String) nodeService.getProperty(ref, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-evCategory-assoc-ref"));
            NodeRef category = null;
            String categoryText = "";
            if (categoryRef != null && NodeRef.isNodeRef(categoryRef)) {
                category = new NodeRef(categoryRef);
                categoryText = nodeService.getProperty(category, ContentModel.PROP_NAME).toString();
            }

            String mainObjectRef = (String) nodeService.getProperty(ref, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-mainObject-assoc-ref"));
            NodeRef mainObject = null;
            if (mainObjectRef != null && !"".equals(mainObjectRef) && NodeRef.isNodeRef(mainObjectRef)) {
                mainObject = new NodeRef(mainObjectRef);
            }

            List<AssociationRef> types = nodeService.getTargetAssocs(ref, ASSOC_BR_RECORD_OBJ_TYPE);
            NodeRef objType = null;
            String typeText = "";
            if (types.size() > 0) {
                objType = types.get(0).getTargetRef();
                typeText = nodeService.getProperty(objType, ContentModel.PROP_NAME).toString();
            }

            ArrayList<RecordObject> objects = new ArrayList<RecordObject>();
            for (int i = 1; i <= MAX_SECONDARY_OBJECTS_COUNT; i++) {
                Object value = nodeService.getProperty(ref, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-secondaryObj" + i));
                if (value != null) {
                    objects.add(new RecordObject(null, value.toString()));
                }
            }
            Boolean isActive = (Boolean) nodeService.getProperty(ref, IS_ACTIVE);
            BusinessJournalRecord record = new BusinessJournalRecord(nodeId, date, initiator, mainObject, objType, mainObjectDescription, filledDescription, category, objects, isActive);
            record.setInitiatorText(initiatorText);
            record.setEventCategoryText(categoryText);
            record.setObjectTypeText(typeText);
            return record;
        } catch (Exception e) {
            logger.error("Error while BJ record with NodeRef=" + ref + " initializing", e);
        }
        return null;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }
}
