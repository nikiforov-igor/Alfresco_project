<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#include "/org/alfresco/components/form/form.dependencies.inc">

<script type="text/javascript">//<![CDATA[
LogicECM.module = LogicECM.module || {};
LogicECM.module.DutiesReassign = LogicECM.module.DutiesReassign || {};
//]]>
</script>

<@bpage.basePageSimple showToolbar=reassignIsEnabled>
    <#if reassignIsEnabled>
        <@region id="datagrid" scope="template" />
        <@region id="reassign-actions" scope="template" />
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
</@bpage.basePageSimple>