<#escape x as x?js_string>
{
"list": [
    <#list items as item>
    {
        "key":  "${item.key}",
        "allCount": "${item.allCount}",
        "importantCount": "${item.importantCount}",
        "filter": "${item.filter}",
        "importantFilter": "${item.importantFilter}"
    }
        <#if item_has_next>,</#if>
    </#list>
    ]
}
</#escape>