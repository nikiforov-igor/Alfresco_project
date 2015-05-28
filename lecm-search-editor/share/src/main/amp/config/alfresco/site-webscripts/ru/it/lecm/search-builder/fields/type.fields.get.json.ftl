<#escape x as jsonUtils.encodeJSONString(x)>
{
    <#if error??>
    "error": "${error}"
    <#else>
    "fields":
    [
        <#list fields as col>
        {
        "type": "${col.type}",
        "name": "${col.name}",
        "formsName": "<#if col.type == "association">assoc<#else>prop</#if>_${col.name?replace(":", "_")}",
        <#if col.control??>
        "control": {
            "template": "${col.control.template!''}",
            "params": [
                <#list col.control.params as param>
                {
                    "name":"${param.name}",
                    "value":"${param.value}"
                }<#if param_has_next>,</#if>
                </#list>
            ]
        },
        </#if>
        "label": "${col.label!""}",
            <#if col.nameSubstituteString??>
            "nameSubstituteString":"${col.nameSubstituteString}",
            </#if>
            <#if col.dataType??>
            "dataType": "${col.dataType}",
            <#else>
            "dataType": "${col.endpointType}",
            </#if>
            <#if col.sortable??>
            "sortable": ${col.sortable?string}
            <#else>
            "sortable": true
            </#if>
        }<#if col_has_next>,</#if>
        </#list>
    ]
    </#if>
}
</#escape>