<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if effectiveExecutor??>
		"effectiveExecutor": "${effectiveExecutor}"
	</#if>
}
</#escape>
