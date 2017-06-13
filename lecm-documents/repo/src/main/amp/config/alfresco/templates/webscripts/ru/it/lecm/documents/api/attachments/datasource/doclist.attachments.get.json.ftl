<#import "/org/alfresco/slingshot/documentlibrary-v2/item.lib.ftl" as itemLib />
<#escape x as jsonUtils.encodeJSONString(x)>
{
	"isComplex": ${isComplex?string},
	"metadata": {},
	"items":
		[
			<#list items as item>
			{
				"isInnerAttachment": "${item.isInnerAttachment?string}",
				"meta": {
					"category": "${item.meta.category}",
					"document": "${item.meta.document}"
				},
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