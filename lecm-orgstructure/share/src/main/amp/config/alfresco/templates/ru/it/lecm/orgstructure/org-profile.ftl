<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
	<#include "/org/alfresco/components/form/form.dependencies.inc">
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-orgstructure/orgstructure-profile.css" />
    <#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-const.js"></@script>-->
    <script type="text/javascript">//<![CDATA[
    (function() {
        var response = ${response};
        LogicECM.module.OrgStructure.PROFILE_SETTINGS = response;
        LogicECM.module.OrgStructure.IS_ENGINEER = ${isOrgEngineer?string};
    })();
    //]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="organization-profile" scope="template" />
</@bpage.basePage>
