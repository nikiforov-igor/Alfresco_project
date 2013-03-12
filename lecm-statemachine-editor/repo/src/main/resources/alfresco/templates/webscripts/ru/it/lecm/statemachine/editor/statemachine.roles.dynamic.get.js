var nodeRef = args["nodeRef"];

if (nodeRef != null) {

	var rolesRoot = search.xpathSearch("/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_Справочники/lecm-dic:Бизнес_x0020_роли")[0];
	var query = " +PATH:\"" + rolesRoot.getQnamePath() + "//* \" AND @lecm-orgstr\\:business-role-is-dynamic: \"true\"";

	var roles = search.query(
		{
			query: query,
			language: "lucene",
			page:
			{
				maxItems: 1000
			},
			sort: [
				{
					column: "@cm:name",
					ascending: true
				}]
		});
	var result = [];
	for each (var role in roles) {
		result.push({
			nodeRef: role.nodeRef.toString(),
			name: role.properties["cm:name"]
		});

	}
	model.roles = result;
}