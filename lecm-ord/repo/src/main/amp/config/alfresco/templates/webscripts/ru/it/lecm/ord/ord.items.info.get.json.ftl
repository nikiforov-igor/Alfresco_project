<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "user": {
        "nodeRef": "${user.nodeRef}",
        "isController": ${user.isController?string}
        <#if user.roles??>,
        "roles":
        [
            <#list user.roles as role>
                "${role}"
                <#if role_has_next>,</#if>
            </#list>
        ]
        </#if>
    },
    "document": {
        "status": "${document.status}"
    }

}
</#escape>