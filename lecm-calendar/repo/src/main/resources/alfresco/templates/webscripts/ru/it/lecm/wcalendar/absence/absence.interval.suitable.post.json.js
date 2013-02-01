jsonString = jsonUtils.toJSONString(json);
logger.log("*****************");
logger.log(jsonString);
logger.log("*****************");
var isSuitable = absence.isIntervalSuitableForAbsence(json);
model.isSuitable = isSuitable;
