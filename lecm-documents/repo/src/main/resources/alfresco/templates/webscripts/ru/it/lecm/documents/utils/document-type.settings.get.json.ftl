<#escape x as x?js_string>
{
    "nodeRef": "${nodeRef}",
    "draftPath": "${draftPath}",
    "documentPath": "${documentPath}",
    "archivePath": "${archivePath}"
    <#if defaultFilter??>,
    "defaultFilter": "${defaultFilter}",
    "defaultKey": "${defaultKey}"
    </#if>
}
</#escape>