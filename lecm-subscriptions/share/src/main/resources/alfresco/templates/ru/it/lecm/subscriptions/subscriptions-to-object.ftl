<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
	<script type="text/javascript">//<![CDATA[

	if (typeof LogicECM == "undefined" || !LogicECM) {
	    var LogicECM = {};
	}
	LogicECM.module = LogicECM.module || {};
	LogicECM.module.Subscriptions = LogicECM.module.Subscriptions || {};

	LogicECM.module.Subscriptions.IS_ENGINEER = ${isEngineer?string};
	//]]>
	</script>
	<#include "/org/alfresco/components/form/form.get.head.ftl">
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
