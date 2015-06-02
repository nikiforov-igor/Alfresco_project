<#escape x as jsonUtils.encodeJSONString(x)>
{
    "stages": [
        <#list stages as stage>
            {
                "nodeRef": "${stage.node.nodeRef}",
                "title": "${stage.node.title}",
                "type": "${stage.node.type}",
                "decision": {
                    "value": "${stage.node.decision.value}",
                    "displayValue": "${stage.node.decision.displayValue}"
                },
                "term": "${stage.node.term}",
                "items": [
                    <#list stage.items as item>
                    {
                        "employee": "${item.employee}",
                        "dueDate": "<#if item.dueDate??>${item.dueDate?string("dd.MM.yyy")}</#if>",
                        "state": "${item.state}"
                    }<#if item_has_next>,</#if>
                    </#list>
                ]
            }<#if stage_has_next>,</#if>
        </#list>
    ]
}
</#escape>
