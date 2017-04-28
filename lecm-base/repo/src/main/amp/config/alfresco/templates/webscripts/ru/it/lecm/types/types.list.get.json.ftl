<#escape x as x?json_string>
{ "data": [
    <#if types??>
        <#list types as type>
        {
        "name": "${type.name!""}",
        "value": "${type.value!""}"
        }<#if type_has_next>,</#if>
        </#list>
    </#if>]
}
</#escape>