var nodeId = args["recordId"];

var record = businessJournal.getNodeById(parseInt(nodeId));

var result = {
    date: record.getFormatDate(),
    description: record.getRecordDescription(),
    category: record.getEventCategoryText(),
    type: record.getObjectTypeText(),
    typeRef: record.getObjectType() != null ? record.getObjectType().nodeRef : null,
    initiator: record.getInitiatorText(),
    initiatorRef: record.getInitiator()!= null ? record.getInitiator().nodeRef : null,
    mainObject: record.getMainObjectDescription(),
    mainObjectRef: record.getMainObject() != null ? record.getMainObject().nodeRef : null,
    object1: record.getObject1(),
    object2: record.getObject2(),
    object3: record.getObject3(),
    object4: record.getObject4(),
    object5: record.getObject5()
};

model.result = jsonUtils.toJSONString(result);