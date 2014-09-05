<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<#include "/org/alfresco/components/form/form.dependencies.inc">
<#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-const.js"></@script>-->
<script type="text/javascript">//<![CDATA[
    if (typeof LogicECM == "undefined" || !LogicECM) {
        var LogicECM = {};
    }
    LogicECM.module = LogicECM.module || {};
    LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};    var response = ${response};
    LogicECM.module.OrgStructure.WORK_ROLES_SETTINGS = response;
    LogicECM.module.OrgStructure.IS_ENGINEER = ${isOrgEngineer?string};
//]]></script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePageSimple>
	<@region id="roles-grid" scope="template" />
</@bpage.basePageSimple>
