logger.log (jsonUtils.toJSONString (json));
var protocol = url.templateArgs.protocol;
var identifier = url.templateArgs.identifier;
var id = url.templateArgs.id;
logger.log ("protocol = " + protocol);
logger.log ("identifier = " + identifier);
logger.log ("id = " + id);

var ref = protocol + "://" + identifier + "/" + id;
delegation.saveDelegationOpts (ref, json);

model.item_kind = "node";
model.item_id = ref;
