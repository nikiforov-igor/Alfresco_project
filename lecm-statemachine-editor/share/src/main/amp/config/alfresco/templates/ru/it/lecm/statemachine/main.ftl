<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
    <@documentLibraryJS />
    <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
<!-- Required CSS -->
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/treeview/assets/skins/sam/treeview.css"/>
<!-- Optional CSS for for date editing with Calendar-->
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/calendar/assets/skins/sam/calendar.css"/>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/menu/assets/skins/sam/menu.css"/>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/fonts/fonts-min.css"/>

    <!-- Dependency source file -->
    <script type="text/javascript" src="${url.context}/res/yui/yahoo-dom-event/yahoo-dom-event.js"></script>
    <!-- Optional dependency source file -->
    <@script type="text/javascript" src="${url.context}/res/yui/animation/animation.js"></@script>
    <!-- Optional dependency source file for date editing with Calendar-->
    <@script type="text/javascript" src="${url.context}/res/yui/calendar/calendar.js"></@script>
    <!-- Optional dependency source file to decode contents of yuiConfig markup attribute-->
    <@script type="text/javascript" src="${url.context}/res/yui/json/json.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/yui/menu/menu.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/yui/container/container_core.js"></@script>

    <!-- TreeView source file -->
    <@script type="text/javascript" src="${url.context}/res/yui/treeview/treeview.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/actions.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/yui/element/element.js"></@script>

    <!-- Source file -->
    <script src="${url.context}/res/yui/button/button-min.js"></script>

    <!-- Skin CSS files resize.css must load before layout.css -->
    <link rel="stylesheet" type="text/css" href="${url.context}/res/yui/assets/skins/sam/resize.css">
    <link rel="stylesheet" type="text/css" href="${url.context}/res/yui/assets/skins/sam/layout.css">
    <!-- Utility Dependencies -->
    <@script type="text/javascript" src="${url.context}/res/yui/dragdrop/dragdrop.js"></@script>
    <!-- Optional Resize Support -->
    <@script type="text/javascript" src="${url.context}/res/yui/resize/resize.js"></@script>
    <!-- Source file for the Layout Manager -->
    <@script type="text/javascript" src="${url.context}/res/yui/layout/layout.js"></@script>

	<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-statemachine-editor/main.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-statemachine-editor/menu.js"></@script>
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-statemachine-editor/editor.css">

    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-statemachine-editor/menu.css" />
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-statemachine-editor/menu.js"></@script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
    <#if hasRole>
        <@region id="content" scope="template" />
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
</@bpage.basePage>
