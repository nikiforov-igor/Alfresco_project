<#include "/org/alfresco/components/component.head.inc">
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-arm/arm-tree-menu.js"></@script>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-arm/lecm-arm-tree-menu.css" />
</@>

<#assign id = args.htmlid>
<div id="${id}-arm-tree" class="ygtv-highlight arm-tree-menu">
	<ul id="${id}-headlines" class="arm-accordion">
		<li class="wait-container"><div class="wait"></div></li>
	</ul>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
    function initMenu() {
        new LogicECM.module.ARM.TreeMenu("${id}");
    }

    YAHOO.util.Event.onDOMReady(initMenu);
})();
//]]></script>