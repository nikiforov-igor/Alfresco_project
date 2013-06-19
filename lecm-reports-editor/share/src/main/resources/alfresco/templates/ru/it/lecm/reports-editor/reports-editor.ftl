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
//]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=show>
    <@region id="content" scope="template" />
</@bpage.basePage>