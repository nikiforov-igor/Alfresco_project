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
    "title":    "${record.title}",
    "summary":    "${record.summary}",
    "status":    "${record.status}",
    "number":   "${record.number}",
    "initiator": "${record.initiator}",
    "initiator_name": "${record.initiator_name}",
    "executor": "${record.executor}",
    "executor_name": "${record.executor_name}",
    "isExpired":   "${record.isExpired?string}",
    "isImportant": "${record.isImportant?string}",
    "subject": "${record.subject}"
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