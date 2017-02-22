<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if unit??>
		"fullName": "${unit.properties["lecm-orgstr:element-full-name"]}",
		"shortName": "${unit.properties["lecm-orgstr:element-short-name"]}",
		"code": "${unit.properties["lecm-orgstr:unit-code"]}",
		"type": "${unit.properties["lecm-orgstr:unit-type"]}",
		"active": "${unit.properties["lecm-dic:active"]?string}",
		"nodeRef": "${unit.getNodeRef().toString()}"
	</#if>
}
</#escape>