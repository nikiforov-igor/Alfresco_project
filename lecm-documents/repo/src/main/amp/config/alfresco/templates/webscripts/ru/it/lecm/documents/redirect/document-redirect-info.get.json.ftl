<#escape x as x?js_string>
{
	<#if type??>
	"type":"${type}",
	</#if>
	<#if node??>
	"nodeRef":"${node.nodeRef?string}",
	</#if>
	"redirect":${redirect?string}
}
</#escape>
