<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#include "/org/alfresco/components/form/form.dependencies.inc">

<script type="text/javascript">//<![CDATA[
LogicECM.module = LogicECM.module || {};
LogicECM.module.DutiesReassign = LogicECM.module.DutiesReassign || {};
//]]>
</script>

<@bpage.basePageSimple showToolbar=true>
    <@region id="datagrid" scope="template" />
    <@region id="reassign-actions" scope="template" />
</@bpage.basePageSimple>
