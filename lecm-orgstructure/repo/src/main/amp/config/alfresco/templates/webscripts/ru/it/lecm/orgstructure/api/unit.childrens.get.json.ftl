<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<#list  subUnits as unit>
	{
		"fullName": "${unit.properties["lecm-orgstr:element-full-name"]}",
		"shortName": "${unit.properties["lecm-orgstr:element-short-name"]}",
		"code": "${unit.properties["lecm-orgstr:unit-code"]}",
		"type": "${unit.properties["lecm-orgstr:unit-type"]}",
		"active": "${unit.properties["lecm-dic:active"]?string}",
		"nodeRef": "${unit.getNodeRef().toString()}"
	}
		<#if unit_has_next>,</#if>
	</#list>
]
</#escape>