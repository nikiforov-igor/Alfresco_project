<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.dependencies.inc">
    <#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-reports-editor/reports-editor-const.js"></@script>-->
	<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"/>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-reports-editor/editor-tree-menu.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-reports-editor/toolbar.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-reports-editor/template-edit/template-editor-toolbar.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-reports-editor/edit-source/source-edit-toolbar.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-reports-editor/select-source/data-source-columns-grid.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-reports-editor/select-source/source-select-editor.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-reports-editor/select-source/data-sources-grid.js"></@script>

    <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/treeview/assets/skins/sam/treeview.css"/>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-reports-editor/report-editor.css" />

<script type="text/javascript">//<![CDATA[
(function() {
    LogicECM.module.ReportsEditor.SETTINGS =
        <#if settings?? >
            ${settings}
        <#else>
            {}
        </#if>;

    LogicECM.module.ReportsEditor.REPORT_SETTINGS =
        <#if reportSettings?? >
            ${reportSettings}
        <#else>
            {}
        </#if>;
    })();

    LogicECM.currentUser = <#if currentUser?? >'${currentUser}'<#else>null</#if>;

//]]></script>
</@>

<#import "/ru/it/lecm/base-share/components/2-panels-with-splitter.ftl" as panels/>

<div id="no_menu_page" class="sticky-wrapper">
<@bpage.basePage showTitle=true showToolbar=false showMenu=false>
    <div class="yui-t1" id="editor-with-tree">
        <@panels.twoPanels initialWidth=300 leftRegions=[] leftPanelId="reports-editor-tree-panel" rightPanelId="reports-editor-panel">

        </@panels.twoPanels>
    </div>
    <script type="text/javascript">//<![CDATA[
    (function () {
        function initTree() {
            Dom.get('reports-editor-tree-panel').innerHTML='<div id="reports-editor-tree" class="ygtv-highlight"></div>';

            new LogicECM.module.ReportsEditor.Tree("reports-editor-tree");
        }

        YAHOO.util.Event.onDOMReady(initTree);
    })();
    //]]></script>



</@bpage.basePage>
</div>