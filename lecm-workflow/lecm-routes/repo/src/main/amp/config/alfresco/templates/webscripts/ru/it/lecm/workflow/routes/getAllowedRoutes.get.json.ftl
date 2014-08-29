<#escape x as jsonUtils.encodeJSONString(x)>
{
	"nodes": [
		<#list routeNodes as routeNode>
			"${routeNode.nodeRef.toString()}"<#if routeNode_has_next>,</#if>
		</#list>
	]
}
</#escape>
