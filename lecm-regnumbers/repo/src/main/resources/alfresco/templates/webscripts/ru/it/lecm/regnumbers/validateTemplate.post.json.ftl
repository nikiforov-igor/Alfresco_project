<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if errorReason??>
		"errorReason": "${errorReason}",
	</#if>
		"isValid": ${isValid?string}
}
</#escape>
