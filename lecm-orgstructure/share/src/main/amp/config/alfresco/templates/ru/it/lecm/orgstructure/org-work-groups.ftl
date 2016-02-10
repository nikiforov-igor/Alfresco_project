<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<#include "/org/alfresco/components/form/form.dependencies.inc">

<script type="text/javascript">//<![CDATA[
    LogicECM.module = LogicECM.module || {};
    LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};
    var response = ${response};
    LogicECM.module.OrgStructure.WORK_GROUPS_SETTINGS = response;
<#--     LogicECM.module.OrgStructure.IS_ENGINEER = ${isOrgEngineer?string}; -->
//]]></script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#import "/ru/it/lecm/base-share/components/2-panels-with-splitter.ftl" as panels/>
<#-- <#assign hasPermission = isOrgEngineer/> -->
<@bpage.basePageSimple showToolbar=hasPermission>
<#--     <#if hasPermission> -->
        <div class="yui-t1" id="orgstructure-work-groups">
            <@panels.twoPanels initialWidth=500 leftRegions=["groups-toolbar","groups-grid"] leftPanelId="left-panel-workgroups" rightPanelId="right-panel-workgroup">
                    <@region id="workforces-toolbar" scope="template" />
                    <@region id="workforces-grid" scope="template" />
                </@panels.twoPanels>
        </div>
<#--
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
-->
</@bpage.basePageSimple>
