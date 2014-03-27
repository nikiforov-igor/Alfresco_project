var nodeRef = json.get("nodeRef");
var substituteString = json.get("substituteString");

model.formatString = jsonUtils.toJSONString(substitude.formatNodeTitle(nodeRef, substituteString));