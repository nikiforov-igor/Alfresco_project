<#escape x as x?js_string>
{
	testUnit: "${result}",
	id: "${result2.properties["lecm-dlg:testUnitId"]}",
	name: "${result2.properties["lecm-dlg:testUnitName"]}",
	title: "${result2.properties["lecm-dlg:testUnitTitle"]}",
	date: "${result2.properties["lecm-dlg:testUnitDate"]?datetime}",
	nodeRef: "${result2}"
}
</#escape>