<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">

function main() {
	var params = {};
	if (typeof json !== "undefined" && json.has("params")) {
		var pars = json.get("params");

    var roleFolder = search.findNode(pars.get("parent"));
    var roles = roleFolder.getParent().getParent().getParent().getParent().childByNamePath("roles-list");

    var dictionaryRoles = [];
	for each (var role in roles.getChildren()) {
        if (role.getTypeShort() == "lecm-stmeditor:dynamic-role-item") {
    	    dictionaryRoles[role.assocs["lecm-stmeditor:role-assoc"][0].nodeRef.toString()] = 1;
        }
	};

	var activeRoles = [];

	for each (var role in roleFolder.children) {
		activeRoles[role.assocs["lecm-stmeditor:role-assoc"][0].nodeRef.toString()] = role;
	}

	var forDelete = [];
	for (var key in activeRoles) {
		if (dictionaryRoles[key] == null) {
			forDelete.push(activeRoles[key]);
		}
	}

	var forCreate = [];
	for (var key in dictionaryRoles) {
		if (activeRoles[key] == null) {
			forCreate.push(key);
		}
	}

	for each (var node in forDelete) {
		node.remove();
	}

	for each (var node in forCreate) {
		var newRole = roleFolder.createNode(null, "lecm-stmeditor:dynamic-role", "cm:contains");
		newRole.properties["lecm-stmeditor:permissionTypeValue"] = "LECM_BASIC_PG_Reader";
		newRole.save();
		var dictionaryRole = search.findNode(node);
		newRole.createAssociation(dictionaryRole, "lecm-stmeditor:role-assoc");
	}

		params = {
			searchConfig: (pars.get("searchConfig").length() > 0)  ? pars.get("searchConfig") : null,
			maxResults:(pars.get("maxResults") !== null) ? parseInt(pars.get("maxResults"), 10) : DEFAULT_PAGE_SIZE,
			fields:(pars.get("fields").length() > 0) ? pars.get("fields") : null,
			nameSubstituteStrings:(pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null,
			showInactive: pars.get("showInactive") == true,
			parent: (pars.get("parent").length() > 0)  ? pars.get("parent") : null,
			itemType:(pars.get("itemType").length() > 0)  ? pars.get("itemType") : null,
            useChildQuery: pars.has("useChildQuery") ? ("" + pars.get("useChildQuery") == "true") : false,
			startIndex: pars.has("startIndex") ? parseInt(pars.get("startIndex"), 10) : DEFAULT_INDEX
		};
}

model.data = getSearchResults(params); // call method from search.lib.js
}

main();
