<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
    <@documentLibraryJS />
<script type="text/javascript">//<![CDATA[
new Alfresco.widget.Resizer("Orgstructure");
//]]></script>

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

    <#include "/org/alfresco/components/documentlibrary/documentlist.get.head.ftl" />

    <@script type="text/javascript" src="${page.url.context}/res/ru/it/lecm/orgstructure/orgstructure.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/res/ru/it/lecm/orgstructure/orgstructure-datagrid.js"></@script>
</@>

<@templateBody>
<div id="alf-hd">
    <@region id="header" scope="global"/>
    <@region id="title" scope="template"/>
</div>
<div id="bd">

    <div class="yui-t1" id="alfresco-orgstructure">
        <div id="yui-main">
            <div class="yui-b" id="alf-content">
            <#--<@region id="toolbar" scope="template" />-->
            <@region id="datagrid" scope="template" />
            </div>
        </div>
        <div class="yui-b" id="alf-filters">
            <@region id="column" scope="template"/>
        <#--<@region id="datalists" scope="template" />-->
               <#--<@region id="filter" scope="template" />-->
        </div>
    </div>
</div>
</@>

<@templateFooter>
<div id="alf-ft">
    <@region id="footer" scope="global"/>
</div>
</@>