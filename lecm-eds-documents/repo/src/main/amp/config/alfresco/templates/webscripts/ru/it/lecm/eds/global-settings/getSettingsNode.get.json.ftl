<#escape x as jsonUtils.encodeJSONString(x)>
{
    <#if node??>
        "nodeRef": "${node.getNodeRef().toString()}",
        "isRegCenralized" : <#if node.properties["lecm-eds-globset:centralized-registration"]??>${node.properties["lecm-eds-globset:centralized-registration"]?string!false}<#else>false</#if>
        <#if node.assocs["lecm-eds-globset:arm-for-dashlet-assoc"]??>
        ,"armCode": "${node.assocs["lecm-eds-globset:arm-for-dashlet-assoc"][0]["lecm-arm:code"]!"SED"}"
        </#if>
        <#if node.assocs["lecm-eds-globset:arm-node-for-dashlet-assoc"]??>
        ,"armDashletNode": "${node.assocs["lecm-eds-globset:arm-node-for-dashlet-assoc"][0].getNodeRef().toString()}"
        </#if>
    </#if>
}
</#escape>