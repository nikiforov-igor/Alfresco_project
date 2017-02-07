<#if allowEdit >
<#assign el=args.htmlid?html>

<script type="text/javascript">
(function(){

	function init() {
		LogicECM.module.Base.Util.loadResources([
			'scripts/lecm-os/os-settings.js'
		], [
            'css/lecm-base/global-settings.css'
        ], function() {
			new LogicECM.module.LecmOSSettings('${el}').setMessages(${messages});
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
</script>


<div id="${el}-body" class="global-settings os-global-settings">
	<div class="yui-g">
		<div class="yui-u first">
			<div class="title">${msg('label.title')}</div>
		</div>
	</div>

	<div id="${el}-settings"></div>
</div>
<#else>
	<#include "/ru/it/lecm/base-share/components/forbidden.get.html.ftl">
</#if>
