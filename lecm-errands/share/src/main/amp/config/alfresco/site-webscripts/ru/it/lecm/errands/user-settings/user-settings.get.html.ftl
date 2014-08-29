<#assign el=args.htmlid?html>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		LogicECM.module.Base.Util.loadResources([
					'/scripts/lecm-errands/errands-user-settings.js'
				],
				[
					'css/lecm-errands/errands-user-settings.css'
				], createObject);
	}

	function createObject() {
		new LogicECM.ErrandsUserSettings("${el}").setMessages(${messages});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${el}-body" class="errands-user-settings">
	<div id="${el}-settings"></div>
</div>