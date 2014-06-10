<!-- Admin Console Application Tool -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-notifications/notifications-user-settings.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-notifications/notifications-user-settings.js"></@script>

<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
	new LogicECM.NotificationsUserSettings("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="notifications-user-settings">
	<div id="${el}-settings"></div>
</div>