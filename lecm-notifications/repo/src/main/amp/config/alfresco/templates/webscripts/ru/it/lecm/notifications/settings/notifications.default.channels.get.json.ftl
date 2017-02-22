<#escape x as jsonUtils.encodeJSONString(x)!''>
<#if channels??>
[
	<#list channels as channel>
		{
			"nodeRef": "${channel.nodeRef}"
		}<#if channel_has_next>,</#if>
	</#list>
]
</#if>
</#escape>