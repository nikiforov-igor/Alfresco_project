<#escape x as jsonUtils.encodeJSONString(x)>
[
    <#if templates??>
        <#list templates as template>
        {
        "name": "${template.name}",
        "nodeRef": "${template.nodeRef}"
        }
            <#if template_has_next>,</#if>
        </#list>
    </#if>
]
</#escape>