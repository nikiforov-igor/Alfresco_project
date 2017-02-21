<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "displayPath": "${resultNode.displayPath}",
    "title": "${resultNode.title}",
    "type": "${resultNode.type}",
    "nodeRef": "${resultNode.nodeRef}",
    "isLeaf": ${resultNode.isLeaf},
    "isContainer": ${resultNode.isContainer},
    "hasPermAddChildren": ${resultNode.hasPermAddChildren?string}
}
</#escape>