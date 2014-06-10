<#import "/org/alfresco/slingshot/documentlibrary-v2/item.lib.ftl" as itemLib />
<#escape x as jsonUtils.encodeJSONString(x)>
{
	"metadata": {},
	"items":
		[
			<#list items as item>
			{
				"isInnerAttachment": "${item.isInnerAttachment?string}",
				"node": <#noescape>${item.nodeJSON}</#noescape>,
				<#if item.parent??>"parent": <#noescape>${item.parent.nodeJSON},</#noescape>
				<#else>
					"parent":
					{
						"nodeRef": ""
					},
				</#if>
				<@itemLib.itemJSON item=item />
			}<#if item_has_next>,</#if>
			</#list>
		]
}
</#escape>