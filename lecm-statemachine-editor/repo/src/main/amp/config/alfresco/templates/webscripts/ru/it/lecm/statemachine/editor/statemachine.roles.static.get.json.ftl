<#escape x as jsonUtils.encodeJSONString(x)!''>
{ "data": [
    <#list roles as role>
    {
        "name": "${role.name}",
        "value": "${role.nodeRef}"
    }
        <#if role_has_next>,</#if>
    </#list>
]}
</#escape>