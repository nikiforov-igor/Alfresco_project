<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-arm/arm-tree-menu.js"></@script>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/light-blue-bgr.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/yui/treeview/assets/skins/sam/treeview.css"/>

<div id="arm-tree" class="ygtv-highlight"></div>
<script type="text/javascript">//<![CDATA[
(function () {
    function initMenu() {
        new LogicECM.module.ARM.TreeMenu("arm-tree");
    }

    YAHOO.util.Event.onDOMReady(initMenu);
})();
//]]></script>