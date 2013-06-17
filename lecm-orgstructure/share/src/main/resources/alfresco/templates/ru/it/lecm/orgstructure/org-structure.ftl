<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
	<#include "/org/alfresco/components/form/form.get.head.ftl">
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-orgstructure/orgstructure-const.js"></@script>
    <script type="text/javascript">//<![CDATA[
        var response = ${response};
        LogicECM.module.OrgStructure.STRUCTURE_SETTINGS = response;
        LogicECM.module.OrgStructure.IS_ENGINEER = ${isOrgEngineer?string};
        function init() {
            new LogicECM.module.Base.Resizer('OrgstructureResizer');
        }

        YAHOO.util.Event.onDOMReady(init);
    //]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="tree-and-grid" scope="template" />
</@bpage.basePage>
