<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if redirect??>
		"redirect": "${redirect}",
	<#else>
		"redirect": null,
	</#if>
	<#if error??>
		"error": "${error}"
	<#else>
		"error": ""
	</#if>
}
</#escape>
