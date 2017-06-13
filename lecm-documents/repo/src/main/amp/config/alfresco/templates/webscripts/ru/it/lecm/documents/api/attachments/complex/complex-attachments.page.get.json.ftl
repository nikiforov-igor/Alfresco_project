{
    "nodes":
        [
            <#list nodes as node>
                ${node}
            <#if node_has_next>,</#if>
            </#list>
        ],
    "totalPageNum" : "${totalPageNum}",
    "folderNodeRef" : "${folderNodeRef}"
}
