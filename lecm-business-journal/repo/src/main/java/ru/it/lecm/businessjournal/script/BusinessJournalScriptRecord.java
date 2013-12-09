package ru.it.lecm.businessjournal.script;

import org.alfresco.repo.jscript.ScriptNode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 03.12.13
 * Time: 9:49
 */
public class BusinessJournalScriptRecord {

    private Long nodeId;
    private Date date;
    private ScriptNode initiator;
    private String initiatorText;
    private ScriptNode mainObject;
    private ScriptNode objectType;
    private String objectTypeText;
    private ScriptNode eventCategory;
    private String eventCategoryText;
    private List<String> objects;
    private String mainObjectDescription;
    private String recordDescription;
    private boolean isActive;

    private static SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss dd.MM.yyyy");

    public BusinessJournalScriptRecord(Long nodeId, Date date, ScriptNode initiator, ScriptNode mainObject, ScriptNode objectType, String mainObjectDescription, String recordDescription, ScriptNode eventCategory, List<String> objects, boolean isActive) {
        this.nodeId = nodeId;
        this.date = date;
        this.initiator = initiator;
        this.mainObject = mainObject;
        this.objectType = objectType;
        this.eventCategory = eventCategory;
        this.mainObjectDescription = mainObjectDescription;
        this.recordDescription = recordDescription;
        this.objects = objects;
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

    public String getFormatDate() {
        return format.format(date);
    }

    public ScriptNode getInitiator() {
        return initiator;
    }

    public ScriptNode getMainObject() {
        return mainObject;
    }

    public ScriptNode getObjectType() {
        return objectType;
    }

    public ScriptNode getEventCategory() {
        return eventCategory;
    }

    public List<String> getObjects() {
        return objects;
    }

    public String getMainObjectDescription() {
        return mainObjectDescription;
    }

    public String getRecordDescription() {
        return recordDescription;
    }

    public String getObject1() {
        return objects != null && objects.size() > 0 ? objects.get(0) : "";
    }

    public String getObject2() {
        return objects != null && objects.size() > 1 ? objects.get(1) : "";
    }

    public String getObject3() {
        return objects != null && objects.size() > 2 ? objects.get(2) : "";
    }

    public String getObject4() {
        return objects != null && objects.size() > 3 ? objects.get(3) : "";
    }

    public String getObject5() {
        return objects != null && objects.size() > 4 ? objects.get(4) : "";
    }

    public String getInitiatorText() {
        return initiatorText;
    }

    public void setInitiatorText(String initiatorText) {
        this.initiatorText = initiatorText;
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
}