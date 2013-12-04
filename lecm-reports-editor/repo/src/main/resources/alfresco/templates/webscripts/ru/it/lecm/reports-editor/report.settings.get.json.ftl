<#escape x as x?js_string>
{
    <#if settings??>
    "path": "${settings.path!""}",
    "isSubReport": "${(settings.isSub!false)?string}",
    "isSQLReport": "${(settings.isSQL!false)?string}",
    "parentReport": "${settings.parent!""}"
    </#if>
}
</#escape>
