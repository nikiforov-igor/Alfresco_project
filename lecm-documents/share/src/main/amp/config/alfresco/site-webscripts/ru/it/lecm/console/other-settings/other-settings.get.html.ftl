<#if allowEdit >

<#assign el=args.htmlid?html>

<div id="${el}-body" class="platform-other-settings">
	<div class="yui-g">
		<div class="yui-u first">
			<div class="title">${msg("label.title")}</div>
		</div>
	</div>

	<div id="${el}-body-settings"></div>
</div>

<script type="text/javascript">//<![CDATA[
(function(){
    function createPage() {
        new LogicECM.module.MiscellaneousSettings("${el}-body").setMessages(${messages});
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'components/form/form.js',
            'components/console/consoletool.js',
            'scripts/lecm-documents/other-settings.js'
        ], [
            'css/lecm-documents/other-settings.css'
        ], createPage);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<#else>
    <#include "/ru/it/lecm/base-share/components/forbidden.get.html.ftl">
</#if>