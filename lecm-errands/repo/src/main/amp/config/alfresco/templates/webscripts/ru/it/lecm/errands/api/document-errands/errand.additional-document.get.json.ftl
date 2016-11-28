<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    <#if additionalDoc??>
        "nodeRef": "${additionalDoc.nodeRef}",
        "name": "${additionalDoc.properties["lecm-document:present-string"]!additionalDoc.getName()}"
    </#if>
}
</#escape>