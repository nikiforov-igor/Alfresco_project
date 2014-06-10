<!-- Admin Console Application Tool -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-settings.css" />
<@script type="text/javascript" src="${url.context}/res/components/form/form.js" />
<@script type="text/javascript" src="${url.context}/res/components/console/consoletool.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/errands-settings.js"></@script>

<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
	(function(){
		new LogicECM.module.ErrandsSettings("${el}-body").setMessages(${messages});
	})();
//]]></script>

<div id="${el}-body" class="errands-settings">
	<div class="yui-g">
		<div class="yui-u first">
			<div class="title">${msg("label.title")}</div>
		</div>
	</div>

	<div id="${el}-body-settings"></div>
</div>