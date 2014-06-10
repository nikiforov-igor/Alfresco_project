<#import "item.lib.ftl" as itemLib/>
<?xml version='1.0' encoding='UTF-8'?>

<#if item??>
	<@itemLib.renderItem item=item/>
</#if>




