<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "packageNodeRef": "${packageNodeRef}",
    "machineNodeRef": "${machineNodeRef}",
    "versionsNodeRef": "${versionsNodeRef}",
    "isFinalizeToUnit": ${isFinalizeToUnit?string},
    "isSimple": ${isSimple?string},
    "statuses": [
    <#list statuses as status>
        <@printStatus status />
        <#if status_has_next>,</#if>
    </#list>
    ]
}

<#macro printStatus status>
{
    "name": "${status.name}",
    "nodeRef": "${status.nodeRef}",
    "type": "${status.type}",
    "forDraft": ${status.forDraft},
    "transitions": [
        <#list status.transitions as transition>
            <@printTransition transition />
            <#if transition_has_next>,</#if>
        </#list>
    ]
}
</#macro>

<#macro printTransition transition>
    {
        "user": ${transition.user?string},
        "exp": "${transition.exp!"null"}",
        "status": "${transition.status}",
        "label": "${transition.label}"
    }
</#macro>

</#escape>
