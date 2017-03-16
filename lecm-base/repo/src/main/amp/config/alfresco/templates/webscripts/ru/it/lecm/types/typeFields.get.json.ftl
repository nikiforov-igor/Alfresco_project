<#escape x as jsonUtils.encodeJSONString(x)!''>
{ "fields":
    [<#list fields as field>
    {
        "id": "${field.name!""?js_string}",
        "title": "${field.title!""?js_string}",
        "type": "${field.type!""?js_string}"
    }<#if field_has_next>,</#if>
    </#list>]
}
</#escape>