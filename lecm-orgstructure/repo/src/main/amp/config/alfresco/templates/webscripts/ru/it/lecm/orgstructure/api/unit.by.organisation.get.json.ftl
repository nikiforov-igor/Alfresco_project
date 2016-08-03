<#escape x as jsonUtils.encodeJSONString(x)>
{
    <#if unit??>
        "shortName": "${unit.properties["lecm-orgstr:element-short-name"]}",
        "fullName": "${unit.properties["lecm-orgstr:element-full-name"]}",
        "nodeRef": "${unit.getNodeRef()}"
    </#if>
}
</#escape>