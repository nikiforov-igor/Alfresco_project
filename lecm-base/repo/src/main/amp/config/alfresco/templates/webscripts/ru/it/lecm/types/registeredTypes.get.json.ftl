<#escape x as x?js_string>
{ "data":
    [<#list types as type>
    {
        "value": "${type.name!""?js_string}",
        "name": "${type.title!""?js_string}"
    }<#if type_has_next>,</#if>
    </#list>]
}
</#escape>