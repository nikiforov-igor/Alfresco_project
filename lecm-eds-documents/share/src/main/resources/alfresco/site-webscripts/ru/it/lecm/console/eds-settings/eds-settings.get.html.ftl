<#include "/org/alfresco/components/component.head.inc">

<@script type="text/javascript" src="${page.url.context}/res/components/console/consoletool.js"></@script>
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-eds-documents/global-settings.js"></@script>

<!-- Controls dependencies -->
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-eds-documents/components/global-settings/common-script.js" />
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-eds-documents/components/global-settings/potential-role-tree-picker.js" />

<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
	new LogicECM.EdsGlobalSettings("${el}-body").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="eds-global-settings">
	<div class="yui-g">
		<div class="yui-u first">
			<div class="title">${msg("label.title")}</div>
		</div>
	</div>

	<div id="${el}-body-settings"></div>
</div>