<#escape x as x?js_string>
{
    <#if folder??>
        "nodeRef": "${folder.nodeRef}"
    </#if>
}
</#escape>