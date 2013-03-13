<#escape x as jsonUtils.encodeJSONString(x)>
{
	"hasNext": "${hasNext?string}",
    "items": [
        <#list items as item>
            {
                <#if item.category??>
	                category: {
		                "nodeRef": "${item.category.nodeRef}",
		                "name": "${item.category.name}"
	                },
                </#if>
                attachments: [
	                <#list item.attachments as attachment>
		                {
			                "nodeRef": "${attachment.nodeRef}",
			                "name": "${attachment.name}"
		                }<#if attachment_has_next>,</#if>
	                </#list>
                ]
            }<#if item_has_next>,</#if>
        </#list>
    ]
}
</#escape>