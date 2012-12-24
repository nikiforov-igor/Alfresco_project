var subject = url.templateArgs["subject"];
var nodeRef = args["nodeRef"]
logger.log ("subject is " + subject);
logger.log ("nodeRef is " + nodeRef);

var procuracies = null;
var isSubjectValid;
switch (subject) {
	case "person":
		logger.log ("we need to call actualizeProcuraciesForPerson");
		//если мы не передали nodeRef пользователя, то использовать nodeRef текущего пользователя
		if (!nodeRef) {
			nodeRef = person.nodeRef;
		}
		procuracies = delegation.actualizeProcuraciesForPerson (nodeRef);
		isSubjectValid = true;
		break;
	case "employee":
		if (!nodeRef) {
			isSubjectValid = false;
			status.code = 400;
			status.message = 'URL argument "nodeRef" is required. You must provide correct value like "workspace://SpacesStore/uuid"';
			status.redirect = true;
		} else {
			logger.log ("we need to call actualizeProcuraciesForEmployee");
			procuracies = delegation.actualizeProcuraciesForEmployee (nodeRef);
			isSubjectValid = true;
		}
		break;
	case "delegator":
		if (!nodeRef) {
			isSubjectValid = false;
			status.code = 400;
			status.message = 'URL argument "nodeRef" is required. You must provide correct value like "workspace://SpacesStore/uuid"';
			status.redirect = true;
		} else {
			logger.log ("we need to call actualizeProcuraciesForDelegationOpts");
			procuracies = delegation.actualizeProcuraciesForDelegationOpts (nodeRef);
			isSubjectValid = true;
		}
		break;
	default:
		isSubjectValid = false;
		status.code = 400;
		status.message = '"' + subject + '" specified in the Request-Line is not allowed for the resource identified by the Request-URI. Allowed values are "person" or "employee" or "delegator"';
		status.redirect = true;
		break;
}
if (isSubjectValid) {
	//если субъект адекватен то формируем ftl-ку
	//в противном случае она по идее не должна сформироваться
	if (procuracies) {
		model.procuracies = procuracies;
	} else {
		model.procuracies = [];
	}
}
