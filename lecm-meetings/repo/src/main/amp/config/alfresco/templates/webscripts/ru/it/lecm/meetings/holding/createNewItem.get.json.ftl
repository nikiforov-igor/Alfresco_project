<#escape x as jsonUtils.encodeJSONString(x)>
{
    <#if item?exists>
		"nodeRef": "${item.nodeRef}"
    </#if>
}
</#escape>
