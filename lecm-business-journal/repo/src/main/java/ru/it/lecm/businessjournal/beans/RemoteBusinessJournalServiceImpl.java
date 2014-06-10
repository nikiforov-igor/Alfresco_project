package ru.it.lecm.businessjournal.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.remote.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

/**
 * User: pmelnikov
 * Date: 09.12.13
 * Time: 14:45
 */
public class RemoteBusinessJournalServiceImpl extends AbstractBusinessJournalService implements BusinessJournalService {

    private BusinessJournalStore remoteService;

    private Logger logger = LoggerFactory.getLogger(RemoteBusinessJournalServiceImpl.class);

    public void init() {
		bjRootID = BJ_ROOT_ID;
		bjArchiveID = BJ_ARCHIVE_ROOT_ID;
    }

    @Override
    public void saveToStore(BusinessJournalRecord record) throws Exception {
        remoteService.save(pack(record));
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    @Override
    public List<BusinessJournalRecord> getRecordsByInterval(Date begin, Date end) {
        List<BusinessJournalRecord> result = new ArrayList<BusinessJournalRecord>();
        try {
            GregorianCalendar beginCalendar = new GregorianCalendar();
            if (begin == null) {
                beginCalendar.setTime(new Date(0));
            } else {
                beginCalendar.setTime(begin);
            }
            XMLGregorianCalendar beginDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(beginCalendar);

            GregorianCalendar endCalendar = new GregorianCalendar();
            if (end == null) {
                endCalendar.setTime(new Date());
            } else {
                endCalendar.setTime(end);
            }
            XMLGregorianCalendar endDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(endCalendar);
            result = unpack(remoteService.getRecordsByInterval(beginDate, endDate));
        } catch (Exception e) {
            logger.error("Cannot execute getRecordsByInterval because: ", e);
        }
        return result;
    }

    @Override
    public List<BusinessJournalRecord> getRecordsByParams(String objectTypeRefs, Date begin, Date end, String whoseKey, Boolean checkMainObject) {
        return getRecordsByParams(objectTypeRefs, begin, end, whoseKey, checkMainObject, null, null);
    }

    @Override
    public List<BusinessJournalRecord> getRecordsByParams(String objectTypeRefs, Date begin, Date end, String whoseKey, Boolean checkMainObject, Integer skipCount, Integer maxItems) {
        List<BusinessJournalRecord> result = new ArrayList<BusinessJournalRecord>();
        try {
            List<Long> objectTypes = new ArrayList<Long>();
            StringTokenizer st = new StringTokenizer(objectTypeRefs, ",");
            while (st.hasMoreTokens()) {
                String nodeRefStr = st.nextToken().trim();
                if (NodeRef.isNodeRef(nodeRefStr) && nodeService.exists(new NodeRef(nodeRefStr))) {
                    objectTypes.add((Long) nodeService.getProperty(new NodeRef(nodeRefStr), ContentModel.PROP_NODE_DBID));
                }
            }

            List<Long> initiators = new ArrayList<Long>();
            st = new StringTokenizer(whoseKey, ",");
            while (st.hasMoreTokens()) {
                String nodeRefStr = st.nextToken().trim();
                if (NodeRef.isNodeRef(nodeRefStr) && nodeService.exists(new NodeRef(nodeRefStr))) {
                    initiators.add((Long) nodeService.getProperty(new NodeRef(nodeRefStr), ContentModel.PROP_NODE_DBID));
                }
            }

            GregorianCalendar beginCalendar = new GregorianCalendar();
            if (begin == null) {
                beginCalendar.setTime(new Date(0));
            } else {
                beginCalendar.setTime(begin);
            }
            XMLGregorianCalendar beginDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(beginCalendar);

            GregorianCalendar endCalendar = new GregorianCalendar();
            if (end == null) {
                endCalendar.setTime(new Date());
            } else {
                endCalendar.setTime(end);
            }
            XMLGregorianCalendar endDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(endCalendar);

            List<Long> employees = new ArrayList<Long>();
            if (whoseKey != null && !"".equals(whoseKey)) {
                switch (WhoseEnum.valueOf(whoseKey.toUpperCase())) {
                    case MY: {
                        NodeRef employee = orgstructureService.getCurrentEmployee();
                        employees.add((Long) nodeService.getProperty(employee, ContentModel.PROP_NODE_DBID));
                        break;
                    }
                    case DEPARTMENT: {
                        NodeRef boss = orgstructureService.getCurrentEmployee();

                        if (boss != null) {
                            for (NodeRef employee : orgstructureService.getBossSubordinate(boss)) {
                                employees.add((Long) nodeService.getProperty(employee, ContentModel.PROP_NODE_DBID));
                            }
                            employees.add((Long) nodeService.getProperty(boss, ContentModel.PROP_NODE_DBID));                        }
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

            //Добавить обработку whoseKey и фильтрацию по доступности объекта checkMainObject
            result = unpack(remoteService.getRecordsByParams(objectTypes, beginDate, endDate, employees, skipCount, maxItems));

            if (checkMainObject != null && checkMainObject) {
                List<BusinessJournalRecord> filteredResult = new ArrayList<BusinessJournalRecord>();
                // проверить доступность основного объекта
                for (BusinessJournalRecord record : result) {
					NodeRef mainObject = record.getMainObject();
					if (nodeService.exists(mainObject) && lecmPermissionService.hasReadAccess(mainObject)
							&& (!stateMachineService.isDraft(mainObject) || isOwnNode(mainObject))) {
                        filteredResult.add(record);
                    }
                }
                result = filteredResult;
            }

        } catch (Exception e) {
            logger.error("Cannot execute getRecordsByParams because: ", e);
        }
        return result;
    }

    @Override
    public boolean moveRecordToArchive(Long recordId) {
        boolean result = false;
        try {
            result = remoteService.moveRecordToArchive(recordId);
        } catch (Exception e) {
            logger.error("Cannot execute getRecordsByParams because: ", e);
        }
        return result;
    }

    @Override
    public BusinessJournalRecord getNodeById(Long nodeId) {
        BusinessJournalRecord result = null;
        try {
            result = unpack(remoteService.getNodeById(nodeId));
        } catch (Exception e) {
            logger.error("Cannot execute getRecordsByParams because: ", e);
        }
        return result;
    }

    @Override
    public List<BusinessJournalRecord> getStatusHistory(NodeRef nodeRef, String sortColumnLocalName, boolean sortAscending) {
        List<BusinessJournalRecord> result = new ArrayList<BusinessJournalRecord>();
        try {
            Long id = (Long) nodeService.getProperty(nodeRef, ContentModel.PROP_NODE_DBID);
            List<Long> categories = new ArrayList<Long>();
            NodeRef changeCategory = getEventCategoryByCode("CHANGE_DOCUMENT_STATUS");
            if (changeCategory != null) {
                categories.add((Long) nodeService.getProperty(changeCategory, ContentModel.PROP_NODE_DBID));
            }
            NodeRef addCategory = getEventCategoryByCode("ADD");
            if (addCategory != null) {
                categories.add((Long) nodeService.getProperty(addCategory, ContentModel.PROP_NODE_DBID));
            }

            Field sortField = Field.valueOf(BusinessJournalRecord.Field.fromFieldName(sortColumnLocalName).name());
            result = unpack(remoteService.getHistoryByCategory(id, categories, sortField, sortAscending));
        } catch (Exception e) {
            logger.error("Cannot execute getStatusHistory because: ", e);
        }
        return result;
    }

    @Override
    public List<BusinessJournalRecord> getHistory(NodeRef nodeRef, String sortColumnLocalName, boolean sortAscending, boolean includeSecondary, boolean showInactive) {
        List<BusinessJournalRecord> result = new ArrayList<BusinessJournalRecord>();
        try {
            Long id = (Long) nodeService.getProperty(nodeRef, ContentModel.PROP_NODE_DBID);
            Field sortField = null;
            try {
                sortField = Field.valueOf(BusinessJournalRecord.Field.fromFieldName(sortColumnLocalName).name());
            } catch (Exception e) {
                sortField = Field.DATE;
                sortAscending = false;
            }
            result = unpack(remoteService.getHistory(id, sortField, sortAscending, includeSecondary, showInactive));
        } catch (Exception e) {
            logger.error("Cannot execute getHistory because: ", e);
        }
        return result;
    }

    @Override
    public List<BusinessJournalRecord> getLastRecords(int maxRecordsCount, boolean includeFromArchive) {
        List<BusinessJournalRecord> result = new ArrayList<BusinessJournalRecord>();
        try {
            result = unpack(remoteService.getLastRecords(maxRecordsCount, includeFromArchive));
        } catch (Exception e) {
            logger.error("Cannot execute getLastRecords because: ", e);
        }
        return result;
    }

    @Override
    public List<BusinessJournalRecord> getRecords(BusinessJournalRecord.Field sortField, boolean ascending, int startIndex, int maxResults, Map<BusinessJournalRecord.Field, String> filter, boolean andFilter, boolean includeArchived) {
        List<BusinessJournalRecord> result = new ArrayList<BusinessJournalRecord>();
        try {
            Field storeSortField = Field.valueOf(sortField.name());
            ObjectFactory factory = new ObjectFactory();
            GetRecords.Filter storeFilter = factory.createGetRecordsFilter();
            for (BusinessJournalRecord.Field field : filter.keySet()) {
                if (!"".equals(filter.get(field))) {
                    GetRecords.Filter.Entry entry = factory.createGetRecordsFilterEntry();
                    entry.setKey(Field.valueOf(field.name()));
                    entry.setValue(filter.get(field));
                    storeFilter.getEntry().add(entry);
                }
            }
            result = unpack(remoteService.getRecords(storeSortField, ascending, startIndex, maxResults, storeFilter, andFilter, includeArchived));
        } catch (Exception e) {
            logger.error("Cannot execute getRecords because: ", e);
        }
        return result;
    }

    @Override
    public Long getRecordsCount(Map<BusinessJournalRecord.Field, String> filter, boolean andFilter, boolean includeArchived) {
        Long result = 0L;
        try {
            ObjectFactory factory = new ObjectFactory();
            GetRecordsCount.Filter storeFilter = factory.createGetRecordsCountFilter();
            for (BusinessJournalRecord.Field field : filter.keySet()) {
                if (!"".equals(filter.get(field))) {
                    GetRecordsCount.Filter.Entry entry = factory.createGetRecordsCountFilterEntry();
                    entry.setKey(Field.valueOf(field.name()));
                    entry.setValue(filter.get(field));
                    storeFilter.getEntry().add(entry);
                }
            }
            result = remoteService.getRecordsCount(storeFilter, andFilter, includeArchived);
        } catch (Exception e) {
            logger.error("Cannot execute getRecordsCount because: ", e);
        }
        return result;
    }

    @Override
    public List<BusinessJournalRecord> getRecordsAfter(Long lastRecordId) {
        List<BusinessJournalRecord> result = new ArrayList<BusinessJournalRecord>();
        try {
            result = unpack(remoteService.getRecordsAfter(lastRecordId));
        } catch (Exception e) {
            logger.error("Cannot execute getRecords because: ", e);
        }
        return result;
    }

    public void setRemoteService(BusinessJournalStore remoteService) {
        this.remoteService = remoteService;
    }

    private BusinessJournalStoreRecord pack(BusinessJournalRecord record) throws Exception {
        BusinessJournalStoreRecord storeRecord = new BusinessJournalStoreRecord();

        storeRecord.setActive(true);

        GregorianCalendar c = new GregorianCalendar();
        c.setTime(record.getDate());
        XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

        storeRecord.setDate(date);

        Long eventCategoryId = null;
        String eventCategoryStringId = null;
        String eventCategoryText = null;
        if (record.getEventCategory() != null) {
            eventCategoryId = (Long) nodeService.getProperty(record.getEventCategory(), ContentModel.PROP_NODE_DBID);
            eventCategoryStringId = record.getEventCategory().toString();
            eventCategoryText = record.getEventCategoryText();
        }
        storeRecord.setEventCategoryId(eventCategoryId);
        storeRecord.setEventCategoryStringId(eventCategoryStringId);
        storeRecord.setEventCategoryText(eventCategoryText);

        Long initiatorId = null;
        String initiatorStringId = null;
        String initiatorText = null;
        if (record.getInitiator() != null) {
            initiatorId = (Long) nodeService.getProperty(record.getInitiator(), ContentModel.PROP_NODE_DBID);
            initiatorStringId = record.getInitiator().toString();
            initiatorText = record.getInitiatorText();
        }
        storeRecord.setInitiatorId(initiatorId);
        storeRecord.setInitiatorStringId(initiatorStringId);
        storeRecord.setInitiatorText(initiatorText);

        String mainObjectDescription = record.getMainObjectDescription();

        Long mainObjectId = 0L;
        if (record.getMainObject() != null && nodeService.exists(record.getMainObject())) {
            mainObjectId = (Long) nodeService.getProperty(record.getMainObject(), ContentModel.PROP_NODE_DBID);
        }
        String mainObjectStringId = record.getMainObject().toString();
        storeRecord.setMainObjectId(mainObjectId);
        storeRecord.setMainObjectStringId(mainObjectStringId);
        storeRecord.setMainObjectDescription(mainObjectDescription);

        Long objectTypeId = null;
        String objectTypeStringId = null;
        String objectTypeText = null;
        if (record.getObjectType() != null) {
            objectTypeId = (Long) nodeService.getProperty(record.getObjectType(), ContentModel.PROP_NODE_DBID);
            objectTypeStringId = record.getObjectType().toString();
            objectTypeText = record.getObjectTypeText();
        }
        storeRecord.setObjectTypeId(objectTypeId);
        storeRecord.setObjectTypeStringId(objectTypeStringId);
        storeRecord.setObjectTypeText(objectTypeText);

        String recordDescription = record.getRecordDescription();
        storeRecord.setRecordDescription(recordDescription);

        Long obj1LongId = record.getObject1Id() != null ? (Long) nodeService.getProperty(record.getObject1Id(), ContentModel.PROP_NODE_DBID) : null;
        String obj1StringValue = record.getObject1();
        storeRecord.setObj1LongId(obj1LongId);
        storeRecord.setObj1StringValue(obj1StringValue);

        Long obj2LongId = record.getObject2Id() != null ? (Long) nodeService.getProperty(record.getObject2Id(), ContentModel.PROP_NODE_DBID) : null;
        String obj2StringValue = record.getObject2();
        storeRecord.setObj2LongId(obj2LongId);
        storeRecord.setObj2StringValue(obj2StringValue);

        Long obj3LongId = record.getObject3Id() != null ? (Long) nodeService.getProperty(record.getObject3Id(), ContentModel.PROP_NODE_DBID) : null;
        String obj3StringValue = record.getObject3();
        storeRecord.setObj3LongId(obj3LongId);
        storeRecord.setObj3StringValue(obj3StringValue);

        Long obj4LongId = record.getObject4Id() != null ? (Long) nodeService.getProperty(record.getObject4Id(), ContentModel.PROP_NODE_DBID) : null;
        String obj4StringValue = record.getObject4();
        storeRecord.setObj4LongId(obj4LongId);
        storeRecord.setObj4StringValue(obj4StringValue);

        Long obj5LongId = record.getObject5Id() != null ? (Long) nodeService.getProperty(record.getObject5Id(), ContentModel.PROP_NODE_DBID) : null;
        String obj5StringValue = record.getObject5();
        storeRecord.setObj5LongId(obj5LongId);
        storeRecord.setObj5StringValue(obj5StringValue);

        return storeRecord;
    }

    private List<BusinessJournalRecord> unpack(List<BusinessJournalStoreRecord> records) {
        List<BusinessJournalRecord> result = new ArrayList<BusinessJournalRecord>();
        for (BusinessJournalStoreRecord record : records) {
            result.add(unpack(record));
        }
        return result;
    }

    private BusinessJournalRecord unpack(BusinessJournalStoreRecord record) {
        Long nodeId = record.getNodeId();
        Date date = record.getDate().toGregorianCalendar().getTime();
        NodeRef initiator = null;
        String initiatorText = null;
        if (record.getInitiatorId() != null) {
            initiator = new NodeRef(record.getInitiatorStringId());
            initiatorText = record.getInitiatorText();
        }
        NodeRef mainObject = new NodeRef(record.getMainObjectStringId());
        String mainObjectDescription = record.getMainObjectDescription();

        NodeRef objectType = null;
        String objectTypeText = null;
        if (record.getObjectTypeId() != null) {
            objectType = new NodeRef(record.getObjectTypeStringId());
            objectTypeText = record.getObjectTypeText();
        }
        NodeRef eventCategory = null;
        String eventCategoryText = null;
        if (record.getEventCategoryId() != null) {
            eventCategory = new NodeRef(record.getEventCategoryStringId());
            eventCategoryText = record.getEventCategoryText();
        }

        String recordDescription = record.getRecordDescription();
        boolean isActive = record.isActive();
        List<RecordObject> objects = new ArrayList<RecordObject>();
        if (record.getObj1StringValue() != null) {
            objects.add(new RecordObject(null, record.getObj1StringValue()));
        }
        if (record.getObj2StringValue() != null) {
            objects.add(new RecordObject(null, record.getObj2StringValue()));
        }
        if (record.getObj3StringValue() != null) {
            objects.add(new RecordObject(null, record.getObj3StringValue()));
        }
        if (record.getObj4StringValue() != null) {
            objects.add(new RecordObject(null, record.getObj4StringValue()));
        }
        if (record.getObj5StringValue() != null) {
            objects.add(new RecordObject(null, record.getObj5StringValue()));
        }
        BusinessJournalRecord result = new BusinessJournalRecord(
                nodeId,
                date,
                initiator,
                mainObject,
                objectType,
                mainObjectDescription,
                recordDescription,
                eventCategory,
                objects,
                isActive
        );
        if (initiatorText != null) {
        result.setInitiatorText(initiatorText);
        }
        if (objectTypeText != null) {
        result.setObjectTypeText(objectTypeText);
        }
        if (eventCategoryText != null) {
        result.setEventCategoryText(eventCategoryText);
        }
        return result;
    }
}
