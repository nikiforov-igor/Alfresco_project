<#escape x as jsonUtils.encodeJSONString(x)!''>
{
"data": [
    <#list records as record>
    {
    "nodeRef":     "${record.getNodeRef()?string}",
    "title":    "${record.properties["lecm-errands:title"]?string}",
    "number":    "${record.properties["lecm-errands:number"]?string}",
    "executorRef": "${record.assocs["lecm-errands:executor-assoc"][0].nodeRef?string}",
    "executor_name" : "${record.properties["lecm-errands:executor-assoc-text-content"]?string}",

        <#if record.properties["lecm-errands:work-start-date"]??>
        "startDate": "${record.properties["lecm-errands:work-start-date"]?string("yyyy-MM-dd")}",
        </#if>
        <#if record.properties["lecm-errands:limitation-date"]??>
        "endDate":        "${record.properties["lecm-errands:limitation-date"]?string("yyyy-MM-dd")}",
        </#if>

        <#if record.assocs["lecm-errands:controller-assoc"]??>
            "inControler": "true",
        <#else>
            "inControler": "false",
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
    "maxRecord": ${maxRecord?string},
    "maxItems": ${maxItems?string},
    "skipCount": ${skipCount?string},
    "maxDate": "${maxDate?string}",
    "minDate": "${minDate?string}"
}
}
</#escape>