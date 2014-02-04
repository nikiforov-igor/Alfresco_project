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