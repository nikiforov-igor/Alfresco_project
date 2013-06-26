<#escape x as x?js_string>
{
    <#if template??>
        "name": "${template.name}",
        "nodeRef": "${template.nodeRef}"
    </#if>
}
</#escape>