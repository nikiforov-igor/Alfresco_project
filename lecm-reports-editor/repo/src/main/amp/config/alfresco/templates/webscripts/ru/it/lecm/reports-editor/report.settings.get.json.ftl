<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    <#if settings??>
    "path": "${settings.path!""}",
    "isSubReport": "${(settings.isSub!false)?string}",
    "isSQLReport": "${(settings.isSQL!false)?string}",
    "parentReport": "${settings.parent!""}"
    </#if>
}
</#escape>
