<#escape x as jsonUtils.encodeJSONString(x)>
[
    <#if sources??>
        <#list sources as source>
        {
        "name": "${source.name}",
        "code": "${source.properties["lecm-rpeditor:dataSourceCode"]!""}",
        "nodeRef": "${source.nodeRef}"
        }
            <#if source_has_next>,</#if>
        </#list>
    </#if>
]
</#escape>