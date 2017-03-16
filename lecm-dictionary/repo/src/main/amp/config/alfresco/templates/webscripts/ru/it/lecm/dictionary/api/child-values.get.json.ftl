<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<#if children??>
	    <#list children as child>
	        {
	            "name": "${child.getName()}",
	            "nodeRef": "${child.getNodeRef()}"
	        }
	        <#if child_has_next>,</#if>
	    </#list>
	</#if>
]
</#escape>



