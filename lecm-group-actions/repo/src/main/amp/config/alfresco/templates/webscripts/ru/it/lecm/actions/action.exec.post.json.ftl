<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "forCollection": ${forCollection?string},
	"message": "${messageVar!""}",
    "withErrors": ${withErrors?string},
    "redirect": "${redirect!""}",
    "postRedirect": ${(postRedirect!true)?string},
    "openWindow": "${openWindow!""}",
	"showModalWindow": ${(showModalWindow!false)?string}<#if items??>,
    "items": [
        <#list items as item>
        {
            "message": "${item.message!""}",
            "withErrors": ${item.withErrors?string},
            "redirect": "${item.redirect!""}",
            "postRedirect": ${(item.postRedirect!true)?string},
            "openWindow": "${item.openWindow!""}",
            "showModalWindow": ${(item.showModalWindow!false)?string}
        }<#if item_has_next>,</#if>
        </#list>
    ]
    </#if>
}
</#escape>
