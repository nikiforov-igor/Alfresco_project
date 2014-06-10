<#escape x as x?js_string>
    {
        <#if root??>
            nodeRef: "${root.nodeRef!"NOT_LOAD"}",
            itemType: "${root.itemType!""}"
        </#if>
    }
</#escape>