<#escape x as x?js_string>
{
    <#if node??>
        nodeRef: "${node.getNodeRef().toString()}",
        isRegCenralized : ${node.properties["lecm-eds-globset:centralized-registration"]!false},
        isHideProps: ${node.properties["lecm-eds-globset:hide-properties-for-recipients"]!false}
    </#if>
}
</#escape>