var nodeRef = args["nodeRef"]
logger.log ("nodeRef is " + nodeRef);

var delegationOpts = null;
var isSubjectValid;

if (!nodeRef) {
	nodeRef = person.nodeRef;
}
delegationOpts = delegation.getDelegationOpts (nodeRef);

if (delegationOpts) {
	logger.log (jsonUtils.toJSONString (delegationOpts));
	model.delegationOpts = delegationOpts.nodeRef.toString();
	model.isActive = delegationOpts.properties["lecm-dic:active"];
}

/*
switch (subject) {
	case "person":
		logger.log ("we need to call getDelegationOptsByPerson");
		//если мы не передали nodeRef пользователя, то использовать nodeRef текущего пользователя
		if (!nodeRef) {
			nodeRef = person.nodeRef;
		}
		delegationOpts = delegation.getDelegationOptsByPerson (nodeRef);
		isSubjectValid = true;
		break;
	case "employee":
		if (!nodeRef) {
			isSubjectValid = false;
			status.code = 400;
			status.message = 'URL argument "nodeRef" is required. You must provide correct value like "workspace://SpacesStore/uuid"';
			status.redirect = true;
		} else {
			logger.log ("we need to call getDelegationOptsByEmployee");
			delegationOpts = delegation.getDelegationOptsByEmployee (nodeRef);
			isSubjectValid = true;
		}
		break;
	default:
		isSubjectValid = false;
		status.code = 400;
		status.message = '"' + subject + '" specified in the Request-Line is not allowed for the resource identified by the Request-URI. Allowed values are "person" or "employee"';
		status.redirect = true;
		break;
}
if (isSubjectValid) {
	//если субъект адекватен то формируем ftl-ку
	//в противном случае она по идее не должна сформироваться
	if (delegationOpts) {
		logger.log (jsonUtils.toJSONString (delegationOpts));
		model.delegationOpts = delegationOpts.nodeRef.toString();
		model.isActive = delegationOpts.properties["lecm-dic:active"];
	} else {
		model.delegationOpts = null;
		model.isActive = null;
	}
}
*/