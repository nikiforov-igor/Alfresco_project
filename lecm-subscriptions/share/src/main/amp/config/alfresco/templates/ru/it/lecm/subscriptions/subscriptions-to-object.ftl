<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
	<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"/>
	<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>
<#--загрузка base-utils.js вынесена в base-share-config-custom.xml-->
	<#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>-->
	<script type="text/javascript">//<![CDATA[
	if (typeof LogicECM == "undefined" || !LogicECM) {
	    var LogicECM = {};
	}
	LogicECM.module = LogicECM.module || {};
	LogicECM.module.Subscriptions = LogicECM.module.Subscriptions || {};
	(function () {
		//TODO:
		LogicECM.module.Subscriptions.IS_ENGINEER = ${isEngineer?string};
	})();
	//]]>
	</script>
	<#include "/org/alfresco/components/form/form.dependencies.inc">
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<#assign hasPermission = isEngineer/>
		<#if hasPermission>
			<@region id="to-object-grid" scope="template" />
		<#else>
			<@region id="forbidden" scope="template"/>
		</#if>
</@bpage.basePage>
