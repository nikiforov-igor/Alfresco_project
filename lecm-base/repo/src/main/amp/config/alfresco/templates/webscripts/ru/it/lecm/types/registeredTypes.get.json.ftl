<#escape x as jsonUtils.encodeJSONString(x)!''>
{ "data":
    [<#list types as type>
    {
        "value": "${type.name!""?js_string}",
        "name": "${type.title!""?js_string}"
    }<#if type_has_next>,</#if>
    </#list>]
}
</#escape>