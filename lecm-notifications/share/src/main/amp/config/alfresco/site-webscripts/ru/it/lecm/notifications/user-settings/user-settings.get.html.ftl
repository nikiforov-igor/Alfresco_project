<#assign el=args.htmlid?html>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		LogicECM.module.Base.Util.loadResources([
					'/scripts/lecm-notifications/notifications-user-settings.js'
				],
				[
					'css/lecm-notifications/notifications-user-settings.css'
				], createObject);
	}

	function createObject() {
		new LogicECM.NotificationsUserSettings("${el}").setMessages(${messages});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${el}-body" class="notifications-user-settings">
	<div id="${el}-settings"></div>
</div>