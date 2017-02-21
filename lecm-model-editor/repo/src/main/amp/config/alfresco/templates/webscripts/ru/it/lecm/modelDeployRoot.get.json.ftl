<#escape x as jsonUtils.encodeJSONString(x)!''>
	<#if rootFolder??>
	{
		"nodeRef": "${rootFolder.nodeRef}"
	}
	</#if>
</#escape>