<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#list records as record>
        {
            "date": "${record.properties["lecm-busjournal:bjRecord-date"]?datetime?string("MM/dd/yyyy HH:mm:ss")}",
            "record": "${record.properties["lecm-busjournal:bjRecord-description"]}"
        }
        <#if record_has_next>,</#if>
    </#list>
]
</#escape>