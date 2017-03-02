<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if type??>
	"type": "${type}",
	</#if>
	<#if node??>
	"nodeRef": "${node.nodeRef?string}",
	</#if>
	"redirect": ${redirect?string}
}
</#escape>
