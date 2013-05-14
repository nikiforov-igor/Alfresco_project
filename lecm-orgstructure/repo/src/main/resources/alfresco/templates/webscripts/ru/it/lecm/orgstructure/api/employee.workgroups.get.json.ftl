<#escape x as x?js_string>
[
    <#list  workGroups as wg>
    {
        groupName: "${wg.getName()}",
        groupRef: "${wg.getNodeRef().toString()}"
    }
        <#if wg_has_next>,</#if>
    </#list>

]
</#escape>