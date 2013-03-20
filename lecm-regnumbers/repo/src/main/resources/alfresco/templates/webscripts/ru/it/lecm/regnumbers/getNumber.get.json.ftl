<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if regNumber??>
		"regNumber": "${regNumber}"
	<#else/>
		"regNumber": ""
	</#if>
}
</#escape>
