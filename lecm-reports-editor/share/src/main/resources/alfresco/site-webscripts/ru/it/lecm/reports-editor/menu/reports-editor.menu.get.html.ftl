<#assign id = args.htmlid,
selected = args.selected!"reportsList"/>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#if page.url.args.reportId??>

    <@comp.baseMenu>
        <@comp.baseMenuButton "reportsList" msg('lecm.reports-editor.reports.btn') selected/>
        <@comp.baseMenuButton "reportSettings" msg('lecm.reports-editor.report-info.btn') selected/>
        <@comp.baseMenuButton "editDataSource" msg('lecm.reports-editor.editSource.btn') selected/>
        <#if !reportSettings?? || (reportSettings?? && reportSettings.isSubReport == "false")>
            <@comp.baseMenuButton "subReports" msg('lecm.reports-editor.subReports.btn') selected/>
        </#if>
        <@comp.baseMenuButton "editTemplate" msg('lecm.reports-editor.editTemplate.btn') selected/>
        <#if !reportSettings?? || (reportSettings?? && reportSettings.isSubReport == "false")>
            <@comp.baseMenuButton "deployReport" msg('lecm.reports-editor.deploy.btn') selected/>
        </#if>
    </@comp.baseMenu>

<script type="text/javascript">//<![CDATA[
(function () {
    function init() {
        var menu = new LogicECM.module.ReportsEditor.ReportMenu("menu-buttons");
        menu.setMessages(${messages});
        menu.setReportId("${page.url.args.reportId}");
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
<#else>
    <@comp.baseMenu>
        <@comp.baseMenuButton "reportsList" msg('lecm.reports-editor.reports.btn') selected/>
        <@comp.baseMenuButton "templatesList" msg('lecm.reports-editor.templates.btn') selected/>
        <@comp.baseMenuButton "sourcesList" msg('lecm.reports-editor.sources.btn') selected/>
        <@comp.baseMenuButton "reportProviders" msg('lecm.reports-editor.reportProviders.btn') selected/>
    </@comp.baseMenu>

<script type="text/javascript">//<![CDATA[
(function () {
    function init() {
        var menu = new LogicECM.module.ReportsEditor.MainMenu("menu-buttons");
        menu.setMessages(${messages});
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
</#if>


