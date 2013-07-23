<#escape x as x?js_string>
{
	<#if targetEmployee??> 
		"targetEmployee": "${targetEmployee.nodeRef.toString()}",
	</#if>
	<#if success??>
		"success": ${success?string}
	<#else>
		"success": false
	</#if>
}
</#escape>
