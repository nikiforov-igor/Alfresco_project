<#escape x as x?js_string>
[
    <#list  roots as b>
        {
            page: "${b.page}",
            nodeRef: "${b.nodeRef}",
            itemType: "${b.itemType}",
            namePattern: "${b.pattern}",
            fullDelete:${b.deleteNode}
        }
        <#if b_has_next>,</#if>
    </#list>
]
</#escape>