<#escape x as x?js_string>
{
    title: "${title!""}",
    baseQuery : "${baseQuery!""}",
    isExist: ${isExist?string},
    "filters":
    [
    <#if filters??>
        <#list filters as filter>
        {
        "title": "${filter.title}",
        "query": "${(filter.query!"")}"
        }<#if filter_has_next>,</#if>
        </#list>
    </#if>
    ]
}
</#escape>