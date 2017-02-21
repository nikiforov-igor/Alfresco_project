<#escape x as jsonUtils.encodeJSONString(x)!''>
	<#if config??>
	{
		"nodeRef": "${config.nodeRef}",
		"name": "${config.name}",
		"version": "${config.properties["cm:versionLabel"]!""}"
	}
	</#if>
</#escape>