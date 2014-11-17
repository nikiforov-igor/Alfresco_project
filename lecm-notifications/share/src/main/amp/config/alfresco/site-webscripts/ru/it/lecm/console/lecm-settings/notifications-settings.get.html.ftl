<#if allowEdit >
<#assign el=args.htmlid?html>

<script type="text/javascript">//<![CDATA[
(function(){
    function createPage() {
        new LogicECM.module.NotificationsGlobalSettings("${el}-body").setMessages(${messages});
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'components/console/consoletool.js',
            'scripts/lecm-notifications/notifications-global-settings.js',
            'components/form/form.js'
        ], [
            'css/lecm-notifications/notifications-global-settings.css'
        ], createPage);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${el}-body" class="notifications-settings">
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