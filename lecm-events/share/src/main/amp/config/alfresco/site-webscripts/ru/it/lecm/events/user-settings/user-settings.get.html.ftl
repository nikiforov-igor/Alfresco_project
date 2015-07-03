<#assign el=args.htmlid?html>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		LogicECM.module.Base.Util.loadResources([
					'/scripts/lecm-events/events-user-settings.js'
				],
				[
					'css/lecm-events/events-user-settings.css'
				], createObject);
	}

	function createObject() {
		new LogicECM.EventsUserSettings("${el}").setMessages(${messages});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${el}-body" class="events-user-settings">
	<div id="${el}-settings"></div>
</div>