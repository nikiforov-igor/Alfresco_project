<#escape x as x?js_string>
[
    <#list  roots as b>
        {
            page: "${b.page}",
            nodeRef: "${b.nodeRef}",
            itemType: "${b.itemType}",
            namePattern: "${b.pattern}"
        }
        <#if b_has_next>,</#if>
    </#list>
]
</#escape>