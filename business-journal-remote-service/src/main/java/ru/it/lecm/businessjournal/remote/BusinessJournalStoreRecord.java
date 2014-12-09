package ru.it.lecm.businessjournal.remote;

import java.io.Serializable;
import java.util.Date;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


/**
 * User: pmelnikov Date: 02.12.13 Time: 16:11
 */
//@PersistenceCapable(objectIdClass = ComposedKey.class)
@PersistenceCapable(identityType=IdentityType.DATASTORE)
@DatastoreIdentity(columns = {@Column(name = "dummy"), @Column(name = "date"), @Column(name = "nodeid")})
//@PrimaryKey(columns = {"key", "date", "isactive"});
public class BusinessJournalStoreRecord implements Serializable {

	public BusinessJournalStoreRecord() {
	}

	public enum Field {

		DATE,
		OBJECT_TYPE,
		EVENT_CATEGORY,
		MAIN_OBJECT,
		INITIATOR,
		INITIATOR_TEXT,
		MAIN_OBJECT_TEXT,
		OBJECT_TYPE_TEXT,
		EVENT_CATEGORY_TEXT,
		OBJECT_1_TEXT,
		OBJECT_2_TEXT,
		OBJECT_3_TEXT,
		OBJECT_4_TEXT,
		OBJECT_5_TEXT,
		RECORD_DESCRIPTION;
	}

	@Index
	private String initiator;
	private String initiatorText;
	@Index
	private String mainObject;
	private String mainObjectDescription;
	@Index
	private String objectType;
	private String objectTypeText;
	@Index
	private String eventCategory;
	private String eventCategoryText;
	@Column(length=10485760)
	private String recordDescription;
	@PrimaryKey
	private Date date;
	@Persistent(customValueStrategy="increment")
	@PrimaryKey
	@Index
	private Long nodeId;
	@Column(length=10485760)
	private String object1;
	private String object2;
	private String object3;
	private String object4;
	private String object5;
	@Index
	private String object1Id;
	private String object2Id;
	private String object3Id;
	private String object4Id;
	private String object5Id;
	@PrimaryKey
	@Index
	private Integer dummy = 0;

	public BusinessJournalStoreRecord(Date date,
			String initiatorStringId, String initiatorText,
			String mainObjectStringId, String mainObjectDescription,
			String objectTypeStringId, String objectTypeText,
			String eventCategoryStringId, String eventCategoryText,
			String recordDescription) {

		this.date = date;
		this.initiator = initiatorStringId;
		this.mainObject = mainObjectStringId;
		this.mainObjectDescription = mainObjectDescription;
		this.objectType = objectTypeStringId;
		this.eventCategory = eventCategoryStringId;
	}

	public BusinessJournalStoreRecord(BusinessJournalStoreRecord obj) {
		this.date = obj.getDate();
		this.eventCategory = obj.getEventCategory();
		this.initiator = obj.getInitiator();
		this.mainObject = obj.getMainObject();
		this.mainObjectDescription = obj.getMainObjectDescription();
		this.object1 = obj.getObject1();
		this.object2 = obj.getObject2();
		this.object3 = obj.getObject3();
		this.object4 = obj.getObject4();
		this.object5 = obj.getObject5();
		this.object1Id = obj.getObject1Id();
		this.object2Id = obj.getObject2Id();
		this.object3Id = obj.getObject3Id();
		this.object4Id = obj.getObject4Id();
		this.object5Id = obj.getObject5Id();
		this.objectType = obj.getObjectType();
		this.recordDescription = obj.getRecordDescription();
		this.nodeId = obj.getNodeId();
	}

	public Long getNodeId() {
		return nodeId;
	}

	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	public String getObject1() {
		return object1;
	}

	public void setObject1(String object1) {
		this.object1 = object1;
	}

	public String getObject2() {
		return object2;
	}

	public void setObject2(String object2) {
		this.object2 = object2;
	}

	public String getObject3() {
		return object3;
	}

	public void setObject3(String object3) {
		this.object3 = object3;
	}

	public String getObject4() {
		return object4;
	}

	public void setObject4(String object4) {
		this.object4 = object4;
	}

	public String getObject5() {
		return object5;
	}

	public void setObject5(String object5) {
		this.object5 = object5;
	}

	public String getObject1Id() {
		return object1Id;
	}

	public void setObject1Id(String object1Id) {
		this.object1Id = object1Id;
	}

	public String getObject2Id() {
		return object2Id;
	}

	public void setObject2Id(String object2Id) {
		this.object2Id = object2Id;
	}

	public String getObject3Id() {
		return object3Id;
	}

	public void setObject3Id(String object3Id) {
		this.object3Id = object3Id;
	}

	public String getObject4Id() {
		return object4Id;
	}

	public void setObject4Id(String object4Id) {
		this.object4Id = object4Id;
	}

	public String getObject5Id() {
		return object5Id;
	}

	public void setObject5Id(String object5Id) {
		this.object5Id = object5Id;
	}


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public String getMainObject() {
		return mainObject;
	}

	public void setMainObject(String mainObject) {
		this.mainObject = mainObject;
	}

	public String getMainObjectDescription() {
		return mainObjectDescription;
	}

	public void setMainObjectDescription(String mainObjectDescription) {
		this.mainObjectDescription = mainObjectDescription;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getEventCategory() {
		return eventCategory;
	}

	public void setEventCategory(String eventCategory) {
		this.eventCategory = eventCategory;
	}

	public String getRecordDescription() {
		return recordDescription;
	}

	public void setRecordDescription(String recordDescription) {
		this.recordDescription = recordDescription;
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
