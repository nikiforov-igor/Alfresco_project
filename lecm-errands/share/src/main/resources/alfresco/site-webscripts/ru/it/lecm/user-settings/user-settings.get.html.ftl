<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
	new LogicECM.ErrandsUserSettings("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="errands-user-settings">
	<div id="${el}-settings"></div>
</div>