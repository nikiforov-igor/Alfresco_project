<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#list records as record>
        {
            "date": "${record.properties["lecm-busjournal:bjRecord-date"]?datetime?string("MM/dd/yyyy HH:mm:ss")}",
            "record": "${record.properties["lecm-busjournal:bjRecord-description"]}",
            "initiator": "${record.properties["lecm-busjournal:bjRecord-initiator"]}",
            "initiatorRef": "${record.properties["lecm-busjournal:bjRecord-initiator-assoc-ref"]}"
        }
        <#if record_has_next>,</#if>
    </#list>
]
</#escape>