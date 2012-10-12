<#escape x as x?js_string>
[
    <#list  branch as b>
        {
            title: "${b.title}",
            type: "${b.type}",
            nodeRef: "${b.nodeRef}",
            isLeaf: ${b.isLeaf},
            dsUri: "${b.dsUri!""}",
            childType: "${b.childType!""}",
            childAssoc: "${b.childAssoc!""}",
        }
        <#if b_has_next>,</#if>
    </#list>
]
</#escape>