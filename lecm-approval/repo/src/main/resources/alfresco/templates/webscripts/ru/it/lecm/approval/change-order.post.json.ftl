<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if success??>
		"success": ${success?string}
	</#if>
}
</#escape>