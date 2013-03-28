<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
	<#include "/org/alfresco/components/form/form.get.head.ftl">
	<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-orgstructure/orgstructure-const.js"></@script>
    <script type="text/javascript">//<![CDATA[
        var response = ${response};
        LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS = response;
    //]]>
    </script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="employees-grid" scope="template" />
</@bpage.basePage>
