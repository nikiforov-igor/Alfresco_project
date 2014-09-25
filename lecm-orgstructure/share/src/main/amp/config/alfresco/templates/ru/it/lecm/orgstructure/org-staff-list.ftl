<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<#include "/org/alfresco/components/form/form.dependencies.inc">
<script type="text/javascript">//<![CDATA[
    LogicECM.module = LogicECM.module || {};
    LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

    var response = ${response};
    LogicECM.module.OrgStructure.STAFF_LIST_SETTINGS = response;
    LogicECM.module.OrgStructure.IS_ENGINEER = ${isOrgEngineer?string};
//]]></script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#import "/ru/it/lecm/base-share/components/2-panels-with-splitter.ftl" as panels/>
<@bpage.basePageSimple>
    <div class="yui-t1" id="orgstructure-staff-grid-with-tree">
        <@panels.twoPanels leftPanelId="left-panel-staff" rightPanelId="right-panel-staff">
            <@region id="grid" scope="template" />
        </@panels.twoPanels>
    </div>    
</@bpage.basePageSimple>
