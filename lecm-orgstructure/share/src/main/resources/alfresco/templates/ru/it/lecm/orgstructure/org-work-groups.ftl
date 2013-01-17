<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
	<#include "/org/alfresco/components/form/form.get.head.ftl">
	<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-base/components/utils/generate-custom-name.js"></@script>
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-orgstructure/orgstructure-work-groups.css" />

<!-- Advanced Search -->
	<@script type="text/javascript" src="${page.url.context}/res/components/form/date-range.js"></@script>
	<@script type="text/javascript" src="${page.url.context}/res/components/form/number-range.js"></@script>
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/search/search.css" />

	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-orgstructure/workgroup-datagrid.js"></@script>
	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-orgstructure/workforce-datagrid.js"></@script>

<!-- Historic Properties Viewer -->
	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-base/components/versions.js"></@script>
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/document-details/historic-properties-viewer.css" />

    <script type="text/javascript">//<![CDATA[
        function init() {
            var resizer = new LogicECM.module.Base.Resizer('WorkGroupsResizer');

            resizer.setOptions({
                initialWidth: 500
            });
        }
        YAHOO.util.Event.onDOMReady(init);
    //]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage showToolbar=false>
	<div class="yui-t1" id="orgstructure-work-groups">
		<div id="yui-main">
			<div class="yui-b" id="alf-content">
				<@region id="workforces-toolbar" scope="template" />
				<@region id="workforces-grid" scope="template" />
			</div>
		</div>
		<div id="alf-filters">
			<@region id="groups-toolbar" scope="template" />
			<@region id="groups-grid" scope="template" />
		</div>
	</div>
</@bpage.basePage>
