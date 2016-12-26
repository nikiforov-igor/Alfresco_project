<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    <#if baseDoc??>
    "nodeRef": "${baseDoc.nodeRef}",
    "name": "${baseDoc.properties["lecm-document:present-string"]!baseDoc.getName()}",
    "isFinal": ${isFinal?string}
    </#if>
}
</#escape>