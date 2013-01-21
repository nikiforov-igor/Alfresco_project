<#escape x as jsonUtils.encodeJSONString(x)>
[
    <#list records as record>
        {
            "date": "${record.properties["lecm-busjournal:bjRecord-date"]?date?string.short}",
            "record": "${record.properties["lecm-busjournal:bjRecord-description"]}"
        }
        <#if record_has_next>,</#if>
    </#list>
]
</#escape>