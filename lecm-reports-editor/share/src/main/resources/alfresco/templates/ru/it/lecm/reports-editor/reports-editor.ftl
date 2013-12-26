<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.get.head.ftl">
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-reports-editor/reports-editor-const.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-reports-editor/editor-tree-menu.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-reports-editor/toolbar.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-reports-editor/template-edit/template-editor.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-reports-editor/template-edit/template-editor-toolbar.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-reports-editor/edit-source/edit-source-toolbar.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-reports-editor/select-source/data-source-columns-grid.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-reports-editor/select-source/source-select-editor.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-reports-editor/select-source/data-sources-grid.js"></@script>

    <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/toolbar.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/yui/treeview/assets/skins/sam/treeview.css"/>
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-reports-editor/report-editor.css" />

    <#assign show = true/>
    <#if page.url.args.reportId?? && page.url.args.reportId != "">
        <#assign show = false/>
    </#if>

<script type="text/javascript">//<![CDATA[
    <#if preferences?? && preferences["redirectUrl"]?? && preferences["redirectUrl"] != "">
        if (window.location.href.indexOf("${preferences["redirectUrl"]}") < 0) {
            window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "${preferences["redirectUrl"]}";
        }
    </#if>

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

    <#if page.url.args.reportId?? && page.url.args.reportId != "">
        LogicECM.module.ReportsEditor.SETTINGS.REPORT_PATH =
                LogicECM.module.ReportsEditor.SETTINGS.REPORT_PATH ?
                        LogicECM.module.ReportsEditor.SETTINGS.REPORT_PATH :
                        LogicECM.module.ReportsEditor.REPORT_SETTINGS.path;
    </#if>

    function initReportsEditorResizer() {
        var resizer = new LogicECM.module.Base.Resizer('ReportsEditorResizer');
        resizer.setOptions({
            initialWidth: 300
        });
    }

    YAHOO.util.Event.onDOMReady(initReportsEditorResizer);
//]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<div id="no_menu_page" class="sticky-wrapper">
<@bpage.basePage showTitle=true showToolbar=false showMenu=false>
    <div class="yui-t1" id="editor-with-tree">
        <div id="yui-main-2">
            <div class="yui-b" style="margin-left: 0" id="alf-content">
                <#--<@region id="toolbar" scope="template" />
                <@region id="content" scope="template" />-->
            </div>
        </div>
        <div id="alf-filters" class="tree">
            <div id="reports-editor-tree" class="ygtv-highlight"></div>
            <script type="text/javascript">//<![CDATA[
            (function () {
                function initTree() {
                    new LogicECM.module.ReportsEditor.Tree("reports-editor-tree");
                }

                YAHOO.util.Event.onDOMReady(initTree);
            })();
            //]]></script>
        </div>
    </div>
</@bpage.basePage>
</div>