<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if success??>
		"success": ${success?string}
	<#else/>
		"success": false
	</#if>
}
</#escape>