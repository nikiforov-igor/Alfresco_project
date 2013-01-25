<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#list records as record>
        {
        "date": "${record.properties["lecm-notf:forming-date"]?datetime?string("MM/dd/yyyy HH:mm:ss")}",
        "record": "${record.properties["lecm-notf:description"]}"
        }
        <#if record_has_next>,</#if>
    </#list>
]
</#escape>