var container = delegation.getDelegationOptsContainer ();
var itemType = delegation.getItemType ();
var isEngineer = delegation.isEngineer (orgstructure.getCurrentEmployee ().nodeRef.toString ());
var isBoss = delegation.isBoss (orgstructure.getCurrentEmployee ().nodeRef.toString ());

model.nodeRef = container.nodeRef.toString ();
logger.log ("model.nodeRef = " + model.nodeRef);
model.itemType = itemType;
logger.log ("model.itemType = " + model.itemType);
model.isEngineer = isEngineer;
logger.log ("model.isEngineer = " + model.isEngineer);
model.isBoss = isBoss;
logger.log ("model.isBoss = " + model.isBoss);
