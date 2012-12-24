<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if procuracies??>
	"procuracies": [
		<#list procuracies as procuracy>
		{
			"nodeRef": "${procuracy.nodeRef}"
		}<#if procuracy_has_next>, </#if>
        </#list>
	]
	<#else/>
	"procuracies": []
	</#if>
}
</#escape>
