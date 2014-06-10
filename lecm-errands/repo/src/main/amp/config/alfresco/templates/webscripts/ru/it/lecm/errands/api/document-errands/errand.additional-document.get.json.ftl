<#escape x as x?js_string>
{
    <#if additionalDoc??>
        "nodeRef": "${additionalDoc.nodeRef}",
        "name": "${additionalDoc.properties["lecm-document:present-string"]!additionalDoc.getName()}"
    </#if>
}
</#escape>