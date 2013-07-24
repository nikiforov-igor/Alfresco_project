<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#list records as record>
    {
    "record":      "${record.properties["lecm-document:present-string"]?string}",
    <#if record.properties["lecm-errands:limitation-date"]??>
    "date":        "${record.properties["lecm-errands:limitation-date"]?string("dd/MM/yyyy")}",
    </#if>
    "isExpired":   "${record.properties["lecm-errands:is-expired"]?string}",
    "isImportant": "${record.properties["lecm-errands:is-important"]?string}"
    }
        <#if record_has_next>,</#if>
    </#list>
]
</#escape>