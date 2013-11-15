<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
<#include "/org/alfresco/components/form/form.get.head.ftl">

<#assign show = true/>
<#if page.url.args.reportId?? && page.url.args.reportId != "">
    <#assign show = false/>
</#if>

<script type="text/javascript">//<![CDATA[
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
//]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=show>
    <@region id="navigation-toolbar" scope="template" />
    <@region id="reports-editor-toolbar" scope="template" />
    <@region id="content" scope="template" />
</@bpage.basePage>