jsonString = jsonUtils.toJSONString(json);
logger.log("*****************");
logger.log(jsonString);
logger.log("*****************");
var specialShedule = shedule.createNewSpecialShedule(json);

if (specialShedule != null) {
	model.nodeRef = specialShedule.nodeRef.toString();
}
