<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "locationAvailable": ${locationAvailable?string},
    "members": [
        <#list members as member>
            {
                "nodeRef": "${member.nodeRef}",
                "name": "${member.name}",
                "available": ${member.available?string}
            }<#if member_has_next>,</#if>
        </#list>
    ]
}
</#escape>