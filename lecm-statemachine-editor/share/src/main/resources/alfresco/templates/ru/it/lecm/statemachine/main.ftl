<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
    <@documentLibraryJS />
    <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
<!-- Required CSS -->
    <@link rel="stylesheet" type="text/css" href="${url.context}/yui/treeview/assets/skins/sam/treeview.css"/>
<!-- Optional CSS for for date editing with Calendar-->
    <@link rel="stylesheet" type="text/css" href="${url.context}/yui/calendar/assets/skins/sam/calendar.css"/>
    <@link rel="stylesheet" type="text/css" href="${url.context}/yui/menu/assets/skins/sam/menu.css"/>
    <@link rel="stylesheet" type="text/css" href="${url.context}/yui/fonts/fonts-min.css"/>

    <!-- Dependency source file -->
    <script type="text/javascript" src="${url.context}/yui/yahoo-dom-event/yahoo-dom-event.js"></script>
    <!-- Optional dependency source file -->
    <@script type="text/javascript" src="${url.context}/yui/animation/animation.js"></@script>
    <!-- Optional dependency source file for date editing with Calendar-->
    <@script type="text/javascript" src="${url.context}/yui/calendar/calendar.js"></@script>
    <!-- Optional dependency source file to decode contents of yuiConfig markup attribute-->
    <@script type="text/javascript" src="${url.context}/yui/json/json.js"></@script>
    <@script type="text/javascript" src="${url.context}/yui/menu/menu.js"></@script>
    <@script type="text/javascript" src="${url.context}/yui/container/container_core.js"></@script>

    <!-- TreeView source file -->
    <@script type="text/javascript" src="${url.context}/yui/treeview/treeview.js"></@script>
    <@script type="text/javascript" src="${url.context}/js/documentlibrary-actions.js"></@script>
    <@script type="text/javascript" src="${url.context}/yui/element/element.js"></@script>
    <#include "/org/alfresco/components/documentlibrary/documentlist.get.head.ftl" />

    <!-- Source file -->
    <script src=""${url.context}/yui/button/button-min.js"></script>

    <link rel="stylesheet" type="text/css" href="${url.context}/yui/reset-fonts-grids/reset-fonts-grids.css">
    <!-- Skin CSS files resize.css must load before layout.css -->
    <link rel="stylesheet" type="text/css" href="${url.context}/yui/assets/skins/sam/resize.css">
    <link rel="stylesheet" type="text/css" href="${url.context}/yui/assets/skins/sam/layout.css">
    <!-- Utility Dependencies -->
    <@script type="text/javascript" src="${url.context}/yui/dragdrop/dragdrop.js"></@script>
    <!-- Optional Resize Support -->
    <@script type="text/javascript" src="${url.context}/yui/resize/resize.js"></@script>
    <!-- Source file for the Layout Manager -->
    <@script type="text/javascript" src="${url.context}/yui/layout/layout.js"></@script>

    <script type="text/javascript" src="${page.url.context}/scripts/statemachine/editor/main.js"></script>
    <link rel="stylesheet" type="text/css" href="${page.url.context}/css/statemachine/editor.css">
</@>

<@templateBody>
<div id="alf-hd">
    <@region id="header" scope="global"/>
    <@region id="title" scope="template"/>
</div>
<div>
    <@region id="content" scope="template"/>
</div>
</@>

<@templateFooter>
<div id="alf-ft">
    <@region id="footer" scope="global"/>
</div>
</@>
