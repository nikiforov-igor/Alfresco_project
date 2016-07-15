<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@bpage.templateHeader "transitional">
	<#include "/org/alfresco/components/form/form.dependencies.inc">
    <#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-const.js"></@script>-->
    <script type="text/javascript">//<![CDATA[
    (function() {
        var response = ${response};
        LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS = response;
        LogicECM.module.OrgStructure.IS_ENGINEER = ${isOrgEngineer?string};
    })();
    //]]>
    </script>
</@>

<@bpage.basePage showToolbar=showContent>
    <@region id="employees-grid" scope="template" />
</@bpage.basePage>

