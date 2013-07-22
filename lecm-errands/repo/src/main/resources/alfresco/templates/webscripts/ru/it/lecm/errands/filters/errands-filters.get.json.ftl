<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#list records as record>
    {
    "record":   "${record.properties["lecm-document:present-string"]?string}",
    "title":    "${record.properties["lecm-errands:title"]?string}",
    "number":   "${record.properties["lecm-errands:number"]?string}",
    "nodeRef":  "${record.getNodeRef()?string}",
    "executor": "${record.assocs["lecm-errands:executor-assoc"][0].nodeRef?string}",
    "executor_name" : "${record.properties["lecm-errands:executor-assoc-text-content"]?string}",
    "limit":    "${record.properties["lecm-errands:limitation-date"]?string("dd/MM/yyyy")}"
    }
        <#if record_has_next>,</#if>
    </#list>
]
</#escape>