<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
    <@documentLibraryJS />
    <#include "/org/alfresco/components/form/form.get.head.ftl">
    <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
<!-- Required CSS -->
    <link rel="stylesheet" type="text/css" href="${url.context}/yui/treeview/assets/skins/sam/treeview.css"/>
<!-- Optional CSS for for date editing with Calendar-->
    <link rel="stylesheet" type="text/css" href="${url.context}/yui/calendar/assets/skins/sam/calendar.css"/>
    <link rel="stylesheet" type="text/css" href="${url.context}/yui/menu/assets/skins/sam/menu.css"/>
    <link rel="stylesheet" type="text/css" href="${url.context}/yui/fonts/fonts-min.css"/>

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

    <!-- Source file -->
    <script src="${url.context}/yui/button/button-min.js"></script>

    <!-- Skin CSS files resize.css must load before layout.css -->
    <link rel="stylesheet" type="text/css" href="${url.context}/yui/assets/skins/sam/resize.css">
    <link rel="stylesheet" type="text/css" href="${url.context}/yui/assets/skins/sam/layout.css">
    <!-- Utility Dependencies -->
    <@script type="text/javascript" src="${url.context}/yui/dragdrop/dragdrop.js"></@script>
    <!-- Optional Resize Support -->
    <@script type="text/javascript" src="${url.context}/yui/resize/resize.js"></@script>
    <!-- Source file for the Layout Manager -->
    <@script type="text/javascript" src="${url.context}/yui/layout/layout.js"></@script>

    <script type="text/javascript" src="${page.url.context}/scripts/lecm-statemachine-editor/main.js"></script>
    <script type="text/javascript" src="${page.url.context}/scripts/lecm-statemachine-editor/menu.js"></script>
    <link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-statemachine-editor/editor.css">

    <link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-statemachine-editor/menu.css" />
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-statemachine-editor/menu.js"></@script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
    <#if hasRole>
        <@region id="content" scope="template" />
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
</@bpage.basePage>
