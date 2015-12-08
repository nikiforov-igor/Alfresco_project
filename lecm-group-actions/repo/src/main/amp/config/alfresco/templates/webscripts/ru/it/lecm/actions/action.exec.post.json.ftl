<#escape x as x!""?js_string>
{
    "forCollection": ${forCollection?string},
    "withErrors": ${withErrors?string},
    "redirect": "${redirect!""}",
    "postRedirect": ${(postRedirect!true)?string},
    "openWindow": "${openWindow!""}"
    <#if items??>
    ,
    "items": [
        <#list items as item>
        {
            "message": "${item.message}",
            "withErrors": ${item.withErrors?string},
            "redirect": "${item.redirect!""}",
            "postRedirect": ${(item.postRedirect!true)?string},
            "openWindow": "${item.openWindow!""}"
        }<#if item_has_next>,</#if>
        </#list>
    ]
    </#if>
}
</#escape>
