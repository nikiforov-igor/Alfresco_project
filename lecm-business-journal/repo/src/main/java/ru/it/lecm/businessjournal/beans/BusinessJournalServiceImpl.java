package ru.it.lecm.businessjournal.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.repo.search.impl.lucene.SolrJSONResultSet;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.policies.BusinessJournalOnCreateAssocsPolicy;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author dbashmakov
 *         Date: 25.12.12
 *         Time: 10:18
 */
public class BusinessJournalServiceImpl extends AbstractBusinessJournalService implements BusinessJournalService {

    private static final String BUSINESS_JOURNAL_POST_TRANSACTION_PENDING_OBJECTS = "business_journal_post_transaction_pending_objects";

    private static final Logger logger = LoggerFactory.getLogger(BusinessJournalServiceImpl.class);

    private SearchService searchService;
    private NodeRef bjRootRef;
    private NodeRef bjArchiveRef;
    private LecmPermissionService lecmPermissionService;
    private StateMachineServiceBean stateMachineService;
    private BusinessJournalOnCreateAssocsPolicy businessJournalOnCreateAssocsPolicy;

    private ThreadPoolExecutor threadPoolExecutor;
    private TransactionListener transactionListener;

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setBusinessJournalOnCreateAssocsPolicy(BusinessJournalOnCreateAssocsPolicy businessJournalOnCreateAssocsPolicy) {
        this.businessJournalOnCreateAssocsPolicy = businessJournalOnCreateAssocsPolicy;
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return bjRootRef;
    }

    private static enum WhoseEnum {
        MY,
        DEPARTMENT,
        CONTROL,
        ALL
    }

    /**
     * Метод инициализвции сервиса
     * Создает рабочую директорию - если она еще не создана.
     * Записыывает в свойства сервиса nodeRef директории с бизнес-журналами
     */
    public void init() {
        businessJournalOnCreateAssocsPolicy.setBusinessJournalService(this);
        bjRootRef = getFolder(BJ_ROOT_ID);
        bjArchiveRef = getFolder(BJ_ARCHIVE_ROOT_ID);
        transactionListener = new BusinessJournalTransactionListener();
    }

    @Override
    public void log(BusinessJournalRecord record) {
        AlfrescoTransactionSupport.bindListener(this.transactionListener);
        List<BusinessJournalRecord> pendingActions = AlfrescoTransactionSupport.getResource(BusinessJournalServiceImpl.BUSINESS_JOURNAL_POST_TRANSACTION_PENDING_OBJECTS);
        if (pendingActions == null) {
            pendingActions = new ArrayList<BusinessJournalRecord>();
            AlfrescoTransactionSupport.bindResource(BusinessJournalServiceImpl.BUSINESS_JOURNAL_POST_TRANSACTION_PENDING_OBJECTS, pendingActions);
        }
        pendingActions.add(record);
    }

