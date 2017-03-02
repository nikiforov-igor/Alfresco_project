<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	"data": [
		<#if results??>
			<#list results as result>
			{
				"name": "${result.name}",
				"value": "${result.value}",
				"isPlane": ${result.isPlane?js_string}
			}<#if result_has_next>,</#if>
			</#list>
		</#if>
	]
}
</#escape>
