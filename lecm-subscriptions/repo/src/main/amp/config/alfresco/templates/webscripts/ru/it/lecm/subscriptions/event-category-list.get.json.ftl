<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<#list value as b>
    {
    "nodeRef": "${b.nodeRef}",
    "name": "${b.name}"
    }
		<#if b_has_next>,</#if>
	</#list>
]
</#escape>