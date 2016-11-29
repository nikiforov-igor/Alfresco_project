<#escape x as jsonUtils.encodeJSONString(x)>
{
    <#if arm??>
    "nodeRef": "${arm.nodeRef}",
    "name": "${arm.name}",
    "showCalendar": <#if arm.properties['lecm-arm:show-calendar']??>${arm.properties['lecm-arm:show-calendar']?string}<#else>false</#if>,
    "showCreateButton": <#if arm.properties['lecm-arm:show-create-button']??>${arm.properties['lecm-arm:show-create-button']?string}<#else>true</#if>
    </#if>
}
</#escape>