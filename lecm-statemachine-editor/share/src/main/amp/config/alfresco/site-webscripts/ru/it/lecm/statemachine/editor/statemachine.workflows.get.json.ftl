<#escape x as jsonUtils.encodeJSONString(x)!''>
{ "data":
    [<#list workflowDefinitions as workflowDefinition>
    {
        "value": "${workflowDefinition.name!""?js_string}",
        "name": "${workflowDefinition.title!""?js_string}"
    }<#if workflowDefinition_has_next>,</#if>
    </#list>]
}
</#escape>