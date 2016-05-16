{
    "result": {
        <#list result?keys as node>
            "${node}": ${result[node]?string}
            <#if node_has_next>,</#if>
        </#list>
    }
}