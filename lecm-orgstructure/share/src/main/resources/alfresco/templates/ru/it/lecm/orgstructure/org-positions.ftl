<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
	<#include "/org/alfresco/components/form/form.get.head.ftl">
	<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-orgstructure/orgstructure-const.js"></@script>
    <script type="text/javascript">//<![CDATA[
        var response = ${response};
        LogicECM.module.OrgStructure.POSITIONS_SETTINGS = response;
        LogicECM.module.OrgStructure.IS_ENGINEER = ${isOrgEngineer?string};
        //]]>
    </script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="positions-grid" scope="template" />
</@bpage.basePage>
