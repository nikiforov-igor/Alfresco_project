<#escape x as x?js_string>
	<#if config??>
	{
		"nodeRef": "${config.nodeRef}",
		"name": "${config.name}",
		"version": "${config.properties["cm:versionLabel"]!""}"
	}
	</#if>
</#escape>