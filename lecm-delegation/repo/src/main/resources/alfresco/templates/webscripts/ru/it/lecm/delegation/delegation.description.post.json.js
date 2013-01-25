var page = url.templateArgs.page;
model.page = page;

function getDescriptionList () {
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
}

function getDescriptionOpts () {
	var isEngineer = delegation.isEngineer (orgstructure.getCurrentEmployee ().nodeRef.toString ());
	var isBoss = delegation.isBoss (orgstructure.getCurrentEmployee ().nodeRef.toString ());
	model.isEngineer = isEngineer;
	logger.log ("model.isEngineer = " + model.isEngineer);
	model.isBoss = isBoss;
	logger.log ("model.isBoss = " + model.isBoss);

	logger.log (jsonUtils.toJSONString (json));
	model.hasSubordinate = delegation.hasSubordinate (json);
}

switch (page) {
	case "list":
		getDescriptionList ();
		break;
	case "opts":
		getDescriptionOpts ();
		break;
	default:
		status.code = 400;
		status.message = '"' + page + '" is unknown page. Valid page is "list" or "opts"';
		status.redirect = true;
		break;
}
