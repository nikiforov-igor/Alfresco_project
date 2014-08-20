<#escape x as x?js_string>
{
"list": [
    <#list items as item>
    {
        "key":  "${item.key}",
        "allCount": "${item.allCount}",
        "importantCount": "${item.importantCount}",
        "path": "${item.path}",
        "importantPath": "${item.importantPath}",
        "armCode": "${item.armCode}"
    }
        <#if item_has_next>,</#if>
    </#list>
    ]
}
</#escape>