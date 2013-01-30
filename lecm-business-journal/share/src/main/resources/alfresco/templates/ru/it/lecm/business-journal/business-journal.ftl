<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader>
	<script type="text/javascript">//<![CDATA[
		var bjContainer = ${bjContainer};

	    if (typeof LogicECM == "undefined" || !LogicECM) {
	        var LogicECM = {};
	    }
		LogicECM.module = LogicECM.module || {};
		LogicECM.module.BusinessJournal = LogicECM.module.BusinessJournal || {};
		LogicECM.module.BusinessJournal.CONTAINER = LogicECM.module.BusinessJournal.CONTAINER || bjContainer.nodeRef;

        LogicECM.module.BusinessJournal.IS_ENGINEER = ${isEngineer?string};
		//]]>
	</script>
	<#include "/org/alfresco/components/form/form.get.head.ftl">
	<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#assign hasPermission = isEngineer/>
<@bpage.basePage>
	<#if hasPermission>
		<@region id="records-grid" scope="template" />
	<#else>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePage>
