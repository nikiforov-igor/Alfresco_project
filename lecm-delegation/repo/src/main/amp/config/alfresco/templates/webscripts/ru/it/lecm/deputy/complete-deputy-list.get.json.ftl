<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	"path": "${path}",
	"ignoredString": "${ignoredString}",
	"organization": "${userOrg}",
	"nodes": [
		<#if results??>
			<#list results as node>
				"${node}"
				<#if node_has_next>,</#if>
			</#list>
		</#if>
	]
}
</#escape>