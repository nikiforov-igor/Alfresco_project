<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
	new LogicECM.NotificationsGlobalSettings("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="notifications-settings">
	<div class="yui-g">
		<div class="yui-u first">
			<div class="title">${msg("label.title")}</div>
		</div>
	</div>

	<div id="${el}-settings"></div>
</div>