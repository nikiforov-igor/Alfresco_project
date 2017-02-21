<#escape x as jsonUtils.encodeJSONString(x)!''>
{
<#if root??>
    "nodeRef": "${root.nodeRef!"NOT_LOAD"}",
    "itemType": "${root.itemType!""}"
</#if>
}
</#escape>
