<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if isNumberUnique??>
		"isNumberUnique": ${isNumberUnique?string}
	</#if>
}
</#escape>
