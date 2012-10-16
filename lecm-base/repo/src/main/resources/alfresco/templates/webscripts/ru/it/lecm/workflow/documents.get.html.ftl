<#escape x as x?js_string>
[
    <#list documents as document>
        {
        nodeRef: "${document.nodeRef}",
        name: "${document.name}",
        status: "${document.status}"
        }
        <#if document_has_next>,</#if>
    </#list>
]
</#escape>