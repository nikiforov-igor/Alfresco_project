<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "nodeRef": "${link.nodeRef?string}",
    "name": "${link.properties["cm:name"]?string}",
    "url": "${link.properties["lecm-links:url"]?string}",
    "success": "${success?string}"
}
</#escape>