<#if item??>
	<#assign id = args.htmlid?html>
	<#assign displayName = (item.displayName!item.fileName)?html>

	<div id="${id}">
		<script type="text/javascript">//<![CDATA[
		(function () {

			function init() {
				new window.LogicECM.DocumentAttachmentsDND("doc-attach-right").setOptions(
						{
							nodeRef: "${nodeRef}",
							fileName: "${displayName}",
							versionLabel: "${item.version}"
						}).setMessages(${messages});
			}

			YAHOO.util.Event.onDOMReady(init);
		})();
		//]]>
		</script>
	</div>
</#if>