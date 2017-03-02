<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "user": {
        "nodeRef": "${user.nodeRef}"
        <#if user.roles??>,
        "roles":
        [
            <#list user.roles as role>
                "${role}"
                <#if role_has_next>,</#if>
            </#list>
        ]
        </#if>
    }
}
</#escape>