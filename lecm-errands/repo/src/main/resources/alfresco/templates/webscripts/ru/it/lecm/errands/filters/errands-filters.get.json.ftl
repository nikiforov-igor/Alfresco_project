<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#list records as record>
    {
    "record":   "${record.properties["lecm-document:present-string"]?string}"
    }
        <#if record_has_next>,</#if>
    </#list>
]
</#escape>