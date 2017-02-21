<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<#list  workgroups as wg>
	{
		"fullName": "${wg.properties["lecm-orgstr:element-full-name"]}",
		"shortName": "${wg.properties["lecm-orgstr:element-short-name"]}",
		"description": "${wg.properties["lecm-orgstr:workGroup-description"]}",
		"active": "${wg.properties["lecm-dic:active"]?string}",
		"nodeRef": "${wg.getNodeRef().toString()}"
	}
		<#if wg_has_next>,</#if>
	</#list>
]
</#escape>