    private NodeRef createRecord(final Date date, final NodeRef initiator, final NodeRef mainObject, final String mainObjectDescription, final NodeRef eventCategory, final List<String> objects, final String description) {
        return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
            @Override
            public NodeRef execute() throws Throwable {
                final NodeRef objectType = getObjectType(mainObject);

                String type;
                if (objectType != null) {
                    type = (String) nodeService.getProperty(objectType, ContentModel.PROP_NAME);
                } else {
                    type = nodeService.getType(mainObject).getPrefixString().replace(":", "_");
                }

                String category;
                if (eventCategory != null) {
                    category = (String) nodeService.getProperty(eventCategory, ContentModel.PROP_NAME);
                } else {
                    category = "unknown";
                }

                final NodeRef saveDirectoryRef = getSaveFolder(type, category, date);

                // создаем ноду
                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(7);
                properties.put(PROP_BR_RECORD_DATE, date);
                properties.put(PROP_BR_RECORD_DESC, description);
                properties.put(PROP_BR_RECORD_INITIATOR, getInitiatorDescription(initiator));
                properties.put(PROP_BR_RECORD_MAIN_OBJECT, mainObjectDescription);
                if (objects != null && objects.size() > 0) {
                    for (int i = 0; i < objects.size() && i < MAX_SECONDARY_OBJECTS_COUNT; i++) {
                        String description = objects.get(i);
                        properties.put(QName.createQName(BJ_NAMESPACE_URI, getSecondObjPropName(i + 1)), description);
                    }
                }

                ChildAssociationRef associationRef = nodeService.createNode(saveDirectoryRef, ContentModel.ASSOC_CONTAINS,
                        QName.createQName(BJ_NAMESPACE_URI, GUID.generate()), TYPE_BR_RECORD, properties);
                NodeRef result = associationRef.getChildRef();

                // создаем ассоциации
                if (initiator != null) {
                    nodeService.createAssociation(result, initiator, ASSOC_BR_RECORD_INITIATOR);
                }
                if (mainObject != null) {
                    nodeService.createAssociation(result, mainObject, ASSOC_BR_RECORD_MAIN_OBJ);
                }

                // необязательные
                if (eventCategory != null) {
                    nodeService.createAssociation(result, eventCategory, ASSOC_BR_RECORD_EVENT_CAT);
                }
                if (objectType != null) {
                    nodeService.createAssociation(result, objectType, ASSOC_BR_RECORD_OBJ_TYPE);
                }

                if (objects != null && objects.size() > 0) {
                    for (int j = 0; j < objects.size() && j < MAX_SECONDARY_OBJECTS_COUNT; j++) {
                        if (NodeRef.isNodeRef(objects.get(j)) && nodeService.exists(new NodeRef(objects.get(j)))) {
                            nodeService.createAssociation(result, new NodeRef(objects.get(j)), QName.createQName(BJ_NAMESPACE_URI, getSeconObjAssocName(j + 1)));
                        }
                    }
                }
                return result;
            }
        });
    }

    @Override
    public NodeRef getBusinessJournalDirectory() {
        return bjRootRef;
    }

    @Override
    public NodeRef getBusinessJournalArchiveDirectory() {
        return bjArchiveRef;
    }

    private String getSeconObjAssocName(int j) {
        return "bjRecord-secondaryObj" + j + "-assoc";
    }

    private String getSecondObjPropName(int i) {
        return "bjRecord-secondaryObj" + i;
    }

    /**
     * Метод, возвращающий список ссылок на записи бизнес-журнала, сформированные за заданный период
     *
     * @param begin - начальная дата
     * @param end   - конечная дата
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

    private NodeRef getSaveFolder(final String type, final String category, final Date date) {
        return getFolder(getBusinessJournalDirectory(), type, category, date);
    }

    private NodeRef getArchiveFolder(final Date date, String type, String category) {
        return getFolder(getBusinessJournalArchiveDirectory(), type, category, date);
    }

    /**
     * Метод, возвращающий ссылку на директорию в директории "Бизнес Журнал" согласно заданным параметрам
     *
     * @param date     - текущая дата
     * @param type     - тип объекта
     * @param category - категория события
     * @param root     - корень, относительно которого строится путь
     * @return ссылка на директорию
     */
    private NodeRef getFolder(final NodeRef root, final String type, final String category, final Date date) {
        List<String> directoryPaths = new ArrayList<String>(3);
        if (type != null) {
            directoryPaths.add(type);
        }
        if (category != null) {
            directoryPaths.add(category);
        }
        directoryPaths.addAll(getDateFolderPath(date));
        return getFolder(root, directoryPaths);
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
        query = "PARENT: \"" + bjRootRef.toString() + "\" TYPE:\"" + TYPE_BR_RECORD.toString() + "\" AND @lecm\\-busjournal\\:bjRecord\\-date:[" + MIN + " TO " + MAX + "] AND @lecm\\-dic\\:active: true ";

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
                    List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(rowNodeRef, ASSOC_BR_RECORD_MAIN_OBJ);
                    if (targetAssocs != null) {
                        for (AssociationRef sourceAssoc : targetAssocs) {
                            NodeRef nodeRef = sourceAssoc.getTargetRef();

                            if (lecmPermissionService.hasReadAccess(nodeRef)
                                    && (!stateMachineService.isDraft(nodeRef) || isOwnNode(nodeRef))) {
                                records.add(rowNodeRef);
                            }
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
    public boolean moveRecordToArchive(final Long recordId) {
        if (!orgstructureService.isCurrentUserTheSystemUser() && !isBJEngineer()) {
            logger.warn("Current employee is not business journal engeneer");
            return false;
        }
        AuthenticationUtil.RunAsWork<Boolean> raw = new AuthenticationUtil.RunAsWork<Boolean>() {
            @Override
            public Boolean doWork() throws Exception {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {
                    @Override
                    public Boolean execute() throws Throwable {
                        final NodeRef record = serviceRegistry.getNodeService().getNodeRef(recordId);
                        if (!isArchive(record)) {
                            NodeRef objectType = findNodeByAssociationRef(record, ASSOC_BR_RECORD_OBJ_TYPE, null, ASSOCIATION_TYPE.TARGET);
                            String type = null;
                            if (objectType != null) {
                                type = (String) nodeService.getProperty(objectType, ContentModel.PROP_NAME);
                            } else {
                                NodeRef mainObject = findNodeByAssociationRef(record, ASSOC_BR_RECORD_MAIN_OBJ, null, ASSOCIATION_TYPE.TARGET);
                                if (mainObject != null) {
                                    type = nodeService.getType(mainObject).getPrefixString().replace(":", "_");
                                }
                            }
                            String category;
                            NodeRef eventCategory = findNodeByAssociationRef(record, ASSOC_BR_RECORD_EVENT_CAT, null, ASSOCIATION_TYPE.TARGET);
                            if (eventCategory != null) {
                                category = (String) nodeService.getProperty(eventCategory, ContentModel.PROP_NAME);
                            } else {
                                category = "unknown";
                            }
                            NodeRef archiveRef = getArchiveFolder(new Date(), (type != null ? type : "unknown"), category);
                            nodeService.setProperty(record, IS_ACTIVE, false); // помечаем как неактивная запись
                            ChildAssociationRef newRef = nodeService.moveNode(record, archiveRef, ContentModel.ASSOC_CONTAINS, nodeService.getPrimaryParent(record).getQName());
                            return newRef != null;
                        } else {
                            return true;
                        }
                    }
                });
            }
        };
        return AuthenticationUtil.runAsSystem(raw);
    }

    @Override
    public List<BusinessJournalRecord> getHistory(NodeRef nodeRef, String sortColumnLocalName, final boolean sortAscending, boolean showSecondary, boolean showInactive) {
        List<NodeRef> result = getHistory(nodeRef, showSecondary, showInactive);

        final QName sortFieldQName = sortColumnLocalName != null && sortColumnLocalName.length() > 0 ? QName.createQName(BJ_NAMESPACE_URI, sortColumnLocalName) : PROP_BR_RECORD_DATE;
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

        if (sortFieldQName.getLocalName().equals(PROP_BR_RECORD_DATE.getLocalName())) {
            Collections.sort(result, new NodeRefComparator<Date>());
        }

        if (sortFieldQName.getLocalName().equals(PROP_BR_RECORD_DESC.getLocalName())) {
            Collections.sort(result, new NodeRefComparator<String>());
        }

        return pack(result);
    }

    private List<NodeRef> getHistory(NodeRef nodeRef, boolean showSecondary, boolean showInactive) {
        if (nodeRef == null) {
            return new ArrayList<NodeRef>();
        }

        List<AssociationRef> sourceAssocs = nodeService.getSourceAssocs(nodeRef, ASSOC_BR_RECORD_MAIN_OBJ);

        List<NodeRef> result = new ArrayList<NodeRef>();
        int index = showSecondary ? MAX_SECONDARY_OBJECTS_COUNT : 0;

        for (int i = -1; i < index; i++) {
            if (i >= 0) {
                sourceAssocs = nodeService.getSourceAssocs(nodeRef, QName.createQName(BJ_NAMESPACE_URI, getSeconObjAssocName(i)));
            }
            for (AssociationRef sourceAssoc : sourceAssocs) {
                NodeRef bjRecordRef = sourceAssoc.getSourceRef();
                if (!showInactive && isArchive(bjRecordRef)) {
                    continue;
                }

                result.add(bjRecordRef);
            }
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


            NodeRef category = new NodeRef((String) nodeService.getProperty(ref, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-evCategory-assoc-ref")));

            NodeRef mainObject = new NodeRef((String) nodeService.getProperty(ref, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-mainObject-assoc-ref")));


            List<AssociationRef> types = nodeService.getTargetAssocs(ref, ASSOC_BR_RECORD_OBJ_TYPE);
            NodeRef objType = null;
            if (types.size() > 0) {
                objType = types.get(0).getTargetRef();
            }

            ArrayList<String> objects = new ArrayList<String>();
            for (int i = 1; i <= MAX_SECONDARY_OBJECTS_COUNT; i++) {
                Object value = nodeService.getProperty(ref, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-secondaryObj" + i));
                if (value != null) {
                    objects.add(value.toString());
                }
            }
            Boolean isActive = (Boolean) nodeService.getProperty(ref, IS_ACTIVE);
            BusinessJournalRecord record = new BusinessJournalRecord(nodeId, date, initiator, mainObject, objType, mainObjectDescription, filledDescription, category, objects, isActive);
            record.setInitiatorText(initiatorText);
            return record;
        } catch (Exception e) {
            logger.error("Error while BJ record with NodeRef=" + ref + " initializing", e);
        }
        return null;
    }

    private class BusinessJournalTransactionListener implements TransactionListener {

        @Override
        public void flush() {

        }

        @Override
        public void beforeCommit(boolean readOnly) {

        }

        @Override
        public void beforeCompletion() {

        }

        @Override
        public void afterCommit() {
            final List<BusinessJournalRecord> pendingRecords = AlfrescoTransactionSupport.getResource(BUSINESS_JOURNAL_POST_TRANSACTION_PENDING_OBJECTS);
            if (pendingRecords != null) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
                            @Override
                            public Void doWork() throws Exception {
                                return serviceRegistry.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                                    @Override
                                    public Void execute() throws Throwable {
                                        for (BusinessJournalRecord record : pendingRecords) {
                                            try {

                                                // создаем записи
                                                createRecord(record.getDate(), record.getInitiator(), record.getMainObject(), record.getMainObjectDescription(), record.getEventCategory(), record.getObjects(), record.getRecordDescription());
                                            } catch (Exception ex) {
                                                logger.error("Could not create business-journal record", ex);
                                            }
                                        }
                                        return null;
                                    }
                                }, false, true);
                            }
                        });
                    }
                };
                threadPoolExecutor.execute(runnable);
            }
        }

        @Override
        public void afterRollback() {

        }
    }

}
