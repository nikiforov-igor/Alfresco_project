<#escape x as jsonUtils.encodeJSONString(x)>
[
    <#if types??>
        <#list types as type>
            {
                "nodeRef": "${type.nodeRef}",
                "name": "${type.name}",
                "defaultTitle": "${type.properties["lecm-errands-dic:errand-type-default-title"]}",
                "manual-selection": ${type.properties["lecm-errands-dic:errand-type-manual-selection"]?string},
                "report-required": ${type.properties["lecm-errands-dic:errand-type-report-required"]?string},
                "limitless": ${type.properties["lecm-errands-dic:errand-type-limitless"]?string},
                "launch-review": ${type.properties["lecm-errands-dic:errand-type-launch-review"]?string}

        }<#if type_has_next>,</#if>
        </#list>
    </#if>
]
</#escape>