<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">

function main() {
	var params = {};
	if (typeof json !== "undefined" && json.has("params")) {
		var pars = json.get("params");

	var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
	var serviceDictionary = ctx.getBean("serviceDictionary");

	var qname = Packages.org.alfresco.service.namespace.QName.createQName("http://www.it.ru/lecm/org/structure/1.0", "business-role-is-dynamic");

	var value = new Boolean(true);
	var businessRoles = serviceDictionary.getRecordsByParamValue(
	Packages.ru.it.lecm.orgstructure.beans.OrgstructureBean.BUSINESS_ROLES_DICTIONARY_NAME,
	qname,
	Packages.java.lang.Boolean.TRUE).toArray();

	var dictionaryRoles = [];
	for each (var role in businessRoles) {
	dictionaryRoles[role.toString()] = 1;
	};

	var roleFolder = search.findNode(pars.get("parent"));

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
		newRole.properties["lecm-stmeditor:permissionTypeValue"] = "full";
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
			startIndex: pars.has("startIndex") ? parseInt(pars.get("startIndex"), 10) : DEFAULT_INDEX
		};
}

model.data = getSearchResults(params); // call method from search.lib.js
}

main();
