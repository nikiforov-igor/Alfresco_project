<#escape x as jsonUtils.encodeJSONString(x)!''>
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