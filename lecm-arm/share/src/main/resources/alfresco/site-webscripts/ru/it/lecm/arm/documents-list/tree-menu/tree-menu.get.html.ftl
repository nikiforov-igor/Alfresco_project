<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-arm/arm-tree-menu.js"></@script>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/light-blue-bgr.css" />

<#assign id = args.htmlid>
<div id="${id}-arm-tree" class="ygtv-highlight arm-tree-menu">
	<ul id="${id}-headlines" class="arm-accordion dynamic"></ul>
</div>
<script type="text/javascript">//<![CDATA[
(function () {
    function initMenu() {
        new LogicECM.module.ARM.TreeMenu("${id}");
    }

    YAHOO.util.Event.onDOMReady(initMenu);
})();
//]]></script>