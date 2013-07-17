<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
	new LogicECM.NotificationsUserSettings("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="notifications-user-settings">
	<div id="${el}-settings"></div>
</div>