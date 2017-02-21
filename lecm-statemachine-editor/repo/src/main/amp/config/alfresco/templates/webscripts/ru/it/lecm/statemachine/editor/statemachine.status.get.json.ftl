<#escape x as jsonUtils.encodeJSONString(x)!''>
{ "data": [
    <#list statuses as status>
    {
        "name": "${status.name}",
        "value": "${status.nodeRef}"
    }
        <#if status_has_next>,</#if>
    </#list>
]}
</#escape>