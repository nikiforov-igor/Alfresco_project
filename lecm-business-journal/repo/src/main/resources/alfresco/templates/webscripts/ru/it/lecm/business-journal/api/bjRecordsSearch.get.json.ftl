<#escape x as jsonUtils.encodeJSONString(x)>
[
    <#list records as record>
        "${record.properties["lecm-busjournal:bjRecord-description"]}"
        <#if record_has_next>,</#if>
    </#list>
]
</#escape>