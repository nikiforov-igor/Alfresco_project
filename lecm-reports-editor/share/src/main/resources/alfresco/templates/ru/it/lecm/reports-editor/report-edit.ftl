<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
<#include "/org/alfresco/components/form/form.get.head.ftl">

<script type="text/javascript">//<![CDATA[
LogicECM.module.ReportsEditor.SETTINGS =
    <#if settings?? >
        ${settings}
    <#else>
        {}
    </#if>;
LogicECM.module.ReportsEditor.SETTINGS.DESTINATION =
        LogicECM.module.ReportsEditor.SETTINGS.DESTINATION ? LogicECM.module.ReportsEditor.SETTINGS.DESTINATION : null;
//]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=true>
    <@region id="content" scope="template" />
</@bpage.basePage>