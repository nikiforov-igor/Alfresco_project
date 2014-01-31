var nodeRefs = json.get("items");
var turnOn = json.get("turnOn");
model.result = businessJournal.switchLogging(nodeRefs, turnOn).toString();



