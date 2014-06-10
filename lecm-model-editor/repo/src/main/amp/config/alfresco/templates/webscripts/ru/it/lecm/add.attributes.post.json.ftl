<#escape x as (x!"")?js_string>
[
	<#if results??>
	    <#list results as node>
	    {
	        "nodeRef": "${node.nodeRef}"
	    }<#if node_has_next>,</#if>
	    </#list>
	</#if>
]
</#escape>