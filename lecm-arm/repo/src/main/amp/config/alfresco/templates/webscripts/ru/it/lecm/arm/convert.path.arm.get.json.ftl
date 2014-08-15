<#escape x as jsonUtils.encodeJSONString(x)>
{
    <#if result??>
        "accordion": "${result.accordion}",
        "selected": "${result.selected}",
        "pageNum": "${result.pageNum}"
    </#if>
}
</#escape>
