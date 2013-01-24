<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#list records as record>
        {
        "date": "${record.properties["lecm-notf:forming-date"]?date?string.short}",
        "record": "${record.properties["lecm-notf:description"]}"
        }
        <#if record_has_next>,</#if>
    </#list>
]
</#escape>