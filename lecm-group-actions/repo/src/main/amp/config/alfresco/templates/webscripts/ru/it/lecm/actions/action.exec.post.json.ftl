<#escape x as x!""?js_string>
{
    forCollection: ${forCollection?string},
    withErrors: ${withErrors?string},
    redirect: "${redirect!""}",
    openWindow: "${openWindow!""}"
    <#if items??>
    ,
    items: [
        <#list items as item>
        {
            message: "${item.message}",
            withErrors: ${item.withErrors?string},
            redirect: "${item.redirect!""}",
            openWindow: "${item.openWindow!""}"
        }<#if item_has_next>,</#if>
        </#list>
    ]
    </#if>
}
</#escape>