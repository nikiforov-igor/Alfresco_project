<#import "item.lib.ftl" as itemLib/>
<?xml version='1.0' encoding='UTF-8'?>

<#if items??>
	<items>
		<#list items as item>
			<item name="${item.node.getName()}" nodeRef="${item.node.nodeRef}" type="${item.node.typeShort}" hasChildren="${item.hasChildren?string}">
				<@itemLib.renderProperties item=item/>
			</item>
		</#list>
	</items>
</#if>




