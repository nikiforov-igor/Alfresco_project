<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-arm/arm-filters.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"></@script>

	<@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/treeview/assets/skins/sam/treeview.css"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-arm/lecm-arm-documents.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/light-blue-bgr.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/search/search.css" />

	<#include "/org/alfresco/components/form/form.dependencies.inc">

	<#assign filter = ""/>
	<#assign formId = ""/>
	<#assign isDocListPage = false/>
	<#assign queryFilterId = ""/>
<script type="text/javascript">//<![CDATA[
// настройки из repo
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.currentUser = <#if currentUser?? >'${currentUser}'<#else>null</#if>;

LogicECM.module.ARM = LogicECM.module.ARM || {};

LogicECM.module.ARM.SETTINGS =
	<#if settings?? >
	${settings}
	<#else>
	{}
	</#if>;

LogicECM.module.ARM.SETTINGS.ARM_CODE = "${code!''}";
LogicECM.module.ARM.SETTINGS.ARM_PATH = ${path!'{}'};
LogicECM.module.ARM.SETTINGS.ARM_TYPE = "USER";
//]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#import "/ru/it/lecm/base-share/components/2-panels-with-splitter.ftl" as panels/>

<div id="no_menu_page" class="sticky-wrapper">
<@bpage.basePage showHeader=true showTitle=true showToolbar=false showMenu=false>
	<div class="yui-t1" id="arm-with-tree">
		<@panels.twoPanels initialWidth=300 leftRegions=["documents-tree"]>
			<div id="arm-documents-html" class="hidden1">
				<@region id="arm-html-node" scope="template" />
			</div>
		</@panels.twoPanels>
	</div>
	<@region id="dependencies" scope="template" />
</@bpage.basePage>
</div>
