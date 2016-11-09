<#escape x as x?js_string>
[
    <#if types??>
        <#list types as type>
            {
                "nodeRef": "${type.nodeRef}",
                "name": "${type.name}",
                "defaultTitle": "${type.properties["lecm-errands-dic:errand-type-default-type"]}"
            }<#if type_has_next>,</#if>
        </#list>
    </#if>
]
</#escape>