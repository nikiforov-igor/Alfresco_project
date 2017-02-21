<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<#if versions??>
		<#list versions as version>
			<#if version.node??>
				{
					"version": "${version.label}",
					"nodeRef": "${version.node.nodeRef}"
				}<#if version_has_next>,</#if>
			</#if>
		</#list>
	</#if>
]
</#escape>