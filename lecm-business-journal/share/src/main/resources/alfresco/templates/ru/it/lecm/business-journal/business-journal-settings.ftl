<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader>
	<script type="text/javascript">//<![CDATA[
		var bjSettings = ${bjSettings};

	    if (typeof LogicECM == "undefined" || !LogicECM) {
	        var LogicECM = {};
	    }
		LogicECM.module = LogicECM.module || {};
		LogicECM.module.BusinessJournal = LogicECM.module.BusinessJournal || {};
		LogicECM.module.BusinessJournal.ARCHIVER_SETTINGS_REF = LogicECM.module.BusinessJournal.CONTAINER || bjSettings.nodeRef;
		//]]>
	</script>
	<#include "/org/alfresco/components/form/form.get.head.ftl">
	<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="business-journal-settings" scope="template" />
</@bpage.basePage>
