<#escape x as x?js_string>
[
    <#list  branch as b>
        {
            title: "${b.title}",
            label: "${b.label}",
            type: "${b.type}",
            nodeRef: "${b.nodeRef}",
            isLeaf: ${b.isLeaf}
        }
        <#if b_has_next>,</#if>
    </#list>
]
</#escape>