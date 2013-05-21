<#escape x as x?js_string>
{
"list":
    [
        <#list list as item>
        {
        "key" : "${item.key}",
        "skip": "${item.skip?string}",
        "amountContracts" : "${item.amountContracts}",
        "filter" : "${item.filter}"
        }
            <#if item_has_next>,</#if>
        </#list>
    ],
"members": {
    <#list members as member>
    "key": "${member.key}",
    "amountMembers": "${member.amountMembers}"
    </#list>
}
}
</#escape>