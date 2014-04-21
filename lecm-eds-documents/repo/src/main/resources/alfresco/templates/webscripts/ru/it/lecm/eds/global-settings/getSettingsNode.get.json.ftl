<#escape x as x?js_string>
{
    <#if node??>
        nodeRef: "${node.getNodeRef().toString()}",
        isRegCenralized : <#if node.properties["lecm-eds-globset:centralized-registration"]??>${node.properties["lecm-eds-globset:centralized-registration"]?string!false}<#else>false</#if>,
        isHideProps: <#if node.properties["lecm-eds-globset:hide-properties-for-recipients"]??>${node.properties["lecm-eds-globset:hide-properties-for-recipients"]?string!false}<#else>false</#if>
        <#if node.assocs["lecm-eds-globset:arm-node-for-dashlet-assoc"]??>
        ,"armDashletNode": "${node.assocs["lecm-eds-globset:arm-node-for-dashlet-assoc"][0].getNodeRef().toString()}"
        </#if>
    </#if>
}
</#escape>