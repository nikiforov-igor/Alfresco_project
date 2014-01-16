package ru.it.lecm.businessjournal.beans;

import org.alfresco.service.cmr.repository.NodeRef;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 02.12.13
 * Time: 16:11
 */
public class BusinessJournalRecord implements Serializable {

    public enum Field {
        DATE("lecm-busjournal:bjRecord-date"),
        OBJECT_TYPE("lecm-busjournal:bjRecord-objType-assoc"),
        EVENT_CATEGORY("lecm-busjournal:bjRecord-evCategory-assoc"),
        MAIN_OBJECT("lecm-busjournal:bjRecord-mainObject-assoc"),
        INITIATOR("lecm-busjournal:bjRecord-initiator-assoc"),
        INITIATOR_TEXT("lecm-busjournal:bjRecord-initiator-assoc-text-content"),
        MAIN_OBJECT_TEXT("lecm-busjournal:bjRecord-mainObject-assoc-text-content"),
        OBJECT_TYPE_TEXT("lecm-busjournal:bjRecord-objType-assoc-text-content"),
        EVENT_CATEGORY_TEXT("lecm-busjournal:bjRecord-evCategory-assoc-text-content"),
        OBJECT_1_TEXT("lecm-busjournal:bjRecord-secondaryObj1-assoc-text-content"),
        OBJECT_2_TEXT("lecm-busjournal:bjRecord-secondaryObj2-assoc-text-content"),
        OBJECT_3_TEXT("lecm-busjournal:bjRecord-secondaryObj3-assoc-text-content"),
        OBJECT_4_TEXT("lecm-busjournal:bjRecord-secondaryObj4-assoc-text-content"),
        OBJECT_5_TEXT("lecm-busjournal:bjRecord-secondaryObj5-assoc-text-content"),
        RECORD_DESCRIPTION("lecm-busjournal:bjRecord-description");

        private String fieldName;

        Field(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public static Field fromFieldName(String fieldName) {
            if (fieldName != null) {
                for (Field field : Field.values()) {
                    if (fieldName.equalsIgnoreCase(field.fieldName)) {
                        return field;
                    }
                }
            }
            return null;
        }
    }

    private Long nodeId;
    private Date date;
    private NodeRef initiator;
    private String initiatorText = "";
    private NodeRef mainObject;
    private String mainObjectDescription = "";
    private NodeRef objectType;
    private String objectTypeText = "неизвестно";
    private NodeRef eventCategory;
    private String eventCategoryText = "неизвестно";
    private List<RecordObject> objects;
    private String recordDescription = "";
    private boolean isActive = true;

    public BusinessJournalRecord(Date date, NodeRef initiator, NodeRef mainObject, NodeRef objectType, String mainObjectDescription, String recordDescription, NodeRef eventCategory, List<RecordObject> objects, boolean isActive) {
        this.date = date;
        this.initiator = initiator;
        this.mainObject = mainObject;
        this.objectType = objectType;
        this.eventCategory = eventCategory;
        this.objects = objects;
        this.mainObjectDescription = mainObjectDescription;
        this.recordDescription = recordDescription;
        this.isActive = isActive;
    }

    public BusinessJournalRecord(Long nodeId, Date date, NodeRef initiator, NodeRef mainObject, NodeRef objectType, String mainObjectDescription, String recordDescription, NodeRef eventCategory, List<RecordObject> objects, boolean isActive) {
        this.nodeId = nodeId;
        this.date = date;
        this.initiator = initiator;
        this.mainObject = mainObject;
        this.objectType = objectType;
        this.eventCategory = eventCategory;
        this.objects = objects;
        this.mainObjectDescription = mainObjectDescription;
        this.recordDescription = recordDescription;
        this.isActive = isActive;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public boolean isActive() {
        return isActive;
    }

    public Date getDate() {
        return date;
    }

    public NodeRef getInitiator() {
        return initiator;
    }

    public NodeRef getMainObject() {
        return mainObject;
    }

    public NodeRef getObjectType() {
        return objectType;
    }

    public NodeRef getEventCategory() {
        return eventCategory;
    }

    public List<RecordObject> getObjects() {
        return objects;
    }

    public String getMainObjectDescription() {
        return mainObjectDescription;
    }

    public String getRecordDescription() {
        return recordDescription;
    }

    public String getObject1() {
        return objects != null && objects.size() > 0 ? objects.get(0).getDescription() : "";
    }

    public String getObject2() {
        return objects != null && objects.size() > 1 ? objects.get(1).getDescription() : "";
    }

    public String getObject3() {
        return objects != null && objects.size() > 2 ? objects.get(2).getDescription() : "";
    }

    public String getObject4() {
        return objects != null && objects.size() > 3 ? objects.get(3).getDescription() : "";
    }

    public String getObject5() {
        return objects != null && objects.size() > 4 ? objects.get(4).getDescription() : "";
    }

    public NodeRef getObject1Id() {
        return objects != null && objects.size() > 0 ? objects.get(0).getNodeRef() : null;
    }

    public NodeRef getObject2Id() {
        return objects != null && objects.size() > 1 ? objects.get(1).getNodeRef() : null;
    }

    public NodeRef getObject3Id() {
        return objects != null && objects.size() > 2 ? objects.get(2).getNodeRef() : null;
    }

    public NodeRef getObject4Id() {
        return objects != null && objects.size() > 3 ? objects.get(3).getNodeRef() : null;
    }

    public NodeRef getObject5Id() {
        return objects != null && objects.size() > 4 ? objects.get(4).getNodeRef() : null;
    }

    public String getObjectTypeText() {
        return objectTypeText;
    }

    public void setObjectTypeText(String objectTypeText) {
        this.objectTypeText = objectTypeText;
    }

    public String getEventCategoryText() {
        return eventCategoryText;
    }

    public void setEventCategoryText(String eventCategoryText) {
        this.eventCategoryText = eventCategoryText;
    }

    public String getInitiatorText() {
        return initiatorText;
    }

    public void setInitiatorText(String initiatorText) {
        this.initiatorText = initiatorText;
    }

}