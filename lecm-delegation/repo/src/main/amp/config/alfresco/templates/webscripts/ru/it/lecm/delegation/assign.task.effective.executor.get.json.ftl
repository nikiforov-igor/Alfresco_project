<#escape x as jsonUtils.encodeJSONString(x)!''>
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
