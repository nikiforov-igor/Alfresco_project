<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
	<#include "/org/alfresco/components/form/form.dependencies.inc">
	<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"/>
<#--загрузка base-utils.js вынесена в base-share-config-custom.xml-->
    <#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>-->
    <#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-const.js"></@script>-->
    <script type="text/javascript">//<![CDATA[
	(function(){
        var response = ${response};
        LogicECM.module.OrgStructure.BUSINESS_ROLES_SETTINGS = response;
        LogicECM.module.OrgStructure.IS_ENGINEER = ${isOrgEngineer?string};
	})();
    //]]>
    </script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="roles-grid" scope="template" />
</@bpage.basePage>
