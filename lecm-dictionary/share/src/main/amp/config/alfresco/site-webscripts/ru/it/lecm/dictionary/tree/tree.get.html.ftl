<#assign id = args.htmlid>

<script type="text/javascript">//<![CDATA[

(function() {
	function initTree() {
		new LogicECM.module.Dictionary.Tree("${id}").setOptions({
			dictionaryName: "${args.dictionaryName}"
		}).setMessages(${messages});
	}

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-dictionary/dictionary-tree.js',
            'scripts/lecm-dictionary/dictionary-utils.js'
        ], [
            'yui/treeview/assets/skins/sam/treeview.css',
            'css/lecm-dictionary/dictionary-tree.css'
        ], initTree);
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