<#escape x as x?js_string>
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