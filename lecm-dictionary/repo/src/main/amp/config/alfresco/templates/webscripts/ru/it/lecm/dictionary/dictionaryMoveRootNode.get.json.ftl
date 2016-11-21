<#escape x as x?js_string>
{
    "displayPath": "${resultNode.displayPath}",
    "title": "${resultNode.title}",
    "type": "${resultNode.type}",
    "nodeRef": "${resultNode.nodeRef}",
    "isLeaf": ${resultNode.isLeaf},
    "isContainer": ${resultNode.isContainer},
    "hasPermAddChildren": ${resultNode.hasPermAddChildren?string},
    "selectable": ${resultNode.selectable?string}
}
</#escape>