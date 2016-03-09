<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if resp??>
		"resp": {
		<#list resp?keys as key>
			"${key}": "${resp[key]}"<#if key_has_next>,</#if>
		</#list>
		},
	<#else>
		"resp": {},
	</#if>
	"success": "true"
}
</#escape>
