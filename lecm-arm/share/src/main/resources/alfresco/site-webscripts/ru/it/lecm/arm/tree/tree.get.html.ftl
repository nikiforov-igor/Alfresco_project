<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-dictionary/dictionary-tree.js"></@script>
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-dictionary/dictionary-utils.js"></@script>
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-arm/arm-tree.js"></@script>

<#assign id = args.htmlid>

<script type="text/javascript">//<![CDATA[

(function() {
	function init() {
		new LogicECM.module.ARM.Tree("${id}").setMessages(${messages});
	}

	//once the DOM has loaded, we can go ahead and set up our tree:
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${id}-body" class="datalists tree">
	<div id="${id}-headerBar" class="header-bar toolbar flat-button theme-bg-2">
		<div class="left"></div>
	</div>
	<div id="dictionary" class="ygtv-highlight"></div>
</div>