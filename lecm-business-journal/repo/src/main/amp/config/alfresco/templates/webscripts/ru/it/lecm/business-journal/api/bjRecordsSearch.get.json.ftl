<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#list records as record>
        {
            "date": "${record.getDate()?datetime?string("MM/dd/yyyy HH:mm:ss")}",
            "record": "${record.getRecordDescription()}",
            "initiator": "${record.getInitiator()!""}",
            "initiatorRef": <#if record.getInitiator()??>"${record.getInitiator().nodeRef}"<#else>""</#if>
        }
        <#if record_has_next>,</#if>
    </#list>
]
</#escape>