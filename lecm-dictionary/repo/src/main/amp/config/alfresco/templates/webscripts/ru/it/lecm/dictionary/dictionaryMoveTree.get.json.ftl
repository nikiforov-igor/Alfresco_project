<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#list  branch as b>
    {
        "label": "${b.label}",
        "title": "${b.title}",
        "type": "${b.type}",
        "nodeRef": "${b.nodeRef}",
        "isLeaf": ${b.isLeaf},
        "isContainer": ${b.isContainer},
        "hasPermAddChildren": ${b.hasPermAddChildren?string},
        "selectable": ${b.selectable?string}
    }
        <#if b_has_next>,</#if>
    </#list>
]
</#escape>
