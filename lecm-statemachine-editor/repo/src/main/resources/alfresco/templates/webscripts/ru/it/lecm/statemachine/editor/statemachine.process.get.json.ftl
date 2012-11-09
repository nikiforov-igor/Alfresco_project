<#escape x as x?js_string>
{
    packageNodeRef: "${packageNodeRef}",
    statuses: [
    <#list statuses as status>
        {
            name: "${status.name}",
            actions: [
                {
                    action: "Action 1",
                    transitions: [
                        "Trans1",
                        "Trans2",
                        "Trans3"
                    ]
                },
                {
                    action: "Action 2",
                    transitions: [
                        "Trans11",
                        "Trans12",
                        "Trans13"
                    ]
                }
            ]
        }
        <#if status_has_next>,</#if>
    </#list>
    ]
}
</#escape>
