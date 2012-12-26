logger.log (jsonUtils.toJSONString (json));
logger.log ("item_kind = " + url.templateArgs.item_kind);
logger.log ("item_id = " + url.templateArgs.item_id);

model.item_kind = url.templateArgs.item_kind;
model.item_id = url.templateArgs.item_id;
