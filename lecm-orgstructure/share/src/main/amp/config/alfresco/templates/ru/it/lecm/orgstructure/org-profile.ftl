<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<#include "/org/alfresco/components/form/form.dependencies.inc">
<script type="text/javascript">//<![CDATA[
    LogicECM.module = LogicECM.module || {};
    LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};
    var response = ${response};
    LogicECM.module.OrgStructure.PROFILE_SETTINGS = response;
    LogicECM.module.OrgStructure.IS_ENGINEER = ${isOrgEngineer?string};
//]]></script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#assign hasPermission = isOrgEngineer/>
<@bpage.basePageSimple showToolbar=hasPermission>
    <#if hasPermission>
        <@region id="organization-profile" scope="template"/>
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
</@bpage.basePageSimple>
