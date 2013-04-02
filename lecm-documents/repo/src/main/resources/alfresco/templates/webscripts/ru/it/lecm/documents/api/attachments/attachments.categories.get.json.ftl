<#escape x as jsonUtils.encodeJSONString(x)>
{
    "categories": [
        <#list items as item>
            <#if item.category??>
	            {
	                "nodeRef": "${item.category.nodeRef}",
	                "name": "${item.category.name}",
	                "path": "${item.category.displayPath}/${item.category.name}",
                    "isReadOnly": ${item.isReadOnly?string}
	            }<#if item_has_next>,</#if>
            </#if>
        </#list>
    ]
}
</#escape>