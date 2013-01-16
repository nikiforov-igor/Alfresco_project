var subscriptionRef = args["subscriptionRef"];
var objectRef = args["objectRef"];

var subscription = search.findNode(subscriptionRef);

var types = [];
if (subscription.assocs["lecm-subscr:notification-type-assoc"] != null) {
	for (var i = 0; i < subscription.assocs["lecm-subscr:notification-type-assoc"].length; i++) {
		types[i] = subscription.assocs["lecm-subscr:notification-type-assoc"][i].nodeRef;
	}
}

var employees = [];
if (subscription.assocs["lecm-subscr:destination-employee-assoc"] != null) {
	for (i = 0; i < subscription.assocs["lecm-subscr:destination-employee-assoc"].length; i++) {
		employees[i] = subscription.assocs["lecm-subscr:destination-employee-assoc"][i].nodeRef;
	}
}

var organizationUnits = [];
if (subscription.assocs["lecm-subscr:destination-organization-unit-assoc"] != null) {
	for (i = 0; i < subscription.assocs["lecm-subscr:destination-organization-unit-assoc"].length; i++) {
		organizationUnits[i] = subscription.assocs["lecm-subscr:destination-organization-unit-assoc"][i].nodeRef;
	}
}

var workGroups = [];
if (subscription.assocs["lecm-subscr:destination-work-group-assoc"] != null) {
	for (i = 0; i < subscription.assocs["lecm-subscr:destination-work-group-assoc"].length; i++) {
		workGroups[i] = subscription.assocs["lecm-subscr:destination-work-group-assoc"][i].nodeRef;
	}
}

var positions = [];
if (subscription.assocs["lecm-subscr:destination-position-assoc"] != null) {
	for (i = 0; i < subscription.assocs["lecm-subscr:destination-position-assoc"].length; i++) {
		positions[i] = subscription.assocs["lecm-subscr:destination-position-assoc"][i].nodeRef;
	}
}

model.success = notifications.testSendNotification(objectRef, types, employees, organizationUnits, workGroups, positions);