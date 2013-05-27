<#escape x as x?js_string>
[
    <#list docs as doc>
    {
	    "nodeRef": "${doc.nodeRef}",
	    "hasMyActiveTasks": "${doc.hasMyActiveTasks?string}"
    }
        <#if doc_has_next>,</#if>
    </#list>
]
</#escape>