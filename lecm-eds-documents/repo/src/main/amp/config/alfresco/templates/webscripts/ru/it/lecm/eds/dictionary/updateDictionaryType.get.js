var dicName = args["dicName"];
var newDicType = args["newDicType"];

model.updatedRecordsCount = updateDictionary(dicName, newDicType);

function updateDictionary(dicName, newDicType) {
    var updatedRecordsCount = 0;
    if (dicName && newDicType) {
        var dicToUpdate = dictionary.getDictionaryByName(dicName);
        if (dicToUpdate) {
            if (dicToUpdate.properties["lecm-dic:type"] != newDicType) {
                dicToUpdate.properties["lecm-dic:type"] = newDicType;
                dicToUpdate.save();
            }
            var children = dictionary.getChildren(dicToUpdate.nodeRef.toString());
            if (children) {
                children.forEach(function (record) {
                    if (record.typeShort != newDicType) {
                        if (record.specializeType(newDicType)) {
                            updatedRecordsCount++;
                        }
                    }
                });
            }
        }
    }
    return updatedRecordsCount;
}

