<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
	new LogicECM.ErrandsSettings("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="errands-settings">
	<div class="yui-g">
		<div class="yui-u first">
			<div class="title">${msg("label.title")}</div>
		</div>
	</div>

	<div id="${el}-settings"></div>
</div>