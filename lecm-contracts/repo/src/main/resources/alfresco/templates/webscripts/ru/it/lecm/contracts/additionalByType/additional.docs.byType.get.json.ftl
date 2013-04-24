<#escape x as x?js_string>
[
    <#list docs as doc>
    {
    "nodeRef": "${doc.getNodeRef().toString()}"
    }
        <#if doc_has_next>,</#if>
    </#list>
]
</#escape>