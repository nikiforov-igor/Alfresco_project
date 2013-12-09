var nodeId = args["recordId"];

var record = businessJournal.getNodeById(parseInt(nodeId));

var result = {
    date: record.getFormatDate(),
    description: record.getRecordDescription(),
    category: record.getEventCategory().properties["cm:name"],
    type: record.getObjectType().properties["cm:name"],
    typeRef: record.getObjectType().nodeRef,
    initiator: record.getInitiatorText(),
    initiatorRef: record.getInitiator()!= null ? record.getInitiator().nodeRef : null,
    mainObject: record.getMainObjectDescription(),
    mainObjectRef: record.getMainObject().nodeRef,
    object1: record.getObject1(),
    object2: record.getObject2(),
    object3: record.getObject3(),
    object4: record.getObject4(),
    object5: record.getObject5()
};

model.result = jsonUtils.toJSONString(result);