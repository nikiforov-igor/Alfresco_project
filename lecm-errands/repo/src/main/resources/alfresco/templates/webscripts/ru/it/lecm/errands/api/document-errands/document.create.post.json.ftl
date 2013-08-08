<#escape x as x?js_string>
{
    "nodeRef": "${link.nodeRef?string}",
    "name": "${link.properties["cm:name"]?string}",
    "url": "${link.properties["lecm-links:url"]?string}",
    "success": "${success?string}"
}
</#escape>