<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if isNumberUnique??>
		"isNumberUnique": "${isNumberUnique}",
	</#if>
}
</#escape>
