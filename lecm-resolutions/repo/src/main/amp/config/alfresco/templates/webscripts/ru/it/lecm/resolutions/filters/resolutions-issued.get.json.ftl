<#escape x as jsonUtils.encodeJSONString(x)!''>
{
"list": [
    <#list items as item>
    {
        "key":  "${item.key}",
        "allCount": "${item.allCount}",
        "controlCount": "${item.controlCount}",
        "path": "${item.path}",
        "controlPath": "${item.controlPath}",
        "armCode": "${item.armCode}"
    }
        <#if item_has_next>,</#if>
    </#list>
]
}
</#escape>