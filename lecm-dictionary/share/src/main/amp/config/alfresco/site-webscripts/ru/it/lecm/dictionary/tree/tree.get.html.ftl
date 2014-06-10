<@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/treeview/assets/skins/sam/treeview.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-dictionary/dictionary-tree.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-dictionary/dictionary-tree.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-dictionary/dictionary-utils.js"></@script>


<#assign id = args.htmlid>

<script type="text/javascript">//<![CDATA[

(function() {
	function init() {
		new LogicECM.module.Dictionary.Tree("${id}").setOptions({
			dictionaryName: "${args.dictionaryName}"
		}).setMessages(${messages});
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