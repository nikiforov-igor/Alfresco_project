<!-- Admin Console Application Tool -->
<@script type="text/javascript" src="${url.context}/res/scripts/signed-docflow/applet-user-settings.js"></@script>

<#assign el=args.htmlid?html>
<script type="text/javascript">
(function() {
	new LogicECM.AppletUserSettings("${el}").setMessages(${messages});
});
//]]></script>

<div id="${el}-body" class="applet-user-settings">
	<div id="${el}-settings"></div>
</div>
