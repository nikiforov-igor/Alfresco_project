<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		LogicECM.module.Base.Util.loadScripts([
					'/scripts/signed-docflow/applet-user-settings.js'
				], createObject);
	}

	function createObject() {
		new LogicECM.AppletUserSettings("${el}").setMessages(${messages});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${el}-body" class="applet-user-settings">
	<div id="${el}-settings"></div>
</div>
