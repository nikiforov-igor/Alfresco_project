<#if allowEdit >
<#include "/org/alfresco/components/component.head.inc">

<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
(function(){
    function createPage() {
        new LogicECM.module.EdsGlobalSettings("${el}-body").setMessages(${messages});
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-base/components/association-tree/association-tree-view.js',
            'scripts/lecm-eds-documents/components/global-settings/potential-role-tree-picker.js',
            'components/form/form.js',
            'components/console/consoletool.js',
            'scripts/lecm-eds-documents/global-settings.js'
        ], [
            'css/lecm-base/global-settings.css',
            'yui/treeview/assets/skins/sam/treeview.css'
        ], createPage);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${el}-body" class="global-settings eds-global-settings">
	<div class="yui-g">
		<div class="yui-u first">
			<div class="title">${msg("label.title")}</div>
		</div>
	</div>

	<div id="${el}-body-settings"></div>
</div>
<#else>
    <#include "/ru/it/lecm/base-share/components/forbidden.get.html.ftl">
</#if>
