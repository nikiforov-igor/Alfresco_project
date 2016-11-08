<#escape x as jsonUtils.encodeJSONString(x)>
[
	<#if results??>
		<#list results as arm>
        {
        "nodeRef": "${arm.nodeRef.toString()}",
		"title": "${arm.name}",
		"code":"${arm.properties['lecm-arm:code']}"
        }<#if arm_has_next>,</#if>
		</#list>
	</#if>
]
</#escape>