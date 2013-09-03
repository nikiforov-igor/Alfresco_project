<#escape x as jsonUtils.encodeJSONString(x)!''>
{
"data": [
    <#list records as record>
    {
        "nodeRef": "${record.nodeRef}",
        "record": "${record.record}",
        <#if record.date??>
        "date": "${record.date?string("yyyy-MM-dd")}",
        </#if>
        <#if record.baseDocString??>
        "baseDocString": "${record.baseDocString}",
        </#if>
        "title":    "${record.title}",
        "number":   "${record.number}",
        "initiator": "${record.initiator}",
        "initiator_name": "${record.initiator_name}",
        "isExpired":   "${record.isExpired?string}",
        "isImportant": "${record.isImportant?string}"
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