<#include "/org/alfresco/include/alfresco-template.ftl"/>
<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
	<#include "/org/alfresco/components/form/form.get.head.ftl">
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-orgstructure/orgstructure-const.js"></@script>
    <script type="text/javascript">//<![CDATA[
        var response = ${response};
        LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS = response;
        LogicECM.module.OrgStructure.IS_ENGINEER = ${isOrgEngineer?string};
    //]]>
    </script>
</@>
<@templateHeader>
<#-- Скрипты, необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.get.head.ftl">

</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage showToolbar=showContent>
                <@region id="employees-grid" scope="template" />
		

</@bpage.basePage>

