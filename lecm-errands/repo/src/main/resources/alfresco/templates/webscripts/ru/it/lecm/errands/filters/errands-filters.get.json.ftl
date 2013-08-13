<#escape x as jsonUtils.encodeJSONString(x)!''>
{
"data": [
    <#list records as record>
    {
        "nodeRef":     "${record.getNodeRef()?string}",
        "record":      "${record.properties["lecm-document:present-string"]?string}",
        <#if record.properties["lecm-errands:limitation-date"]??>
        "date":        "${record.properties["lecm-errands:limitation-date"]?string("dd/MM/yyyy")}",
        </#if>
        <#if record.assocs["lecm-errands:additional-document-assoc"]??>
        "baseDocString" : "${record.assocs["lecm-errands:additional-document-assoc"][0].properties["lecm-document:present-string"]?string}",
        </#if>
        "isExpired":   "${record.properties["lecm-errands:is-expired"]?string}",
        "isImportant": "${record.properties["lecm-errands:is-important"]?string}"
    }
        <#if record_has_next>,</#if>
    </#list>
],
"paging":
    {
    "totalItems": ${totalItems?string},
    "maxItems": ${maxItems?string},
    "skipCount": ${skipCount?string}
    }
}
</#escape>