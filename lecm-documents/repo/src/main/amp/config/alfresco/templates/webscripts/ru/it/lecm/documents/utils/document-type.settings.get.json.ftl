<#escape x as x?js_string>
{
    "nodeRef": "${nodeRef}",
    "title": "${title!''}",
    "draftPath": "${draftPath}",
    "documentPath": "${documentPath}",
    "archivePath": "${archivePath}"
    <#if defaultFilter??>,
    "defaultFilter": "${defaultFilter}",
    "defaultKey": "${defaultKey}"
    </#if>
}
</#escape>