<#escape x as jsonUtils.encodeJSONString(x)!''>
{
"links":
    [
        <#list links as link>
        {
        "nodeRef": "${link.nodeRef?string}",
        "name": "${link.properties["cm:name"]?string}",
        "url": "${link.properties["lecm-links:url"]?string}"
        }<#if link_has_next>,</#if>
        </#list>
    ]
}
</#escape>