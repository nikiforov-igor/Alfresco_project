<#escape x as x?js_string>
[
	<#if roles??>
		<#list roles as role>
        {
	        "id": "${role.properties["lecm-orgstr:business-role-identifier"]?string}",
	        "nodeRef": "${role.nodeRef}"
        }
		<#if role_has_next>,</#if>
		</#list>
	</#if>
]
</#escape>