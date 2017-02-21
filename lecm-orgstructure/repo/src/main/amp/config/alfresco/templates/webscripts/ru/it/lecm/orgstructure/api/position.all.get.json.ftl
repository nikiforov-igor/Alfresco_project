<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<#list  positions as pos>
	{
		"name": "${pos.getName()}",
		"name_g": "${pos.properties["lecm-orgstr:staffPosition-name-g"]}",
		"name_d": "${pos.properties["lecm-orgstr:staffPosition-name-d"]}",
		"code": "${pos.properties["lecm-orgstr:staffPosition-code"]}",
        "active": "${pos.properties["lecm-dic:active"]?string}",
		"nodeRef": "${pos.getNodeRef()}"
	}
		<#if pos_has_next>,</#if>
	</#list>

]
</#escape>