<#assign id = args.htmlid>

<script type="text/javascript">//<![CDATA[
function init() {
	new LogicECM.module.ARM.EditNode("${id}").setMessages(${messages});
}

YAHOO.util.Event.onDOMReady(init);
//]]></script>

<div id="${id}-body" class="arm-metadata"></div>
