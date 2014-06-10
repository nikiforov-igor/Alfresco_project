<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
	<#include "/org/alfresco/components/form/form.dependencies.inc">
	<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-orgstructure/orgstructure-work-groups.css" />

<!-- Advanced Search -->
	<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/search/search.css" />

	<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/workgroup-datagrid.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/workforce-datagrid.js"></@script>

<!-- Historic Properties Viewer -->
<#--загрузка base-utils.js вынесена в base-share-config-custom.xml-->
	<#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>-->
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/versions.js"></@script>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/historic-properties-viewer.css" />
    <#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-const.js"></@script>-->
    <script type="text/javascript">//<![CDATA[
    (function() {
        var response = ${response};
        LogicECM.module.OrgStructure.WORK_GROUPS_SETTINGS = response;
        LogicECM.module.OrgStructure.IS_ENGINEER = ${isOrgEngineer?string};
    })();
    //]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#import "/ru/it/lecm/base-share/components/2-panels-with-splitter.ftl" as panels/>
<@bpage.basePage showToolbar=false>
	<div class="yui-t1" id="orgstructure-work-groups">
            <@panels.twoPanels initialWidth=500 leftRegions=["groups-toolbar","groups-grid"]>
                <@region id="workforces-toolbar" scope="template" />
                <@region id="workforces-grid" scope="template" />
            </@panels.twoPanels>
	</div>
</@bpage.basePage>
