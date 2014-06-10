<!-- Admin Console Application Tool -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-user-settings.css" />
<@script type="text/javascript" src="${url.context}/res/components/console/consoletool.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/errands-user-settings.js"></@script>

<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
	new LogicECM.ErrandsUserSettings("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="errands-user-settings">
	<div id="${el}-settings"></div>
</div>