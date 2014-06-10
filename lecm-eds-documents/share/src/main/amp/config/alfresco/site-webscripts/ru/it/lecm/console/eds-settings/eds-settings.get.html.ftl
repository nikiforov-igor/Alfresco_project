<#include "/org/alfresco/components/component.head.inc">

<!-- Controls dependencies -->
<@script type="text/javascript" src="${url.context}/res/components/form/form.js" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/association-tree/association-tree-view.js" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-eds-documents/components/global-settings/potential-role-tree-picker.js" />

<@script type="text/javascript" src="${url.context}/res/components/console/consoletool.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-eds-documents/global-settings.js"></@script>

<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-eds-documents/global-settings.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/treeview/assets/skins/sam/treeview.css"/>

<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
	(function() {
		new LogicECM.module.EdsGlobalSettings("${el}-body").setMessages(${messages});
	})();
//]]></script>

<div id="${el}-body" class="eds-global-settings">
	<div class="yui-g">
		<div class="yui-u first">
			<div class="title">${msg("label.title")}</div>
		</div>
	</div>

	<div id="${el}-body-settings"></div>
</div>