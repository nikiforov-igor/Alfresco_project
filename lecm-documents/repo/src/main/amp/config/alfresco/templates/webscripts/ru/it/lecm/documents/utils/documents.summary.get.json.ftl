<#escape x as x?js_string>
{
"list":
    [
        <#list list as item>
        {
        "key" : "${msg(item.key)}",
        "skip": ${item.skip?string},
        "amount" : ${item.amount},
        "filter" : "${item.filter}"
        }
            <#if item_has_next>,</#if>
        </#list>
    ],
"members": {
    <#list members as member>
    "key": "${msg(member.key)}",
    "amountMembers": ${member.amountMembers}
    </#list>
}
}
</#escape>