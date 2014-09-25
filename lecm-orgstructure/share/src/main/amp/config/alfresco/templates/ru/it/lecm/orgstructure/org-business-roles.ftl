<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/components/form/form.dependencies.inc">
<script type="text/javascript">//<![CDATA[
    LogicECM.module = LogicECM.module || {};
    LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

    var response = ${response};
    LogicECM.module.OrgStructure.BUSINESS_ROLES_SETTINGS = response;
    LogicECM.module.OrgStructure.IS_ENGINEER = ${isOrgEngineer?string};
//]]>
</script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePageSimple>
	<@region id="roles-grid" scope="template" />
</@bpage.basePageSimple>
